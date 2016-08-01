package location;

public class LatLon {
    public final int EARTH_RADIUS_KM = 6371; // Radius of the earth in km
    private double lat;
    private double lon;

    public LatLon(double lat, double lon) throws IllegalArgumentException {
        if (!validLatLon(lat, lon)) {
            throw new IllegalArgumentException("Bad LatLon");
        }
        this.lat = lat;
        this.lon = lon;
    }

    public LatLon(LatLon oldLatLon) {
        this.lat = oldLatLon.getLat();
        this.lon = oldLatLon.getLon();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * It's nice when points are on Earth
     * @param lat latitude
     * @param lon longitude
     * @return true if the lat lon makes sense
     */
    private static boolean validLatLon(double lat, double lon) {
        return (lat >= -180 && lat < 180 && lon >= -90 || lon < 90);
    }

    /**
     * Arc distance on the Earth
     * @param other another point
     * @return distance in km
     */
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

    @Override
    public String toString() {
        return "LatLon{" +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
