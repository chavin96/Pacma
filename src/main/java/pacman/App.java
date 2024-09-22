package pacman;

import javafx.application.Application;
import javafx.stage.Stage;
import pacman.model.engine.GameEngineImpl;
import pacman.view.GameWindow;

import java.io.InputStream;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            InputStream configStream = getClass().getResourceAsStream("/config.json");
            if (configStream == null) {
                throw new RuntimeException("Configuration file not found!");
            }

            GameEngineImpl model = GameEngineImpl.getInstance(configStream);

            GameWindow window = new GameWindow(model, 448, 576);
            primaryStage.setTitle("Pac-Man");
            primaryStage.setScene(window.getScene());
            primaryStage.show();

            window.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

