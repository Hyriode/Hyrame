package fr.hyriode.hyrame.utils;

import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 15/01/2022 at 08:48
 */
public class DirectionUtil {

    /**
     * Get the player's direction
     *
     * @param player The given player
     * @return A {@link Direction}
     */
    public static Direction getPlayerDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;

        if (rotation < 0)
            rotation += 360.0;

        if (0 <= rotation && rotation < 22.5) {
            return Direction.NORTH;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return Direction.EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return Direction.SOUTH;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return Direction.WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return Direction.NORTH;
        }
        return null;
    }

    /**
     * An enum with all possibles directions
     */
    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

}
