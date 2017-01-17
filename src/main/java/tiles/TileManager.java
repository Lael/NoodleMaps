package tiles;

import autocorrect.Trie;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import data.Downloader;
import data.FileEater;
import data.MapsDB;
import location.BoundingBox;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import static noodleMaps.NoodleMapsApplication.MAPS_DIR_NAME;
import static noodleMaps.NoodleMapsApplication.TILES_DIR_NAME;
import static noodleMaps.NoodleMapsApplication.XML_DIR_NAME;

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

        File tileDir = new File(MAPS_DIR_NAME + "/" + TILES_DIR_NAME);
        File xmlDir = new File(MAPS_DIR_NAME + "/" + XML_DIR_NAME);

        File[] tileFiles = tileDir.listFiles();
        File[] dataFiles = xmlDir.listFiles();

        if (dataFiles != null) {
            for (File f : dataFiles) {
                String name = f.getName();
                if (f.isFile() && name.startsWith("x") && name.endsWith(".xml")) {
                    downloadedIds.add(name.substring(1, name.length() - 4));
                }
            }
        }

        if (tileFiles != null) {
            for (File f : tileFiles) {
                String name = f.getName();
                if (f.isFile() && name.startsWith("t") && name.endsWith(".json")) {
                    drawnIds.add(name.substring(1, name.length() - 5));
                }
            }
        }
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

    private void inscribe(String id, TileData data) throws Exception {
        FileOutputStream out = new FileOutputStream("maps_data/tiles/t" + id + ".json");
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(data);
    }

    public TileData fetchTileData(Tile tile) {
        String id = tile.getId();
        System.out.println(id);
        if (!isDownloaded(id)) {
            System.out.println("Downloading id=" + id);
            BoundingBox bigBox = tile.getBox();
            try {
                File dl = Downloader.downloadBox(bigBox, id);
                if (dl != null) {
                    fileEater.consumeXml(new FileInputStream(dl));
                    addDownloaded(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        TileData result;

        if (!isDrawn(id)) {
            System.out.println("Drawing id=" + id);
            // draw it
            System.out.println("Drawing a tile!");
            result = new TileData(id, mapsDB.fillTile(tile));

            try {
                inscribe(id, result);
                addDrawn(id);
            } catch (Exception e) {
                System.out.println("Possibly failed to write tile JSON: ");
                e.printStackTrace();
                return new TileData(id, Lists.newArrayList());
            }
        } else {
            try {
                FileInputStream streamIn = new FileInputStream("maps_data/tiles/t" + id + ".json");
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                result = (TileData) objectinputstream.readObject();
            } catch (Exception e) {
                System.out.println("Could not read: ");
                e.printStackTrace();
                return new TileData(id, Lists.newArrayList());
            }
        }

        return result;
    }
}
