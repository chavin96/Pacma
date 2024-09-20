package pacman.model.entity.dynamic.physics;

import pacman.model.entity.staticentity.WallEntity;

public interface BoundingBox {
    double getWidth();
    double getHeight();
    double getLeftX();
    double getMiddleX();
    double getRightX();
    double getTopY();
    double getMiddleY();
    double getBottomY();
    void setTopLeft(Vector2D topLeft);
    boolean collidesWith(Direction direction, BoundingBox box);
    boolean containsPoint(Vector2D point);

    // Add this method to the interface
    Vector2D getPosition();
    default void setTopLeft(WallEntity wallEntity, Vector2D topLeft) {
        setTopLeft(topLeft);
    }
}
