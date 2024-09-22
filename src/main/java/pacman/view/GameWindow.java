package pacman.view;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    private HBox livesBox;

    private final List<ImageView> livesImages;

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
        // "READY!" label for 100 frames
        if (readyFrames > 0) {
            List<Renderable> entities = model.getRenderables();
    
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
    
            readyFrames--;
    
            return;
        }
    
        if (readyFrames == 0) {
            pane.getChildren().remove(readyLabel); 
            readyFrames = -1;  
        }

        if (model.getGameStatus().equals("YOU WIN!")) {
            showWinMessage();
            timeline.stop();  
            endGameAfterDelay();  
            return;
        }
    
        model.tick();
    
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
    
        // Check if the game is over
        if (model.getGameStatus().equals("GAME OVER")) {
            removeGhostEntities();
            showGameOver();
            timeline.stop();  
            endGameAfterDelay();  
            return;
        }
    }

    private void initializeLabels() {
        readyLabel = new Label("READY!");
        readyLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: yellow;");
        readyLabel.setLayoutX(pane.getWidth() / 2 - 35);
        readyLabel.setLayoutY(pane.getHeight() / 2 + 28);   
        pane.getChildren().add(readyLabel);  

        gameOverLabel = new Label("GAME OVER!");
        gameOverLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: red;");
        gameOverLabel.setLayoutX(pane.getWidth() / 2 - 57);
        gameOverLabel.setLayoutY(pane.getHeight() / 2 + 28);

        livesBox = new HBox(4);  
        livesBox.setLayoutX(10);  
        livesBox.setLayoutY(545);  

        pane.getChildren().add(livesBox);

        LivesObserver livesObserver = new LivesObserver(model, livesBox);
        model.registerObserver(livesObserver);
    }
    
    private void showWinMessage() {
        removeGhostEntities();
        Label winLabel = new Label("YOU WIN!");
        winLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        winLabel.setLayoutX(pane.getWidth() / 2 - 46);
        winLabel.setLayoutY(pane.getHeight() / 2 + 28);
        pane.getChildren().add(winLabel);
    }

    private void showGameOver() {
        pane.getChildren().add(gameOverLabel);
    }

    // Add Pac-Man lives as icons
    private void addPacmanLives() {
        System.out.println("addPacmanLives called.");
        for (int i = 0; i < model.getNumLives(); i++) {
            ImageView pacmanLife = new ImageView(getClass().getResource("/maze/pacman/playerRight.png").toExternalForm()); 
            pacmanLife.setFitHeight(20); 
            pacmanLife.setFitWidth(20);
            pacmanLife.setLayoutX(20 + (i * 30)); 
            pacmanLife.setLayoutY(10);  
            pane.getChildren().add(pacmanLife); 
            livesImages.add(pacmanLife); 
        }
    }

    private void registerObservers() {
        LivesObserver livesObserver = new LivesObserver(model, livesBox);
        model.registerObserver(livesObserver);

        Label scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.setLayoutX(10); 
        scoreLabel.setLayoutY(15);
        pane.getChildren().add(scoreLabel);
        ScoreObserver scoreObserver = new ScoreObserver(model, scoreLabel);
        model.registerObserver(scoreObserver);
    }

    private void removeGhostEntities() {
        List<EntityView> ghostsToRemove = new ArrayList<>();
        for (EntityView entityView : entityViews) {
            Renderable entity = entityView.getEntity(); 
            if (entity instanceof Ghost) {
                ghostsToRemove.add(entityView); 
            }
        }

        for (EntityView ghostView : ghostsToRemove) {
            pane.getChildren().remove(ghostView.getNode()); 
        }

        entityViews.removeAll(ghostsToRemove);
    }

    public void resetReadyFrames() {
        readyFrames = READY_DISPLAY_TIME;
        pane.getChildren().add(readyLabel); 
    }

    private void endGameAfterDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> {
            Platform.exit();
        });
        delay.play();
    }
}
