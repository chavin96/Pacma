package pacman.view.observer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pacman.model.engine.GameEngine;

import java.util.List;

/**
 * Observer for tracking and updating Pac-Man lives in the UI.
 */
public class LivesObserver implements Observer {
    private final GameEngine model;
    private final List<ImageView> livesImages;

    public LivesObserver(GameEngine model, List<ImageView> livesImages) {
        this.model = model;
        this.livesImages = livesImages;
    }

    @Override
    public void update() {
        int currentLives = model.getNumLives();

        // First, remove all lives from the pane
        for (ImageView lifeImage : livesImages) {
            lifeImage.setVisible(false);
        }

        // Then, show the correct number of lives based on the model
        for (int i = 0; i < currentLives; i++) {
            livesImages.get(i).setVisible(true);
        }
    }
}
