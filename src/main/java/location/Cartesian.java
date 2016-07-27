package location;

public abstract class Cartesian {
    private final double[] coords;

    public Cartesian(double... coords) {
        this.coords = coords;
    }

    public int getDimension() {
        return coords.length;
    }

    public double getCoord(int axis) {
        if (axis < 0 || axis >= coords.length) {
            throw new IllegalArgumentException("Axis must be positive and smaller than the dimension of the Cartesian");
        }
        return coords[axis];
    }

    public double distanceTo(Cartesian other) {
        if (other.getDimension() != getDimension()) {
            return 0.0;
        }

        double sqDist = 0.0;
        for (int i = 0; i < getDimension(); i++) {
            final double loc = getCoord(i) - other.getCoord(i);
            sqDist += loc * loc;
        }

        return Math.sqrt(sqDist);
    }
}
