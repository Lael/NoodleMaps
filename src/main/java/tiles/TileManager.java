package tiles;

import autocorrect.Trie;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.Downloader;
import data.DrawWay;
import data.FileEater;
import data.MapsDB;
import location.BoundingBox;
import location.LatLon;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * This class allows one to keep a roster of which tiles have been fetched already: we keep hold of which data has been
 * fetched and which tiles have been generated.
 */
public class TileManager {

    private Set<Long> downloadedIds = Sets.newHashSet();
    private Set<Long> drawnIds = Sets.newHashSet();

    private FileEater fileEater;
    private Trie trie;
    private MapsDB mapsDB;

    public TileManager(Connection connection, Trie trie) throws SQLException {
        this.fileEater = new FileEater(connection, trie);
        this.trie = trie;
        this.mapsDB = new MapsDB(connection);
        // check tile and xml directories
        // for each file in the directory
        // parse the id from the filename
    }

    private boolean isDownloaded(long id) {
        return downloadedIds.contains(id);
    }

    private boolean isDrawn(long id) {
        return drawnIds.contains(id);
    }

    private void addDownloaded(long id) {
        downloadedIds.add(id);
    }

    private void addDrawn(long id) {
        drawnIds.add(id);
    }

    public List<DrawWay> fetchTileData(Tile tile) {
        Long id = tile.getId();
        if (!isDownloaded(id)) {
            // download it
            BoundingBox bigBox = tile.getBigBox();
            try {
                File dl = Downloader.downloadBox(bigBox, id);
                fileEater.consumeXml(new FileInputStream(dl));
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
                return null;
            }
            // add all smaller tiles
            for (int z = 0; z < Tile.MAX_ZOOM; z++) {
                int gridSize = (int) Math.pow(2, Tile.MAX_ZOOM - z);
                for (int x = 0; x < gridSize; x++) {
                    for (int y = 0; y < gridSize; y++) {
                        double lat = bigBox.getW() + x * Tile.getTileSize(z);
                        double lon = bigBox.getS() + x * Tile.getTileSize(z);
                        Tile t = new Tile(z, new LatLon(lat, lon));
                        addDownloaded(t.getId());
                    }
                }
            }
        }

        List<DrawWay> drawWays = Lists.newArrayList();

        if (!isDrawn(id)) {
            // draw it
            System.out.println("Drawing a tile!");
            drawWays = mapsDB.fillTile(tile);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String drawWaysJson = gson.toJson(drawWays);
            System.out.println("drawWaysJson = " + drawWaysJson);
            // add it
            addDrawn(id);
        } else {

        }


        // return drawn tile JSON
        return drawWays;
    }
}
