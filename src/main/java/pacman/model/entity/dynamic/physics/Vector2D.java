package pacman.model.entity.dynamic.physics;

/**
 * Utility object for 2D coordinates.
 * <p>
 * All state is immutable.
 */
public class Vector2D {

    public static final Vector2D ZERO = new Vector2D(0, 0);
    private final double x;
    private final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.getX(), this.y + v.getY());
    }

    public boolean isLeftOf(double x) {
        return this.x < x;
    }

    public boolean isRightOf(double x) {
        return this.x > x;
    }

    public boolean isAbove(double y) {
        return this.y < y;
    }

    public boolean isBelow(double y) {
        return this.y > y;
    }

    /**
     * Calculates the Euclidean distance between two vectors.
     * Adds null checks to prevent NullPointerExceptions.
     *
     * @param vector1 The first vector.
     * @param vector2 The second vector.
     * @return The Euclidean distance between the two vectors, or -1 if either vector is null.
     */
    public static double calculateEuclideanDistance(Vector2D vector1, Vector2D vector2) {
        if (vector1 == null || vector2 == null) {
            System.err.println("One of the vectors is null. Cannot calculate Euclidean distance.");
            return -1;  // Alternatively, you could throw an exception or handle it in another way.
        }

        double xDistance = vector2.getX() - vector1.getX();
        double yDistance = vector2.getY() - vector1.getY();
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
