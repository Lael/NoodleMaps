package data;

/**
 * Way class. Models the way table; each way is derived from an OSM entry.
 */
public class Way {
    private long id = -1;
    private boolean closed = false;
    private boolean oneWay = false;
    private String landuse = "";
    private String building = "";
    private String highway = "";
    private String name = "";
    private int numNodes;
    private double length = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public String getLanduse() {
        return landuse;
    }

    public void setLanduse(String landuse) {
        this.landuse = landuse;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getHighway() {
        return highway;
    }

    public void setHighway(String highway) {
        this.highway = highway;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public String getInsertMessage() {
        String sql = "insert into way " +
                "(id, num_nodes, highway_type, street_name, land_use, building, closed, one_way, length) " +
                "values " +
                "(" + id + ", " +
                numNodes + ", '" +
                highway + "', '" +
                name + "', '" +
                landuse + "', '" +
                building + "', " +
                ((closed) ? 1 : 0) + ", " +
                ((oneWay) ? 1 : 0) + ", " +
                length + ");";
        return sql;
    }

    @Override
    public String toString() {
        return "Way{" +
                "id=" + id +
                ", closed=" + closed +
                ", oneWay=" + oneWay +
                ", landuse='" + landuse + '\'' +
                ", building='" + building + '\'' +
                ", highway='" + highway + '\'' +
                ", name='" + name + '\'' +
                ", numNodes=" + numNodes +
                '}';
    }
}
