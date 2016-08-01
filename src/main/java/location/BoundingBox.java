package location;

/**
 * Straightforward bounding box class.
 */
public class BoundingBox {

    private LatLon sw;
    private LatLon ne;

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

    public double getN() {
        return ne.getLon();
    }

    public double getE() {
        return ne.getLat();
    }

    public double getW() {
        return sw.getLat();
    }

    public double getS() {
        return sw.getLon();
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "sw=" + sw.toString() +
                ", ne=" + ne.toString() +
                '}';
    }
}
