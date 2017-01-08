package data;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    public boolean isOneWay() {
        return oneWay;
    }

    @JsonProperty
    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    @JsonProperty
    public String getBuilding() {
        return building;
    }

    @JsonProperty
    public void setBuilding(String building) {
        this.building = building;
    }

    @JsonProperty
    public String getHighway() {
        return highway;
    }

    @JsonProperty
    public void setHighway(String highway) {
        this.highway = highway;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public LatLon getP1() {
        return p1;
    }

    @JsonProperty
    public void setP1(LatLon p1) {
        this.p1 = p1;
    }

    @JsonProperty
    public LatLon getP2() {
        return p2;
    }

    @JsonProperty
    public void setP2(LatLon p2) {
        this.p2 = p2;
    }
}
