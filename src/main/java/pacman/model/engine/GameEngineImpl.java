package pacman.model.engine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pacman.model.entity.Renderable;
import pacman.model.level.Level;
import pacman.model.level.LevelImpl;
import pacman.model.maze.Maze;
import pacman.model.maze.MazeCreator;
import pacman.view.observer.Observer;
import pacman.view.observer.Subject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of GameEngine - responsible for coordinating the Pac-Man model
 */
public class GameEngineImpl implements GameEngine, Subject {

    private static GameEngineImpl instance; // Singleton instance

    private Level currentLevel;
    private int numLevels;
    private int currentLevelNo;
    private Maze maze;
    private JSONArray levelConfigs;
    private final List<Observer> observers; // List to hold observers

    // Private constructor for Singleton pattern
    private GameEngineImpl(JSONObject config) {
        this.currentLevelNo = 0;
        this.observers = new ArrayList<>();
        init(config);
    }

    // Public method to provide access to the singleton instance
    public static GameEngineImpl getInstance(InputStream configStream) {
        if (instance == null) {
            try {
                JSONParser parser = new JSONParser();
                JSONObject config = (JSONObject) parser.parse(new InputStreamReader(configStream));
                instance = new GameEngineImpl(config);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("Error parsing configuration file", e);
            }
        }
        return instance;
    }

    private void init(JSONObject config) {
        // Set up map
        String mapFile = (String) config.get("mapFile");
        System.out.println("Map file: " + mapFile);  // Debugging line
        MazeCreator mazeCreator = new MazeCreator(mapFile);
        this.maze = mazeCreator.createMaze();
        this.maze.setNumLives(((Long) config.get("numLives")).intValue());

        // Get level configurations
        this.levelConfigs = (JSONArray) config.get("levels");
        this.numLevels = levelConfigs.size();
        if (levelConfigs.isEmpty()) {
            System.exit(0);
        }
    }

    @Override
    public List<Renderable> getRenderables() {
        if (currentLevel == null) {
            System.out.println("currentLevel is null!");  // Debugging line
        }
        return this.currentLevel.getRenderables();
    }

    @Override
    public void moveUp() {
        currentLevel.moveUp();
        notifyObservers(); // Notify observers
    }

    @Override
    public void moveDown() {
        currentLevel.moveDown();
        notifyObservers(); // Notify observers
    }

    @Override
    public void moveLeft() {
        currentLevel.moveLeft();
        notifyObservers(); // Notify observers
    }

    @Override
    public void moveRight() {
        currentLevel.moveRight();
        notifyObservers(); // Notify observers
    }

    @Override
    public void startGame() {
        startLevel();
        notifyObservers(); // Notify observers
    }

    private void startLevel() {
        if (levelConfigs == null || levelConfigs.isEmpty()) {
            throw new RuntimeException("No levels configured!");
        }
        JSONObject levelConfig = (JSONObject) levelConfigs.get(currentLevelNo);
        System.out.println("Starting level: " + currentLevelNo);  // Debugging line
        maze.reset();
        this.currentLevel = new LevelImpl(levelConfig, maze);
        notifyObservers(); // Notify observers
    }

    @Override
    public void endGame() {
        System.out.println("Game Over!");

        // Notify observers about the game status
        notifyObservers(); 

        // Stop the game logic (optional, depending on your game loop)
        currentLevel = null;
        // You could also reset Pac-Man and Ghosts to their initial positions if needed
    }


    @Override
    public void tick() {
        currentLevel.tick();
        notifyObservers(); 

        if (currentLevel.isLevelFinished()) {
            // Transition to the next level
            currentLevelNo++;
            if (currentLevelNo < numLevels) {
                startLevel();  // Start the next level
            } else {
                endGame();  // End the game if all levels are finished
            }
        }
    }


    @Override
    public int getNumLives() {
        if (currentLevel == null) {
            return 0;
        }
        return currentLevel.getNumLives();
    }

    @Override
    public int getScore() {
        if (currentLevel == null) {
            return 0;
        }
        return currentLevel.getScore();
    }

    @Override
    public String getGameStatus() {
        if (currentLevel == null) {
            return "GAME OVER";
        }
        if (currentLevel.isLevelFinished()) {
            return "YOU WIN!";
        } else if (currentLevel.getNumLives() <= 0) {
            return "GAME OVER";
        } else {
            return "READY!";
        }
    }
    

    // Observer pattern methods
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
