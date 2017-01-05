package tiles;

import com.google.common.collect.Maps;
import location.BoundingBox;
import location.LatLon;

import java.util.Map;

public class Tile {
    public static final int MAX_ZOOM = 3;
    public static final double BASE_SIZE = 0.0125;
    private int zoom;
    private BoundingBox box;
    private long id;


    public Tile(int zoom, LatLon latLon) {
        if (zoom < 0 || zoom > MAX_ZOOM) {
            throw new IllegalArgumentException("Zoom level invalid!");
        }

        double size = getTileSize(zoom);
        double lat = roundDown(latLon.getLat(), size);
        double lon = roundDown(latLon.getLon(), size);

        this.zoom = zoom;
        this.box = new BoundingBox(new LatLon(lat, lon), size);
        this.id = makeID(lat, lon, zoom);
    }

    private static long makeID(double lat, double lon, int zoom) {
        int x = (int) (lat / zoom);
        int y = (int) (lon / zoom);

        assert(Math.abs(x) <= 1 << 15);
        assert(Math.abs(x) <= 1 << 15);
        assert(Math.abs(zoom) <= 1 << 2);

        Long id = 0L;
        id += zoom;
        id += ((long) x) << 2;
        id += ((long) y) << 18;

        return id;
    }

    public static double getTileSize(int zoom) {
        return Math.pow(2, zoom) * BASE_SIZE;
    }

    private double roundDown(double val, double size) {
        return Math.floor((val / size)) * size;
    }

    public int getZoom() {
        return zoom;
    }

    public BoundingBox getBox() {
        return box;
    }

    public BoundingBox getBigBox() {
        double maxSize = getTileSize(MAX_ZOOM);
        double lat = roundDown(box.getS(), maxSize);
        double lon = roundDown(box.getW(), maxSize);
        return new BoundingBox(new LatLon(lat, lon), maxSize);
    }

    public long getId() {
        return id;
    }
}
