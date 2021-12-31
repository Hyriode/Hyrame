package fr.hyriode.hyrame.signgui;

import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
@FunctionalInterface
public interface SignGUICompleteCallback {

    /**
     * Fired when sign gui is closed
     *
     * @param player - Player
     * @param lines - Lines provided
     */
    void call(Player player, String[] lines);

}
