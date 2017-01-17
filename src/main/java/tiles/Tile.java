package tiles;

import location.BoundingBox;
import location.LatLon;

public class Tile {
    private static final int TILES_PER_DEGREE = 10;
    private BoundingBox box;
    private String id;

    public Tile(LatLon latLon) {
        double lat = roundDown(latLon.getLat());
        double lon = roundDown(latLon.getLon());

        this.box = new BoundingBox(new LatLon(lat, lon), 1.0 / TILES_PER_DEGREE);
        this.id = makeID(lat, lon);
    }

    private static String makeID(double lat, double lon) {
        Integer x = (int) (lat * TILES_PER_DEGREE);
        Integer y = (int) (lon * TILES_PER_DEGREE);

        assert(Math.abs(x) <= 1 << 15);
        assert(Math.abs(y) <= 1 << 15);

        return x.toString() + "_" + y.toString();
    }

    private double roundDown(double val) {
        return Math.floor((val * TILES_PER_DEGREE)) / TILES_PER_DEGREE;
    }

    public BoundingBox getBox() {
        return box;
    }

    public String getId() {
        return id;
    }
}
