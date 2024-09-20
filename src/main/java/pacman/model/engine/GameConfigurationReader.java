package pacman.model.engine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pacman.ConfigurationParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to read Game Configuration from JSONObject
 */
public class GameConfigurationReader {

    private static final Logger LOGGER = Logger.getLogger(GameConfigurationReader.class.getName());
    private final JSONObject gameConfig;

    public GameConfigurationReader(String configPath) {
        JSONParser parser = new JSONParser();

        try {
            this.gameConfig = (JSONObject) parser.parse(new FileReader(configPath));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Config file not found", e);
            throw new ConfigurationParseException("Config file not found: " + configPath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading config file", e);
            throw new ConfigurationParseException("Error reading config file: " + configPath);
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error parsing config file", e);
            throw new ConfigurationParseException("Error parsing config file: " + configPath);
        }
    }

    /**
     * Gets the path of the map file
     * @return path of map file
     */
    public String getMapFile() {
        return (String) gameConfig.get("map");
    }

    /**
     * Gets the number of lives of the player
     * @return number of lives of player
     */
    public int getNumLives() {
        return ((Number) gameConfig.get("numLives")).intValue();
    }

    /**
     * Gets JSONArray of level configurations
     * @return JSONArray of level configurations
     */
    public JSONArray getLevelConfigs() {
        return (JSONArray) gameConfig.get("levels");
    }
}
