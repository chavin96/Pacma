package pacman.model.level;

import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.entity.dynamic.ghost.GhostMode;

import java.util.HashMap;
import java.util.Map;

public class LevelConfigurationReader {

    private final JSONObject levelConfiguration;

    public LevelConfigurationReader(JSONObject levelConfiguration) {
        this.levelConfiguration = levelConfiguration;
    }

    /**
     * Retrieves the player's speed for the level
     * @return the player's speed for the level
     */
    public double getPlayerSpeed() {
        try {
            return ((Number) levelConfiguration.get("pacmanSpeed")).doubleValue();
        } catch (NullPointerException | ClassCastException e) {
            throw new ConfigurationParseException("Invalid or missing 'pacmanSpeed' in configuration");
        }
    }

    /**
     * Retrieves the lengths of the ghost modes in seconds
     * @return the lengths of the ghost modes in seconds
     */
    public Map<GhostMode, Integer> getGhostModeLengths() {
        Map<GhostMode, Integer> ghostModeLengths = new HashMap<>();
        try {
            JSONObject modeLengthsObject = (JSONObject) levelConfiguration.get("modeLengths");
            ghostModeLengths.put(GhostMode.CHASE, ((Number) modeLengthsObject.get("chase")).intValue());
            ghostModeLengths.put(GhostMode.SCATTER, ((Number) modeLengthsObject.get("scatter")).intValue());
        } catch (NullPointerException | ClassCastException e) {
            throw new ConfigurationParseException("Invalid or missing 'modeLengths' configuration");
        }
        return ghostModeLengths;
    }

    /**
     * Retrieves the speeds of the ghosts for each ghost mode
     * @return the speeds of the ghosts for each ghost mode
     */
    public Map<GhostMode, Double> getGhostSpeeds() {
        Map<GhostMode, Double> ghostSpeeds = new HashMap<>();
        try {
            JSONObject ghostSpeed = (JSONObject) levelConfiguration.get("ghostSpeed");
            ghostSpeeds.put(GhostMode.CHASE, ((Number) ghostSpeed.get("chase")).doubleValue());
            ghostSpeeds.put(GhostMode.SCATTER, ((Number) ghostSpeed.get("scatter")).doubleValue());
        } catch (NullPointerException | ClassCastException e) {
            throw new ConfigurationParseException("Invalid or missing 'ghostSpeed' configuration");
        }
        return ghostSpeeds;
    }
}
