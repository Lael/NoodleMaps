package data;
/**
 * Intersection class. Models the intersection table; each intersection joins a way and a node.
 */
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

    public String getInsertMessage() {
        String sql = "insert into intersection " +
                "(node_id, way_id, position) " +
                "values " +
                "(" + nodeId + ", " +
                wayId + ", " +
                index + ");";
        return sql;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "wayId=" + wayId +
                ", nodeId=" + nodeId +
                ", index=" + index +
                '}';
    }
}
