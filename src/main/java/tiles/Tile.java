package tiles;

import location.BoundingBox;
import location.LatLon;

public class Tile {
    private static final int MAX_ZOOM = 3;
    private static final double BASE_SIZE = 0.0125;
    private int zoom;
    private BoundingBox box;

    public Tile(int zoom, LatLon latLon) {
        if (zoom < 0 || zoom > MAX_ZOOM) {
            throw new IllegalArgumentException("Zoom level invalid!");
        }

        double size = getTileSize(zoom);
        double lat = roundDown(latLon.getLat(), size);
        double lon = roundDown(latLon.getLon(), size);

        this.zoom = zoom;
        this.box = new BoundingBox(new LatLon(lat, lon), size);
    }

    private double getTileSize(int zoom) {
        return Math.pow(2, zoom) * BASE_SIZE;
    }

    private double roundDown(double val, double size) {
        return Math.floor((val / size)) * size;
    }

    public static int getMaxZoom() {
        return MAX_ZOOM;
    }

    public static double getBaseSize() {
        return BASE_SIZE;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public BoundingBox getBox() {
        return box;
    }

    public void setBox(BoundingBox box) {
        this.box = box;
    }
}
