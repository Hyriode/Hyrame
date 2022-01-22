package fr.hyriode.hyrame.utils.target;

import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 03/01/2022 at 19:25
 */
public class TargetInfo {

    /** The target */
    private final Player player;
    /** The distance between the player and the target */
    private final double distance;

    /**
     * Constructor of {@link TargetInfo}
     *
     * @param player The target found
     * @param distance The distance
     */
    public TargetInfo(Player player, double distance) {
        this.player = player;
        this.distance = distance;
    }

    /**
     * Get the target found
     *
     * @return A {@link Player}
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the distance between the target and the player
     *
     * @return A distance
     */
    public double getDistance() {
        return this.distance;
    }

}
