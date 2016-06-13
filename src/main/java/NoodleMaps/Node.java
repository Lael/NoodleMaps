package NoodleMaps;

import Data.XMLTag;
import Maps.LatLon;

/**
 * Node class
 * Created by laelcosta on 6/12/16.
 */
class Node {

    private String id;
    private LatLon latLon;

    Node(XMLTag tag) {
        id = tag.getDatum("id");
        latLon = new LatLon(Double.parseDouble(tag.getDatum("lat")), Double.parseDouble(tag.getDatum("lon")));
    }

    public String toString() {
        return "(\"" + id + "\"," + latLon.getLat() + "," + latLon.getLon() + ")";
    }
}
