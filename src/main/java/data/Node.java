package data;

import location.LatLon;

public class Node {

    private long id;

    private Double latitude;

    private Double longitude;

    public Node() {
    }

    public Node(long id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLon getLatLon() {
        return new LatLon(latitude, longitude);
    }

    public String toString() {
        return "(\"" + id + "\"," + latitude + "," + longitude + ")";
    }
}
