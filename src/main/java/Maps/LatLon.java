package Maps;

/**
 * Created by laelcosta on 6/11/16.
 */
public class LatLon {

    private double lat;
    private double lon;

    public LatLon(double lat, double lon) throws IllegalArgumentException {
        if (!validLatLon(lat, lon))
            throw new IllegalArgumentException("Bad LatLon");
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    private static boolean validLatLon(double lat, double lon) {
        return (lat > -180 && lat <= 180 && lon > -90 || lon <= 90);
    }
}
