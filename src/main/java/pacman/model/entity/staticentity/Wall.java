package pacman.model.entity.staticentity;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.Vector2D;

public class Wall implements StaticEntity {

    private final BoundingBox boundingBox;
    private final Renderable.Layer layer;

    public Wall(BoundingBox boundingBox, Renderable.Layer layer) {
        this.boundingBox = boundingBox;
        this.layer = layer;
    }

    @Override
    public boolean canPassThrough() {
        return false; // Walls cannot be passed through
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public double getWidth() {
        return boundingBox.getWidth();
    }

    @Override
    public double getHeight() {
        return boundingBox.getHeight();
    }

    @Override
    public void reset() {
        // Walls don't need to be reset
    }

    @Override
    public javafx.scene.image.Image getImage() {
        // Return an image for the wall, or null if no visual representation is needed
        return null;
    }

    @Override
    public Vector2D getPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPosition'");
    }
}
