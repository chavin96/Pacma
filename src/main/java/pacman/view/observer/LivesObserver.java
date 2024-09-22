package pacman.view.observer;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import pacman.model.engine.GameEngine;

//Observer to update the number of lives on the UI using images.
public class LivesObserver implements Observer {
    private final GameEngine gameEngine;
    private final HBox livesBox;

    public LivesObserver(GameEngine gameEngine, HBox livesBox) {
        this.gameEngine = gameEngine;
        this.livesBox = livesBox;
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            livesBox.getChildren().clear();

            addLivesImages(gameEngine.getNumLives());
        });
    }

    private void addLivesImages(int numLives) {
        Image pacmanLifeImage = new Image(getClass().getResource("/maze/pacman/playerRight.png").toExternalForm());

        for (int i = 0; i < numLives; i++) {
            ImageView pacmanLife = new ImageView(pacmanLifeImage);
            pacmanLife.setFitHeight(20);
            pacmanLife.setFitWidth(20);   

            livesBox.getChildren().add(pacmanLife);
        }
    }
}
