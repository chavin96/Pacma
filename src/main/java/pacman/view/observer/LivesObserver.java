package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import pacman.model.engine.GameEngine;

/**
 * Observer to update the number of lives on the UI.
 */
public class LivesObserver implements Observer {
    private final GameEngine gameEngine;
    private final Label livesLabel;

    public LivesObserver(GameEngine gameEngine, Label livesLabel) {
        this.gameEngine = gameEngine;
        this.livesLabel = livesLabel;
    }

    @Override
    public void update() {
        int lives = gameEngine.getNumLives();
        // Use Platform.runLater to ensure the UI is updated on the JavaFX Application Thread
        Platform.runLater(() -> livesLabel.setText("Lives: " + lives));
    }
}
