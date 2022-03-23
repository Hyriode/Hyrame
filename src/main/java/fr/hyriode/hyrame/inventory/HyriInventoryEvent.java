package fr.hyriode.hyrame.inventory;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.hyrame.scoreboard.HyriScoreboardEvent;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 08:47
 */
public class HyriInventoryEvent extends HyriEvent {

    /** The {@link HyriInventory} that triggered the event */
    private final HyriInventory inventory;
    /** The player that owns the inventory */
    private final Player player;
    /** The action that triggered the event */
    private final Action action;

    /**
     * Constructor of {@link HyriScoreboardEvent}
     *
     * @param inventory The inventory
     * @param player The player
     * @param action The action
     */
    public HyriInventoryEvent(HyriInventory inventory, Player player, Action action) {
        this.inventory = inventory;
        this.player = player;
        this.action = action;
    }

    /**
     * Get the inventory
     *
     * @return The {@link HyriInventory}
     */
    public HyriInventory getInventory() {
        return this.inventory;
    }

    /**
     * Get the player
     *
     * @return The {@link Player}
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the action
     *
     * @return An {@link Action} value
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * The possible actions that can trigger the event
     */
    public enum Action {
        /** When an inventory is opened */
        OPEN,
        /** When an inventory is closed */
        CLOSE
    }

}
