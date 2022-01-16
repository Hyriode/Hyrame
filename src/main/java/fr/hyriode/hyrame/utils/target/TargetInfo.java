package fr.hyriode.hyrame.utils.target;

import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 03/01/2022 at 19:25
 */
public class TargetInfo {

    private final Player player;
    private final double distance;

    public TargetInfo(Player player, double distance) {
        this.player = player;
        this.distance = distance;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getDistance() {
        return this.distance;
    }

}
