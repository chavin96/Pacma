package pacman.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import pacman.model.engine.GameEngine;
import pacman.model.entity.Renderable;
import pacman.view.background.BackgroundDrawer;
import pacman.view.background.StandardBackgroundDrawer;
import pacman.view.entity.EntityView;
import pacman.view.entity.EntityViewImpl;
import pacman.view.keyboard.KeyboardInputHandler;
import pacman.view.observer.GameStatusObserver;
import pacman.view.observer.LivesObserver;
import pacman.view.observer.ScoreObserver;

import java.util.ArrayList;
import java.util.List;

public class GameWindow {

    private static final int READY_DISPLAY_TIME = 100;  // Number of frames to display READY!
    private Label readyLabel;
    private int readyFrames = READY_DISPLAY_TIME;
    private Timeline timeline;
    private Label gameOverLabel;
    private final Pane pane;
    private final Scene scene;
    private final GameEngine model;
    private final List<EntityView> entityViews;

    private final List<ImageView> livesImages;  // For displaying lives as Pac-Man images

    public GameWindow(GameEngine model, int width, int height) {
        this.model = model;
        this.pane = new Pane();
        this.scene = new Scene(pane, width, height);
        this.entityViews = new ArrayList<>();
        this.livesImages = new ArrayList<>();

        // Initialize the KeyboardInputHandler with the Command pattern
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);
        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);

        // Draw the background
        BackgroundDrawer backgroundDrawer = new StandardBackgroundDrawer();
        backgroundDrawer.draw(model, pane);

        // Initialize labels
        initializeLabels();

        // Add lives images (Pac-Man icons)
        addPacmanLives();

        // Register observers
        registerObservers();
    }

    public Scene getScene() {
        return scene;
    }

    public void run() {
        timeline = new Timeline(new KeyFrame(Duration.millis(34), t -> this.draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        model.startGame();
    }

    private void draw() {
        // Delay the start of the game by showing the "READY!" label for 100 frames
        if (readyFrames > 0) {
            readyFrames--;
            return;  // Don't update entities while "READY!" is displayed
        }

        // Remove "READY!" label after 100 frames
        if (readyFrames == 0) {
            pane.getChildren().remove(readyLabel);
            readyFrames = -1;  // Set a flag to stop this condition from repeating
        }

        model.tick();  // Update the game state by advancing one tick

        List<Renderable> entities = model.getRenderables();  // Get all renderables

        // Mark all current entity views for deletion
        for (EntityView entityView : entityViews) {
            entityView.markForDelete();
        }

        // Update the view for each renderable entity
        for (Renderable entity : entities) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update();  // Update the entity view
                    break;
                }
            }

            if (notFound) {
                // Debugging: Make sure we're adding the entity's view to the pane
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());  // Add to the pane
            }
        }

        // Remove all entity views marked for deletion
        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }

        entityViews.removeIf(EntityView::isMarkedForDelete);  // Clean up entity views

        // Check if the game is over
        if (model.getGameStatus().equals("GAME OVER")) {
            showGameOver();
            timeline.stop();  // Stop the game loop by stopping the Timeline
            return;
        }
    }

    // Initialize "READY!" label, score label, etc.
    private void initializeLabels() {
        // Create the "READY!" label
        readyLabel = new Label("READY!");
        readyLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: yellow;");
        readyLabel.setLayoutX(pane.getWidth() / 2 - 50); // Center the label
        readyLabel.setLayoutY(pane.getHeight() / 2);     // Position near the bottom
        pane.getChildren().add(readyLabel);  // Add to the pane

        // "Game Over" label (hidden by default)
        gameOverLabel = new Label("GAME OVER!");
        gameOverLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: red;");
        gameOverLabel.setLayoutX(pane.getWidth() / 2 - 100);
        gameOverLabel.setLayoutY(pane.getHeight() / 2);
    }

    // Show "GAME OVER" when the player loses all lives
    private void showGameOver() {
        pane.getChildren().add(gameOverLabel);
    }

    // Add Pac-Man lives as icons
    private void addPacmanLives() {
        System.out.println("addPacmanLives called.");
        for (int i = 0; i < model.getNumLives(); i++) {
            ImageView pacmanLife = new ImageView(getClass().getResource("/maze/pacman/playerRight.png").toExternalForm());  // Load Pacman life image
            pacmanLife.setFitHeight(20);  // Resize the image as needed
            pacmanLife.setFitWidth(20);
            pacmanLife.setLayoutX(20 + (i * 30));  // Set position for each Pac-Man icon
            pacmanLife.setLayoutY(10);  // Display at the top of the screen
            pane.getChildren().add(pacmanLife);  // Add to the pane
            livesImages.add(pacmanLife);  // Store the reference for updating
        }
    }
    

    // Register observers for lives, score, and game status
    private void registerObservers() {
        // Add a label for lives and register it as an observer
        LivesObserver livesObserver = new LivesObserver(model, livesImages);
        model.registerObserver(livesObserver);

        // Add a label for score and register it as an observer
        Label scoreLabel = new Label("Score: 0");  // Initial placeholder
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.setLayoutX(10);  // Position at the top-left corner
        scoreLabel.setLayoutY(10);
        pane.getChildren().add(scoreLabel);
        ScoreObserver scoreObserver = new ScoreObserver(model, scoreLabel);
        model.registerObserver(scoreObserver);

        // Add a label for game status and register it as an observer
        Label statusLabel = new Label("READY!");  // Initial placeholder
        statusLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: yellow;");
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(40);
        pane.getChildren().add(statusLabel);
        GameStatusObserver statusObserver = new GameStatusObserver(model, statusLabel);
        model.registerObserver(statusObserver);
    }

    // Utility method to load images from resources
    private ImageView loadImage(String resourcePath) {
        return new ImageView(getClass().getResource(resourcePath).toExternalForm());
    }

    // Reset "READY!" timer when starting a new level or after a life is lost
    public void resetReadyFrames() {
        readyFrames = READY_DISPLAY_TIME;
        pane.getChildren().add(readyLabel);  // Re-add the label to the pane
    }
}
