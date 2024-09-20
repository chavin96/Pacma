package pacman.view.keyboard;

import pacman.model.engine.GameEngine;

public class MoveRightCommand implements Command {
    private final GameEngine gameEngine;

    public MoveRightCommand(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void execute() {
        gameEngine.moveRight();
    }
}
