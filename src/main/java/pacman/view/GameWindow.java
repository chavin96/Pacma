package pacman.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import pacman.model.engine.GameEngine;
import pacman.model.entity.Renderable;
import pacman.view.background.BackgroundDrawer;
import pacman.view.background.StandardBackgroundDrawer;
import pacman.view.entity.EntityView;
import pacman.view.entity.EntityViewImpl;
import pacman.view.keyboard.KeyboardInputHandler;
import pacman.view.observer.LivesObserver;
import pacman.view.observer.ScoreObserver;
import pacman.view.observer.GameStatusObserver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for managing the Pac-Man Game View
 */
public class GameWindow {

    private final Scene scene;
    private final Pane pane;
    private final GameEngine model;
    private Timeline timeline;
    private final List<EntityView> entityViews;
    private List<ImageView> livesImages; // To track Pac-Man lives images

    public GameWindow(GameEngine model, int width, int height) {
        this.model = model;

        pane = new Pane();
        scene = new Scene(pane, width, height);

        entityViews = new ArrayList<>();
        livesImages = new ArrayList<>();  // Initialize lives image tracking

        // Initialize the KeyboardInputHandler with the Command pattern
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);
        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);

        // Draw the background
        BackgroundDrawer backgroundDrawer = new StandardBackgroundDrawer();
        backgroundDrawer.draw(model, pane);

        // Add Pac-Man lives at the top left corner (ensure this is done first)
        addPacmanLives();  
        
        // Register the observer for lives AFTER the livesImages list has been populated
        LivesObserver livesObserver = new LivesObserver(model, livesImages);
        model.registerObserver(livesObserver);

        // Add a label for score and register it as an observer
        Label scoreLabel = new Label("Score: 0"); // Initial placeholder
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        pane.getChildren().add(scoreLabel);
        ScoreObserver scoreObserver = new ScoreObserver(model, scoreLabel);
        model.registerObserver(scoreObserver);

        // Add a label for game status and register it as an observer
        Label statusLabel = new Label("READY!");
        statusLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 36px;");
        statusLabel.setLayoutX((width / 2.0) - 50); // Center the "READY!" text
        statusLabel.setLayoutY((height / 2.0) - 50);
        pane.getChildren().add(statusLabel);
        GameStatusObserver statusObserver = new GameStatusObserver(model, statusLabel);
        model.registerObserver(statusObserver);
    }

    public Scene getScene() {
        return scene;
    }

    public void run() {
        // Initialize timeline and start the game loop
        timeline = new Timeline(new KeyFrame(Duration.millis(34), t -> this.draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        model.startGame();
    }

    private void draw() {
        model.tick();

        // Check if the game is over
        if (model.getGameStatus().equals("GAME OVER")) {
            Label gameOverLabel = new Label("GAME OVER!");
            gameOverLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: red;");
            gameOverLabel.setLayoutX(pane.getWidth() / 2 - 100);
            gameOverLabel.setLayoutY(pane.getHeight() / 2);
            pane.getChildren().add(gameOverLabel);

            // Stop the game loop by stopping the Timeline
            timeline.stop();  // Now this will work correctly
            return;
        }

        // Continue updating the game view
        List<Renderable> entities = model.getRenderables();
        for (EntityView entityView : entityViews) {
            entityView.markForDelete();
        }
        for (Renderable entity : entities) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update();
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

    // Helper method to add Pac-Man lives icons to the UI
    private void addPacmanLives() {
        System.out.println("addPacmanLives called.");
        
        HBox livesContainer = new HBox(10); // Container for the lives icons
        livesContainer.setLayoutX(10);
        livesContainer.setLayoutY(10);

        try {
            for (int i = 0; i < model.getNumLives(); i++) {
                ImageView pacmanLife = new ImageView(loadImage("/maze/pacman/playerRight.png")); // Load Pacman life image
                pacmanLife.setFitWidth(20);
                pacmanLife.setFitHeight(20);
                livesImages.add(pacmanLife); // Track lives
                livesContainer.getChildren().add(pacmanLife); // Add each image to the container
            }

            pane.getChildren().add(livesContainer); // Add the container to the pane

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to load images
    private Image loadImage(String path) {
        InputStream imageStream = getClass().getResourceAsStream(path);
        if (imageStream == null) {
            throw new RuntimeException("Image not found: " + path);
        }
        return new Image(imageStream);
    }
}
