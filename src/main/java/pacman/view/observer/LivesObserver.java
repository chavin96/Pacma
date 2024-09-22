package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import pacman.model.engine.GameEngine;

/**
 * Observer to update the number of lives on the UI.
 */
public class LivesObserver implements Observer {
    private final GameEngine gameEngine;
    private final Label livesLabel;  // Reference to the lives label

    public LivesObserver(GameEngine gameEngine, Label livesLabel) {
        this.gameEngine = gameEngine;
        this.livesLabel = livesLabel;
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            // Update the lives label with the current number of lives
            livesLabel.setText(generateLivesText(gameEngine.getNumLives()));
        });
    }

    // Method to generate the "X" representation for lives
    private String generateLivesText(int numLives) {
        StringBuilder livesText = new StringBuilder();
        for (int i = 0; i < numLives; i++) {
            livesText.append("X");

            // Add spacing between each "X" except the last one
            if (i < numLives - 1) {
                livesText.append("  ");  // Two spaces for ~4px padding
            }
        }
        return livesText.toString();
    }
}
