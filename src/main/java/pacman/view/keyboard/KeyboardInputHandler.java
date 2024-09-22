package pacman.view.keyboard;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pacman.model.engine.GameEngine;

// Command Pattern KeyboardInputHandler

public class KeyboardInputHandler {

    private final Command moveUpCommand;
    private final Command moveDownCommand;
    private final Command moveLeftCommand;
    private final Command moveRightCommand;

    public KeyboardInputHandler(GameEngine gameEngine) {
        this.moveUpCommand = new MoveUpCommand(gameEngine);
        this.moveDownCommand = new MoveDownCommand(gameEngine);
        this.moveLeftCommand = new MoveLeftCommand(gameEngine);
        this.moveRightCommand = new MoveRightCommand(gameEngine);
    }

    public void handlePressed(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        switch (keyCode) {
            case LEFT -> moveLeftCommand.execute();
            case RIGHT -> moveRightCommand.execute();
            case DOWN -> moveDownCommand.execute();
            case UP -> moveUpCommand.execute();
            default -> {}
        }
    }
}
