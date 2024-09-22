package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import pacman.model.engine.GameEngine;

/**
 * Observer to update the score on the UI.
 */
public class ScoreObserver implements Observer {
    private final GameEngine gameEngine;
    private final Label scoreLabel;

    public ScoreObserver(GameEngine gameEngine, Label scoreLabel) {
        this.gameEngine = gameEngine;
        this.scoreLabel = scoreLabel;
    }

    @Override
    public void update() {
        int score = gameEngine.getScore();
        Platform.runLater(() -> scoreLabel.setText("Score: " + score));
    }
}

