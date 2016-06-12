package NoodleMaps;

import Database.SQLWrapper;
import Maps.BoundingBox;

import java.sql.Connection;

/**
 * For a given bounding box, turns the map_data file into a mySQL database.
 * Created by laelcosta on 6/11/16.
 */
public class DBMaker {

    private Connection conn;

    public DBMaker(String name) {
        conn = SQLWrapper.createNewDB("map_data/", name);
    }

    public boolean makeDB(BoundingBox box) {
        return false;
    }
}
