package pacman.model.engine;

import pacman.model.entity.Renderable;
import pacman.view.observer.Observer;
import java.util.List;

/**
 * The base interface for interacting with the Pac-Man model
 */
public interface GameEngine {

    /**
     * Registers an observer to be notified of changes in the game state.
     * @param observer The observer to register.
     */
    void registerObserver(Observer observer);

    /**
     * Removes an observer so it no longer receives updates.
     * @param observer The observer to remove.
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all registered observers of a change in the game state.
     */
    void notifyObservers();

    /**
     * Returns a list of all renderable objects in the game.
     * @return A list of renderable objects.
     */
    List<Renderable> getRenderables();

    /**
     * Starts the game and initializes the game state.
     */
    void startGame();

    /**
     * Starts the game and initializes the game state.
     */
    void endGame();

    /**
     * Moves the player up in the game.
     */
    void moveUp();

    /**
     * Moves the player down in the game.
     */
    void moveDown();

    /**
     * Moves the player left in the game.
     */
    void moveLeft();

    /**
     * Moves the player right in the game.
     */
    void moveRight();

    /**
     * Progresses the game state by one tick.
     */
    void tick();

    /**
     * Returns the number of lives the player has remaining.
     * @return The number of lives.
     */
    int getNumLives();

    /**
     * Returns the current status of the game (e.g., running, paused, over).
     * @return The current game status.
     */
    String getGameStatus();

    /**
     * Returns the current score of the player.
     * @return The player's score.
     */
    int getScore();
}
