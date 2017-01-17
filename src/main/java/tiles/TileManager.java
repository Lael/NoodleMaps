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

    private Set<String> downloadedIds = Sets.newHashSet();
    private Set<String> drawnIds = Sets.newHashSet();

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

    private boolean isDownloaded(String id) {
        return downloadedIds.contains(id);
    }

    private boolean isDrawn(String id) {
        return drawnIds.contains(id);
    }

    private void addDownloaded(String id) {
        downloadedIds.add(id);
    }

    private void addDrawn(String id) {
        drawnIds.add(id);
    }

    private void inscribe(String id, String data) throws Exception {
        String fileName = "maps_data/tiles/t" + id + ".json";
        PrintWriter out = new PrintWriter(fileName);
        out.println(data);
    }

    public TileData fetchTileData(Tile tile) {
        String id = tile.getId();
        if (!isDownloaded(id)) {
            // download it
            BoundingBox bigBox = tile.getBox();
            try {
                File dl = Downloader.downloadBox(bigBox, id);
                fileEater.consumeXml(new FileInputStream(dl));
                addDownloaded(id);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        TileData result;

        if (!isDrawn(id)) {
            // draw it
            System.out.println("Drawing a tile!");
            result = new TileData(id, mapsDB.fillTile(tile));

            String drawWaysJson = gson.toJson(result);
            // add it
            try {
                inscribe(id, drawWaysJson);
                addDrawn(id);
            } catch (Exception e) {
                System.out.println("Possibly failed to write tile JSON!");
                return new TileData(id, Lists.newArrayList());
            }
        } else {
            try {
                result = gson.fromJson(new String(Files.readAllBytes(Paths.get("maps_data/tiles/t" + id + ".json"))), TileData.class);
            } catch (Exception e) {
                System.out.println("Could not read!");
                return new TileData(id, Lists.newArrayList());
            }
        }


        // return drawn tile JSON
        return result;
    }
}
