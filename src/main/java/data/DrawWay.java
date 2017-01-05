package data;

import location.LatLon;

public class DrawWay {
    private boolean oneWay = false;
    private String building = "";
    private String highway = "";
    private String name = "";
    private LatLon p1;
    private LatLon p2;

    public DrawWay(boolean oneWay, String building, String highway, String name, LatLon p1, LatLon p2) {
        this.oneWay = oneWay;
        this.building = building;
        this.highway = highway;
        this.name = name;
        this.p1 = p1;
        this.p2 = p2;
    }
}
