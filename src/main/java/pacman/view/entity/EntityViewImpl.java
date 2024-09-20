package pacman.view.entity;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import pacman.model.entity.Renderable;

/**
 * Concrete implementation of EntityView.
 * This class represents a visual wrapper around the entities in the game.
 */
public class EntityViewImpl implements EntityView {

    private final Renderable entity;   // The entity this view is tied to.
    private boolean delete = false;    // A flag to mark whether this entity should be deleted.
    private final ImageView node;      // The visual representation (node) of the entity.
    private final HBox box;            // The container for the image (allows for further customization).

    public EntityViewImpl(Renderable entity) {
        this.entity = entity;
        box = new HBox();
        node = new ImageView(entity.getImage());
        
        // Set the image for the ImageView
        node.setImage(entity.getImage());
        
        // Add the node to the HBox
        box.getChildren().add(node);
        
        // Set the view order for layering (Z-index)
        box.setViewOrder(getViewOrder(entity.getLayer()));
        
        // Ensure the HBox fits its children properly
        box.setFillHeight(true);
        
        // Update the position and size
        update();
    }

    // Determines the view order based on the entity layer (used for Z-ordering entities)
    private static double getViewOrder(Renderable.Layer layer) {
        return switch (layer) {
            case BACKGROUND -> 100.0;
            case FOREGROUND -> 50.0;
            case EFFECT -> 25.0;
            case INVISIBLE -> 0.0;
        };
    }

    @Override
    public void update() {
        // Only update the view if the entity is visible
        if (entity.getLayer() != Renderable.Layer.INVISIBLE) {
            node.setVisible(true);

            // Check if the image has changed
            if (!node.getImage().equals(entity.getImage())) {
                node.setImage(entity.getImage());
            }

            // Update the position of the entity on the screen
            box.setLayoutX(entity.getPosition().getX());
            box.setLayoutY(entity.getPosition().getY());

            // Update the size (width/height) of the ImageView
            node.setFitHeight(entity.getHeight());
            node.setFitWidth(entity.getWidth());
            node.setPreserveRatio(true);  // Preserve aspect ratio of the image
        } else {
            // If the entity is invisible, hide the node
            node.setVisible(false);
        }

        // Reset the deletion flag
        delete = false;
    }

    @Override
    public boolean matchesEntity(Renderable entity) {
        // Check if the provided entity matches this entity
        return this.entity.equals(entity);
    }

    @Override
    public void markForDelete() {
        // Mark this entity for deletion
        delete = true;
    }

    @Override
    public Node getNode() {
        // Return the node that represents this entity (the HBox containing the ImageView)
        return box;
    }

    @Override
    public boolean isMarkedForDelete() {
        // Check if this entity is marked for deletion
        return delete;
    }
}
