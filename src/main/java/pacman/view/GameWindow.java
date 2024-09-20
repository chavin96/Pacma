package pacman.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for managing the Pac-Man Game View
 */
public class GameWindow {

    public static final String FONT_FILE_PATH = "src/main/resources/fonts/PressStart2P-Regular.ttf";
    private final Scene scene;
    private final Pane pane;
    private final GameEngine model;
    private final List<EntityView> entityViews;
    private final List<ImageView> livesImages; // For displaying Pac-Man lives
    private final int width;
    private final int height;

    public GameWindow(GameEngine model, int width, int height) {
        this.model = model;
        this.width = width;
        this.height = height;

        pane = new Pane();
        scene = new Scene(pane, width, height);

        entityViews = new ArrayList<>();
        livesImages = new ArrayList<>(); // List to store Pac-Man lives images

        // Initialize the KeyboardInputHandler with the Command pattern
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);
        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);

        // Draw the background
        BackgroundDrawer backgroundDrawer = new StandardBackgroundDrawer();
        backgroundDrawer.draw(model, pane);

        // Load custom font
        Font customFont = null;
        try {
            customFont = Font.loadFont(new FileInputStream(FONT_FILE_PATH), 16);
        } catch (Exception e) {
            System.out.println("Font not found, using default font.");
            customFont = Font.font("Verdana", FontWeight.BOLD, 16);
        }

        // Add a label for score and register it as an observer
        Label scoreLabel = new Label("Score: 0"); // Initial placeholder
        scoreLabel.setFont(customFont);  // Apply custom font
        scoreLabel.setTextFill(javafx.scene.paint.Color.WHITE);  // Set score text to white
        scoreLabel.setLayoutX(10);  // Adjust position as needed
        scoreLabel.setLayoutY(10);  // Adjust position as needed
        pane.getChildren().add(scoreLabel);
        ScoreObserver scoreObserver = new ScoreObserver(model, scoreLabel);
        model.registerObserver(scoreObserver);

        // Add a label for game status and register it as an observer
        Label statusLabel = new Label("READY!");
        statusLabel.setFont(customFont);  // Apply custom font
        statusLabel.setTextFill(javafx.scene.paint.Color.YELLOW);  // Set text color to yellow

        // Positioning the label in the center of the pane
        statusLabel.setLayoutX((width - 80) / 2);  // Center the label horizontally (adjust 80 based on label width)
        statusLabel.setLayoutY((height / 2) - 20);  // Center the label vertically, adjusting slightly

        pane.getChildren().add(statusLabel);
        GameStatusObserver statusObserver = new GameStatusObserver(model, statusLabel);
        model.registerObserver(statusObserver);

        // Add Pac-Man lives as images and register them as an observer
        // Create and register the LivesObserver with the model
        addPacmanLives();  // Create the lives as images
        LivesObserver livesObserver = new LivesObserver(model, livesImages);  // Updated to handle List<ImageView>
        model.registerObserver(livesObserver);  // Register observer for lives
    }

    private void addPacmanLives() {
        // Load the Pac-Man image for lives
        Image pacmanLifeImage = new Image(getClass().getResource("/maze/pacman/playerRight.png").toExternalForm());

        // Positioning the lives images
        for (int i = 0; i < model.getNumLives(); i++) {
            ImageView lifeView = new ImageView(pacmanLifeImage);
            lifeView.setFitWidth(20);  // Adjust size
            lifeView.setFitHeight(20);
            lifeView.setLayoutX(10 + (i * 25));  // Spacing between lives
            lifeView.setLayoutY(height - 40);  // Position near the bottom of the window
            pane.getChildren().add(lifeView);
            livesImages.add(lifeView);  // Add to the list
        }
    }

    public Scene getScene() {
        return scene;
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(34),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        model.startGame();
    }

    private void draw() {
        model.tick();  // Update the game state by advancing one tick.
    
        List<Renderable> entities = model.getRenderables();  // Get all renderables.
    
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
                    view.update();  // Update the entity view.
                    break;
                }
            }
        
            if (notFound) {
                // Debugging: Make sure we're adding the entity's view to the pane
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());  // Add to the pane.
            }
        }
        
    
        // Remove all entity views marked for deletion
        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
    
        entityViews.removeIf(EntityView::isMarkedForDelete);  // Clean up entity views.
    }
}
