package data;

import location.LatLon;

public class Node {

    private long id;
    private LatLon latLon;

    public Node() {
        this.latLon = new LatLon(0, 0);
    }

    public Node(long id, Double latitude, Double longitude) {
        this.id = id;
        this.latLon = new LatLon(latitude, longitude);
    }

    public Node(Node oldNode) {
        this.id = oldNode.getId();
        this.latLon = new LatLon(oldNode.getLatLon());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LatLon getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLon latLon) {
        this.latLon = latLon;
    }

    public void setLat(double lat) {
        this.latLon.setLat(lat);
    }

    public void setLon(double lon) {
        this.latLon.setLon(lon);
    }

    public String getInsertMessage() {
        String sql = "add into node " +
                "(id, latitude, longitude) " +
                "values " +
                "(" + id + ", " +
                latLon.getLat() + ", " +
                latLon.getLon() + ");";
        return sql;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", latitude=" + latLon.getLat() +
                ", longitude=" + latLon.getLon() +
                '}';
    }
}
