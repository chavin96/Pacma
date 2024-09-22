package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import pacman.model.engine.GameEngine;

//Observer to display game status messages.
public class GameStatusObserver implements Observer {
    private final GameEngine gameEngine;
    private final Label statusLabel;

    public GameStatusObserver(GameEngine gameEngine, Label statusLabel) {
        this.gameEngine = gameEngine;
        this.statusLabel = statusLabel;
    }

    @Override
    public void update() {
        String status = gameEngine.getGameStatus();
        Platform.runLater(() -> statusLabel.setText(status));
    }
}
