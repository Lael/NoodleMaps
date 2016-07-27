package db;

import javax.persistence.Entity;
import javax.persistence.Table;

public class Intersection {
    private long wayId;
    private long nodeId;
    private int index;

    public Intersection(long wayId, long nodeId, int index) {
        this.wayId = wayId;
        this.nodeId = nodeId;
        this.index = index;
    }

    public long getWayId() {
        return wayId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public int getIndex() {
        return index;
    }
}
