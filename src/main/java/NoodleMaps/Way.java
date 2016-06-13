package NoodleMaps;

import Data.XMLTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents OSM's Ways, with a subset of their tags.
 * Created by laelcosta on 6/12/16.
 */
class Way {

    private String id;
    private List<String> nodeIds;
    private int numNodes;
    private String highwayType = "";
    private String name = "";
    private boolean oneWay = false;
    private int length = -1;

    Way(XMLTag tag) {
        id = tag.getDatum("id");
        int i = 0;
        nodeIds = new ArrayList<String>();
        XMLTag child = null;
        while ((child = tag.getChild(i)) != null) {
            if (child.getType().equals("nd")) {
                nodeIds.add(child.getDatum("id"));
                numNodes ++;
            } else if (child.getType().equals("tag")) {
                String k = child.getDatum("k");
                String v = child.getDatum("v");
                if (k.equals("highway")) {
                    highwayType = v;
                } else if (k.equals("name")) {
                    name = v;
                } else if (k.equals("oneway") && v.equals("yes")) {
                    oneWay = true;
                }
            }
            i ++;
        }
    }

    public int getNumNodes() {
        return numNodes;
    }

    public String getNodeWayEntry(int index) {
        if (index < 0 || index >= numNodes)
            return null;

        return "(\"" + id + "\",\"" + nodeIds.get(index) + "\"," + index + ")";
    }

    public String toString() {
        return "(\"" + id + "\"," + numNodes + ",\"" + highwayType + "\",\"" + name + "\",\"" + oneWay + "\")";
    }
}
