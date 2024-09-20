package pacman.view.keyboard;

import pacman.model.engine.GameEngine;

/**
 * Command to move Pac-Man up
 */
public class MoveUpCommand implements Command {
    private final GameEngine gameEngine;

    public MoveUpCommand(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void execute() {
        gameEngine.moveUp();
    }
}
