package fr.hyriode.hyrame.npc;

import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
@FunctionalInterface
public interface HyriNPCInteractCallback {

    /**
     * Called when a player interact with a NPC
     *
     * @param rightClick - If it's a right click
     * @param player - Player who interacted
     */
    void call(boolean rightClick, Player player);

}
