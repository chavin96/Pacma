package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.DynamicEntity;
import pacman.model.entity.dynamic.physics.Vector2D;

import java.util.Map;

//Represents Ghost entity in Pac-Man Game according the factory method

public interface Ghost extends DynamicEntity {

    void setSpeeds(Map<GhostMode, Double> speeds);

    void setGhostMode(GhostMode ghostMode);

    void setPlayerPosition(Vector2D playerPosition);
}
