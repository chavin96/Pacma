package pacman.model.entity.dynamic.ghost;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.level.Level;
import pacman.model.maze.Maze;

import java.util.*;

/**
 * Concrete implementation of Ghost entity in Pac-Man Game
 */
public class GhostImpl implements Ghost {

    private final Layer layer = Layer.FOREGROUND;
    private final Image image;
    private final BoundingBox boundingBox;
    private final Vector2D startingPosition;
    private final Vector2D targetCorner;
    private KinematicState kinematicState;
    private GhostMode ghostMode;
    private Vector2D targetLocation;
    private Direction currentDirection;
    private Set<Direction> possibleDirections;
    private Vector2D playerPosition;
    private Map<GhostMode, Double> speeds;

    public GhostImpl(Image image, BoundingBox boundingBox, KinematicState kinematicState, GhostMode ghostMode, Vector2D targetCorner, Direction currentDirection) {
        this.image = image;
        this.boundingBox = boundingBox;
        this.kinematicState = kinematicState;
        this.startingPosition = kinematicState.getPosition();
        this.ghostMode = ghostMode != null ? ghostMode : GhostMode.SCATTER; // Ensure default ghostMode is set
        this.currentDirection = currentDirection != null ? currentDirection : Direction.LEFT; // Set a default direction if currentDirection is null
        this.possibleDirections = new HashSet<>();
        this.targetCorner = targetCorner;
        this.targetLocation = getTargetLocation();
    }

    @Override
    public void setSpeeds(Map<GhostMode, Double> speeds) {
        this.speeds = speeds;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void update() {
        this.updateDirection();
        this.kinematicState.update();
        this.boundingBox.setTopLeft(this.kinematicState.getPosition());
    }

    private void updateDirection() {
        // Ghosts update their target location when they reach an intersection
        if (Maze.isAtIntersection(this.possibleDirections)) {
            this.targetLocation = getTargetLocation();
        }

        this.currentDirection = selectDirection(possibleDirections);

        switch (currentDirection) {
            case LEFT -> this.kinematicState.left();
            case RIGHT -> this.kinematicState.right();
            case UP -> this.kinematicState.up();
            case DOWN -> this.kinematicState.down();
        }
    }

    private Vector2D getTargetLocation() {
        if (this.ghostMode == GhostMode.CHASE) {
            // Ensure playerPosition is not null
            if (this.playerPosition != null) {
                return this.playerPosition;
            } else {
                System.out.println("Player position is null. Defaulting to (0,0)");
                return Vector2D.ZERO; // Default to a valid vector
            }
        } else if (this.ghostMode == GhostMode.SCATTER) {
            // Ensure targetCorner is not null
            if (this.targetCorner != null) {
                return this.targetCorner;
            } else {
                System.out.println("Target corner is null. Defaulting to (0,0)");
                return Vector2D.ZERO; // Default to a valid vector
            }
        }
        return Vector2D.ZERO; // Fallback to a valid vector in case of an issue
    }
    
    
    private Direction selectDirection(Set<Direction> possibleDirections) {
        if (possibleDirections.isEmpty()) {
            return currentDirection;  // No possible directions to move
        }
    
        Map<Direction, Double> distances = new HashMap<>();
    
        for (Direction direction : possibleDirections) {
            if (direction != currentDirection.opposite()) {  // Prevent reversing direction
                distances.put(direction, Vector2D.calculateEuclideanDistance(
                        this.kinematicState.getPotentialPosition(direction), this.targetLocation));
            }
        }
    
        // Select the direction that gets closest to the target
        return Collections.min(distances.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    
    

    @Override
    public void setGhostMode(GhostMode ghostMode) {
        this.ghostMode = ghostMode;
        this.kinematicState.setSpeed(speeds.get(ghostMode));
    }

    @Override
    public boolean collidesWith(Renderable renderable) {
        return boundingBox.collidesWith(kinematicState.getDirection(), renderable.getBoundingBox());
    }

    @Override
    public void collideWith(Level level, Renderable renderable) {
        if (level.isPlayer(renderable)) {
            level.handleLoseLife();  // Pac-Man loses a life
        }
    }    

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public double getHeight() {
        return this.boundingBox.getHeight();
    }

    public void setPlayerPosition(Vector2D playerPosition) {
        this.playerPosition = playerPosition;
    }    

    @Override
    public double getWidth() {
        return this.boundingBox.getWidth();
    }

    @Override
    public Vector2D getPosition() {
        return this.kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D position) {
        this.kinematicState.setPosition(position);
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public void reset() {
        // Return ghost to starting position
        this.kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                .setPosition(startingPosition)
                .build();
    }

    @Override
    public void setPossibleDirections(Set<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Direction getDirection() {
        return this.kinematicState.getDirection();
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(boundingBox.getMiddleX(), boundingBox.getMiddleY());
    }
}
