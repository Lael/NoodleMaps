package location;

public class LatLon extends Cartesian {
    public final int EARTH_RADIUS_KM = 6371; // Radius of the earth

    public LatLon(double lat, double lon) throws IllegalArgumentException {
        super(lat, lon);
        if (!validLatLon(lat, lon)) {
            throw new IllegalArgumentException("Bad LatLon");
        }
    }

    public double getLat() {
        return this.getCoord(0);
    }

    public double getLon() {
        return this.getCoord(1);
    }

    private static boolean validLatLon(double lat, double lon) {
        return (lat > -180 && lat <= 180 && lon > -90 || lon <= 90);
    }

    public double distanceTo(LatLon other) {
        Double latDistance = Math.toRadians(other.getLat() - this.getLat());
        Double lonDistance = Math.toRadians(other.getLon() - this.getLon());
        Double a = Math.sin(latDistance / 2)
                * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.getLat()))
                * Math.cos(Math.toRadians(this.getLat()))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c * 1000; // convert to meters
    }
}
