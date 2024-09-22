package pacman.model.maze;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.physics.BoundingBoxImpl;
import pacman.model.entity.dynamic.physics.KinematicStateImpl;
import pacman.model.entity.dynamic.player.PacmanVisual;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.entity.staticentity.WallEntity;
import pacman.model.entity.staticentity.collectable.Pellet;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


//Responsible for creating renderables and storing it in the Maze as entities

public class MazeCreator {

    public final String fileName;
    public static final int RESIZING_FACTOR = 16;

    public MazeCreator(String fileName) {
        this.fileName = fileName;
    }

    public Maze createMaze() {
        InputStream mapFileStream = getClass().getResourceAsStream("/map.txt");
        if (mapFileStream == null) {
            throw new RuntimeException("Map file not found! Ensure 'map.txt' is in 'src/main/resources'.");
        }

        Maze maze = new Maze();

        try (Scanner scanner = new Scanner(mapFileStream)) {
            int y = 0;

            // StringBuilder to store and print the entire map at once
            StringBuilder mapContent = new StringBuilder();

            System.out.println("Reading the map file...");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                mapContent.append(line).append("\n"); // Add each line to the StringBuilder

                char[] row = line.toCharArray();

                for (int x = 0; x < row.length; x++) {
                    char currentChar = row[x];
                    // Use the factory method to create an entity
                    Renderable entity = createEntity(currentChar, x * RESIZING_FACTOR, y * RESIZING_FACTOR);
                    maze.addRenderable(entity, currentChar, x, y);
                }

                y += 1;
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            throw new RuntimeException("Error while reading the map file: " + e.getMessage(), e);
        }

        return maze;
    }

    // Factory method to create entities
    // Factory method to create entities
private Renderable createEntity(char type, int x, int y) {
    switch (type) {
        case RenderableType.PACMAN:
            // Create and initialize the image map for Pacman
            Map<PacmanVisual, Image> imageMap = new HashMap<>();
            imageMap.put(PacmanVisual.UP, loadImage("/maze/pacman/playerUp.png"));
            imageMap.put(PacmanVisual.DOWN, loadImage("/maze/pacman/playerDown.png"));
            imageMap.put(PacmanVisual.LEFT, loadImage("/maze/pacman/playerLeft.png"));
            imageMap.put(PacmanVisual.RIGHT, loadImage("/maze/pacman/playerRight.png"));
            imageMap.put(PacmanVisual.CLOSED, loadImage("/maze/pacman/playerClosed.png"));

            return new Pacman(
                    loadImage("/maze/pacman/playerRight.png"), // Initial image for Pac-Man
                    imageMap, // Map of Pacman images
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    new KinematicStateImpl.KinematicStateBuilder()
                            .setPosition(new Vector2D(x, y))
                            .build()
            );

        case RenderableType.GHOST:
            return new GhostImpl(
                    loadImage("/maze/ghosts/ghost.png"), // Placeholder for Ghost image
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    new KinematicStateImpl.KinematicStateBuilder()
                            .setPosition(new Vector2D(x, y))
                            .build(),
                    null, // Initial GhostMode
                    new Vector2D(x, y), // Placeholder for target corner
                    null  // Placeholder for initial direction
            );

        case RenderableType.PELLET:
            return new Pellet(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    Renderable.Layer.FOREGROUND,
                    loadImage("/maze/pellet.png"), // Image for Pellet
                    100 // Points for collecting the pellet
            );

            case RenderableType.UP_LEFT_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/upLeft.png")
            );
        case RenderableType.UP_RIGHT_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/upRight.png")
            );
        case RenderableType.DOWN_LEFT_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/downLeft.png")
            );
        case RenderableType.DOWN_RIGHT_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/downRight.png")
            );
        case RenderableType.HORIZONTAL_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/horizontal.png")
            );
        case RenderableType.VERTICAL_WALL:
            return new WallEntity(
                    new BoundingBoxImpl(new Vector2D(x, y), RESIZING_FACTOR, RESIZING_FACTOR),
                    loadImage("/maze/walls/vertical.png")
            );
    
            // Add more cases if there are other types of entities to create
            default:
                return null;
        }
}


private Image loadImage(String resourcePath) {
    InputStream imageStream = getClass().getResourceAsStream(resourcePath);
    if (imageStream == null) {
        throw new RuntimeException("Image not found: " + resourcePath);
    }
    return new Image(getClass().getResource(resourcePath).toExternalForm());
    
}

}
