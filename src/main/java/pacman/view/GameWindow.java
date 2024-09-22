package pacman.view;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import pacman.model.engine.GameEngine;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.Ghost;
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
        // Load all entities but freeze the game with the "READY!" label for 100 frames
        if (readyFrames > 0) {
            List<Renderable> entities = model.getRenderables();  // Get all renderables
    
            // Ensure all entity views are drawn and visible, even if the game is paused
            for (Renderable entity : entities) {
                boolean notFound = true;
                for (EntityView view : entityViews) {
                    if (view.matchesEntity(entity)) {
                        notFound = false;
                        view.update();  // Just update the view (but no movement/logic)
                        break;
                    }
                }
    
                if (notFound) {
                    // Add entity views to the pane
                    EntityView entityView = new EntityViewImpl(entity);
                    entityViews.add(entityView);
                    pane.getChildren().add(entityView.getNode());
                }
            }
    
            // Decrease the "readyFrames" counter
            readyFrames--;
    
            return;  // Don't update the game entities or logic while "READY!" is displayed
        }
    
        // Remove "READY!" label after 100 frames and start the game
        if (readyFrames == 0) {
            pane.getChildren().remove(readyLabel);  // Remove the READY! label
            readyFrames = -1;  // Prevent this condition from repeating
        }

        if (model.getGameStatus().equals("YOU WIN!")) {
            showWinMessage();
            timeline.stop();  // Stop the game loop by stopping the Timeline
            endGameAfterDelay();  // End the game after 5 seconds
            return;
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
                // Add the entity's view to the pane
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
            removeGhostEntities();
            showGameOver();
            timeline.stop();  // Stop the game loop by stopping the Timeline
            endGameAfterDelay();  // End the game after 5 seconds
            return;
        }
    }

    // Initialize "READY!" label, score label, etc.
    private void initializeLabels() {
        // Create the "READY!" label
        readyLabel = new Label("READY!");
        readyLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: yellow;");
        readyLabel.setLayoutX(pane.getWidth() / 2 - 35); // Center the label
        readyLabel.setLayoutY(pane.getHeight() / 2 + 28);     // Position near the bottom
        pane.getChildren().add(readyLabel);  // Add to the pane

        // "Game Over" label (hidden by default)
        gameOverLabel = new Label("GAME OVER!");
        gameOverLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
        gameOverLabel.setLayoutX(pane.getWidth() / 2 - 57);
        gameOverLabel.setLayoutY(pane.getHeight() / 2 + 28);
    }

    private void showWinMessage() {
        removeGhostEntities();
        Label winLabel = new Label("YOU WIN!");
        winLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        winLabel.setLayoutX(pane.getWidth() / 2 - 46);
        winLabel.setLayoutY(pane.getHeight() / 2 + 28);
        pane.getChildren().add(winLabel);
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
        LivesObserver livesObserver = new LivesObserver(model, livesImages);
        model.registerObserver(livesObserver);

        Label scoreLabel = new Label("Score: 0");  // Initial placeholder
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.setLayoutX(10);  // Position at the top-left corner
        scoreLabel.setLayoutY(10);
        pane.getChildren().add(scoreLabel);
        ScoreObserver scoreObserver = new ScoreObserver(model, scoreLabel);
        model.registerObserver(scoreObserver);
    }

    // Utility method to load images from resources
    private ImageView loadImage(String resourcePath) {
        return new ImageView(getClass().getResource(resourcePath).toExternalForm());
    }

    private void removeGhostEntities() {
        List<EntityView> ghostsToRemove = new ArrayList<>();
        for (EntityView entityView : entityViews) {
            Renderable entity = entityView.getEntity();  // Access the entity associated with the view
            if (entity instanceof Ghost) {  // Check if the entity is a Ghost
                ghostsToRemove.add(entityView);  // Mark this entity view for removal
            }
        }

        // Remove all ghost entity views from the pane
        for (EntityView ghostView : ghostsToRemove) {
            pane.getChildren().remove(ghostView.getNode());  // Remove ghost view from the pane
        }

        // Also remove them from the entityViews list to stop further updates
        entityViews.removeAll(ghostsToRemove);
    }

    // Reset "READY!" timer when starting a new level or after a life is lost
    public void resetReadyFrames() {
        readyFrames = READY_DISPLAY_TIME;
        pane.getChildren().add(readyLabel);  // Re-add the label to the pane
    }

    // End the game after a 5-second delay
    private void endGameAfterDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> {
            Platform.exit();  // Close the application after 5 seconds
        });
        delay.play();
    }
}
