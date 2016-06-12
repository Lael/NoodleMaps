package Maps;

import java.util.IllegalFormatCodePointException;

/**
 * Straightforward bounding box class.
 * Created by laelcosta on 6/11/16.
 */
public class BoundingBox {

    private LatLon sw;
    private LatLon ne;

    public BoundingBox(double lat, double lon, double size) throws IllegalArgumentException {
        this.sw = new LatLon(lat, lon);
        this.ne = new LatLon(sw.getLat() + size, sw.getLon() + size);
    }

    public BoundingBox(LatLon sw, double size) throws IllegalArgumentException {
        this.sw = sw;
        this.ne = new LatLon(sw.getLat() + size, sw.getLon() + size);
    }

    public BoundingBox(LatLon sw, LatLon ne) throws IllegalArgumentException {
        this.sw = sw;
        this.ne = ne;
    }

    public LatLon getSw() {
        return sw;
    }

    public LatLon getNe() {
        return ne;
    }

    public LatLon getSe() throws IllegalArgumentException {
        return new LatLon(sw.getLat(), ne.getLon());
    }

    public LatLon getNw() throws IllegalArgumentException {
        return new LatLon(ne.getLat(), sw.getLon());
    }
}
