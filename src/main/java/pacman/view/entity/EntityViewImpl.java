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

    private final Renderable entity;   
    private boolean delete = false;   
    private final ImageView node;      
    private final HBox box;           

    public EntityViewImpl(Renderable entity) {
        this.entity = entity;
        box = new HBox();
        node = new ImageView(entity.getImage());
        
        node.setImage(entity.getImage());
        
        box.getChildren().add(node);
        
        box.setViewOrder(getViewOrder(entity.getLayer()));
        
        box.setFillHeight(true);
        
        update();
    }

    // Determines the view order based on the entity layer (used for Z-ordering entities)
    private static double getViewOrder(Renderable.Layer layer) {
        return switch (layer) {
            case BACKGROUND -> 3.0; 
            case FOREGROUND -> 2.0;  
            case EFFECT -> 1.0;      
            case INVISIBLE -> 4.0;
        };
    }

    @Override
    public void update() {
        if (entity.getLayer() != Renderable.Layer.INVISIBLE) {
            node.setVisible(true);

            if (!node.getImage().equals(entity.getImage())) {
                node.setImage(entity.getImage());
            }

            box.setLayoutX(entity.getPosition().getX());
            box.setLayoutY(entity.getPosition().getY());

            node.setFitHeight(entity.getHeight());
            node.setFitWidth(entity.getWidth());
            node.setPreserveRatio(true);
        } else {
            node.setVisible(false);
        }

        delete = false;
    }

    @Override
    public boolean matchesEntity(Renderable entity) {
        return this.entity.equals(entity);
    }

    @Override
    public void markForDelete() {
        delete = true;
    }

    @Override
    public Node getNode() {
        return box;
    }

    @Override
    public boolean isMarkedForDelete() {
        return delete;
    }

    @Override
    public Renderable getEntity() {
        return entity;
    }
}
