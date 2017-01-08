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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private void inscribe(Long id, String data) throws Exception {
        String fileName = "maps_data/tiles/t" + id.toString() + ".json";
        PrintWriter out = new PrintWriter(fileName);
        out.println(data);
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
                e.printStackTrace();
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

        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        List drawWays;

        if (!isDrawn(id)) {
            // draw it
            System.out.println("Drawing a tile!");
            drawWays = mapsDB.fillTile(tile);

            String drawWaysJson = gson.toJson(drawWays);
            // add it
            try {
                inscribe(id, drawWaysJson);
                addDrawn(id);
            } catch (Exception e) {
                System.out.println("Possibly failed to write tile JSON!");
                return Lists.newArrayList();
            }
        } else {
            try {
                drawWays = gson.fromJson(new String(Files.readAllBytes(Paths.get("maps_data/tiles/t" + id.toString() + ".json"))), List.class);
            } catch (Exception e) {
                System.out.println("Could not read!");
                return Lists.newArrayList();
            }
        }


        // return drawn tile JSON
        return drawWays;
    }
}
