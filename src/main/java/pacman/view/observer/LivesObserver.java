package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import pacman.model.engine.GameEngine;

import java.util.List;

/**
 * Observer to update the number of lives on the UI using ImageView elements.
 */
public class LivesObserver implements Observer {
    private final GameEngine gameEngine;
    private final List<ImageView> livesImages;

    public LivesObserver(GameEngine gameEngine, List<ImageView> livesImages) {
        this.gameEngine = gameEngine;
        this.livesImages = livesImages;
    }

    @Override
    public void update() {
        int numLives = gameEngine.getNumLives();

        Platform.runLater(() -> {
            // Ensure we have enough life images to display
            if (numLives > livesImages.size()) {
                System.out.println("Warning: Num lives exceeds available lives images!");
                return;
            }

            // Update the visibility of each life image based on the number of remaining lives
            for (int i = 0; i < livesImages.size(); i++) {
                livesImages.get(i).setVisible(i < numLives);
            }
        });
    }
}
