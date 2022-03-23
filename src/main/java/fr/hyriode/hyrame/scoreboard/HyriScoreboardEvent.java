package fr.hyriode.hyrame.scoreboard;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.hyrame.inventory.HyriInventory;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 08:47
 */
public class HyriScoreboardEvent extends HyriEvent {

    /** The {@link HyriScoreboard} that triggered the event */
    private final HyriScoreboard scoreboard;
    /** The player that owns the inventory */
    private final Player player;
    /** The action that triggered the event */
    private final Action action;

    /**
     * Constructor of {@link HyriScoreboardEvent}
     *
     * @param scoreboard The scoreboard
     * @param player The player
     * @param action The action
     */
    public HyriScoreboardEvent(HyriScoreboard scoreboard, Player player, Action action) {
        this.scoreboard = scoreboard;
        this.player = player;
        this.action = action;
    }

    /**
     * Get the scoreboard
     *
     * @return The {@link HyriScoreboard}
     */
    public HyriScoreboard getScoreboard() {
        return this.scoreboard;
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
        /** When an inventory is shown */
        SHOW,
        /** When an inventory is hidden */
        HIDE
    }

}
