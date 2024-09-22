package pacman.model.entity.staticentity;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.Vector2D;

/**
 * Represents a Wall entity in the Pac-Man game.
 */
public class WallEntity implements StaticEntity {

    public final BoundingBox boundingBox;
    private final Image image;

    public WallEntity(BoundingBox boundingBox, Image image) {
        this.boundingBox = boundingBox;
        this.image = image;
    }

    @Override
    public Vector2D getPosition() {
        return this.boundingBox.getPosition();
    }

    public Image getImage() {
        return this.image;
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
    public Renderable.Layer getLayer() {
        return Renderable.Layer.BACKGROUND;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean canPassThrough() {
        return false; 
    }

    @Override
    public void reset() {
        
    }
}
