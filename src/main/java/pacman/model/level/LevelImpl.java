package pacman.model.level;

import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.DynamicEntity;
import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.ghost.GhostMode;
import pacman.model.entity.dynamic.physics.PhysicsEngine;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Controllable;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.entity.staticentity.StaticEntity;
import pacman.model.entity.staticentity.collectable.Collectable;
import pacman.model.maze.Maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete implementation of Pac-Man level
 */
public class LevelImpl implements Level {

    private static final int START_LEVEL_TIME = 200;
    private final Maze maze;
    private List<Renderable> renderables;
    private Controllable player;
    private List<Ghost> ghosts;
    private int tickCount;
    private Map<GhostMode, Integer> modeLengths;
    private int numLives;
    private List<Renderable> collectables;
    private GhostMode currentGhostMode;
    private int score; // Add a score field

    public LevelImpl(JSONObject levelConfiguration, Maze maze) {
        this.renderables = new ArrayList<>();
        this.maze = maze;
        this.tickCount = 0;
        this.modeLengths = new HashMap<>();
        this.currentGhostMode = GhostMode.SCATTER;
        this.score = 0; // Initialize score
        initLevel(new LevelConfigurationReader(levelConfiguration));
    }

    private void initLevel(LevelConfigurationReader levelConfigurationReader) {
        // Fetch all renderables for the level
        this.renderables = maze.getRenderables();

        // Set up player
        if (!(maze.getControllable() instanceof Controllable)) {
            throw new ConfigurationParseException("Player entity is not controllable");
        }
        this.player = (Controllable) maze.getControllable();
        this.player.setSpeed(levelConfigurationReader.getPlayerSpeed());
        setNumLives(maze.getNumLives());

        // Set up ghosts
        this.ghosts = maze.getGhosts().stream()
                .map(element -> (Ghost) element)
                .collect(Collectors.toList());
        Map<GhostMode, Double> ghostSpeeds = levelConfigurationReader.getGhostSpeeds();

        for (Ghost ghost : this.ghosts) {
            ghost.setSpeeds(ghostSpeeds);
            ghost.setGhostMode(this.currentGhostMode);
        }
        this.modeLengths = levelConfigurationReader.getGhostModeLengths();

        // Set up collectables
        this.collectables = new ArrayList<>(maze.getPellets());
    }

    private void checkPacmanPelletCollision() {
        // Iterate through all pellets
        Iterator<Renderable> pelletIterator = collectables.iterator();
        while (pelletIterator.hasNext()) {
            Renderable pellet = pelletIterator.next();
            if (pellet instanceof Collectable && 
                player.getBoundingBox().collidesWith(player.getDirection(), pellet.getBoundingBox())) {
                // Pac-Man collected the pellet
                Collectable collectable = (Collectable) pellet;
                collect(collectable);  // Updates the score and removes the pellet
                pelletIterator.remove();  // Remove the pellet from the list
            }
        }
    }

    @Override
    public List<Renderable> getRenderables() {
        return this.renderables;
    }

    private List<DynamicEntity> getDynamicEntities() {
        return renderables.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return renderables.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }
    

    @Override
    public void tick() {
        // Handle ghost mode switching
        updateGhostsWithPlayerPosition();
        if (tickCount == modeLengths.get(currentGhostMode)) {
            this.currentGhostMode = GhostMode.getNextGhostMode(currentGhostMode);
            for (Ghost ghost : this.ghosts) {
                ghost.setGhostMode(this.currentGhostMode);
            }
            tickCount = 0;
        }

        // Pac-Man image switch logic
        if (tickCount % Pacman.PACMAN_IMAGE_SWAP_TICK_COUNT == 0) {
            this.player.switchImage();
        }

        // Update the dynamic entities
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        for (DynamicEntity dynamicEntity : dynamicEntities) {
            maze.updatePossibleDirections(dynamicEntity);
            dynamicEntity.update();
        }

        // Check Pac-Man and pellet collisions
        checkPacmanPelletCollision();

        // Notify observers about changes in the game state
        // notifyObservers();

        // Handle collisions between dynamic entities and static entities
        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            // Handle dynamic entity to dynamic entity collisions
            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB) ||
                        dynamicEntityB.collidesWith(dynamicEntityA)) {
                    dynamicEntityA.collideWith(this, dynamicEntityB);
                    dynamicEntityB.collideWith(this, dynamicEntityA);
                }
            }

            // Handle dynamic entity to static entity collisions
            for (StaticEntity staticEntity : getStaticEntities()) {
                if (dynamicEntityA.collidesWith(staticEntity)) {
                    dynamicEntityA.collideWith(this, staticEntity);
                    PhysicsEngine.resolveCollision(dynamicEntityA, staticEntity);
                }
            }
        }

        tickCount++;
    }

    private void updateGhostsWithPlayerPosition() {
        Vector2D playerPosition = player.getPosition();  // Get Pac-Man's current position
        for (Ghost ghost : ghosts) {
            ghost.setPlayerPosition(playerPosition);  // Update each ghost with Pac-Man's position
        }
    }
    

    @Override
    public boolean isPlayer(Renderable renderable) {
        return renderable == this.player;
    }

    @Override
    public boolean isCollectable(Renderable renderable) {
        return maze.getPellets().contains(renderable) && ((Collectable) renderable).isCollectable();
    }

    @Override
    public void moveLeft() {
        player.left();
    }

    @Override
    public void moveRight() {
        player.right();
    }

    @Override
    public void moveUp() {
        player.up();
    }

    @Override
    public void moveDown() {
        player.down();
    }

    @Override
    public boolean isLevelFinished() {
        return collectables.stream()
                .filter(Collectable.class::isInstance)  // Filter only Collectable objects
                .map(Collectable.class::cast)  // Cast to Collectable type
                .noneMatch(Collectable::isCollectable);  // Check if any are still collectable
    }

    @Override
    public int getNumLives() {
        return this.numLives;
    }

    private void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    @Override
    public void handleLoseLife() {
        numLives--;
        player.reset();  // Reset Pac-Man’s position
        ghosts.forEach(Ghost::reset);  // Reset ghosts' positions
    }

    @Override
    public void handleGameEnd() {
        System.out.println("Game Over!");
        // notifyObservers();  // Notify observers about the game status
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void collect(Collectable collectable) {
        if (collectable.isCollectable()) {
            collectable.collect();  // Mark the pellet as collected
            score += collectable.getPoints();  // Increase the score by the points of the pellet
        }
    }
}
