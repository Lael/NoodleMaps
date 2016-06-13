package NoodleMaps;

import Data.SQLWrapper;
import Data.XMLReader;
import Data.XMLTag;
import Maps.BoundingBox;

import java.io.FileNotFoundException;
import java.sql.Connection;

/**
 * For a given bounding box, turns the map_data file into a mySQL database.
 * Created by laelcosta on 6/11/16.
 */
class DBMaker {

    private Connection conn;

    DBMaker(String name) {
        conn = SQLWrapper.createNewDB("map_data/", name);
    }

    boolean makeDB(BoundingBox box) {

        String nodePattern, wayPattern, nodeWayPattern;
        nodePattern = "(id TEXT, lat FLOAT, lon FLOAT) PRIMARY KEY id";

        String fileName = "map_data/map_data" + Main.boxToString(box) + ".xml";

        XMLReader reader = null;
        try {
            reader = new XMLReader(fileName);
        } catch (FileNotFoundException e) {
            Main.fatalError("File not found. This should certainly not happen.");
        }

        XMLTag tag = reader.getTagOfName("node");
        if (tag == null) {
            Main.fatalError("No nodes.");
        }

        SQLWrapper.createTable(conn, "NODES");

        while (tag != null && tag.getType().equals("node")) {
            /* add node to database */
            Node node = new Node(tag);
            SQLWrapper.insertItem(conn, "NODES", node);

            tag = reader.getTag();
        }

        if (tag == null || !tag.getType().equals("way")) {
            Main.fatalError("No ways.");
        }

        SQLWrapper.createTable(conn, "WAYS");
        SQLWrapper.createTable(conn, "NODEWAYS");

        while (tag != null && tag.getType().equals("way")) {
            /* add way to database */
            Way way = new Way(tag);
            SQLWrapper.insertItem(conn, "WAYS", way);
            int numNodes = way.getNumNodes();

            String s;
            for (int i = 0; i < numNodes; i ++) {
                s = way.getNodeWayEntry(i);
                SQLWrapper.insertItem(conn, "NODEWAYS", s);
            }
            tag = reader.getTag();
        }

        return false;
    }
}
