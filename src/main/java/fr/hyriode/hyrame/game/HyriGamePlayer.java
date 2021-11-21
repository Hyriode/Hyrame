package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:48
 */
public class HyriGamePlayer {

    /** Player team */
    protected HyriGameTeam team;

    /** Player is spectating */
    protected boolean spectator;

    /** Player object */
    protected final Player player;

    /**
     * Constructor of {@link Player}
     *
     * @param player - Spigot player
     */
    public HyriGamePlayer(Player player) {
        this.player = player;
    }

    /**
     * Get Spigot player object
     *
     * @return - {@link Player} object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Send a message to the player
     *
     * @param message - Message to send
     */
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }

    /**
     * Get player {@link UUID}
     *
     * @return - Player {@link UUID}
     */
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    /**
     * Check if player is in spectator
     *
     * @return - <code>true</code> if player is spectating
     */
    public boolean isSpectator() {
        return this.spectator;
    }

    /**
     * Change player spectator mode
     *
     * @param spectator - <code>true</code> to toggle
     */
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    /**
     * Get player team
     *
     * @return - Player team
     */
    public HyriGameTeam getTeam() {
        return this.team;
    }

    /**
     * Set player team
     *
     * @param team - New team
     */
    public void setTeam(HyriGameTeam team) {
        this.team = team;
    }

    /**
     * Remove player from his team
     */
    public void removeFromTeam() {
        if (this.hasTeam()) {
            this.team.removePlayer(this);
            this.team = null;
        }
    }

    /**
     * Check if player is in a team
     *
     * @param name - Team name
     * @return - <code>true</code> if player is in one
     */
    public boolean isInTeam(String name) {
        return this.team.getName().equals(name);
    }

    /**
     * Check if player is in a team
     *
     * @param team - Team
     * @return - <code>true</code> if player is in one
     */
    public boolean isInTeam(HyriGameTeam team) {
        return this.isInTeam(team.getName());
    }

    /**
     * Check if player has a team
     *
     * @return - <code>true</code> if player has one
     */
    public boolean hasTeam() {
        return this.team != null;
    }

}
