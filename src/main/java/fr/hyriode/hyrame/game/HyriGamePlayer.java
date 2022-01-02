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
    /** Player is eliminated */
    protected boolean eliminated;
    /** Player is dead */
    protected boolean dead;
    /** The timestamp of the player connection */
    protected long connectionTime = -1;

    /** Player object */
    protected final Player player;

    /** The running game */
    protected final HyriGame<?> game;

    /**
     * Constructor of {@link Player}
     *
     * @param game The running game
     * @param player Spigot player
     */
    public HyriGamePlayer(HyriGame<?> game, Player player) {
        this.game = game;
        this.player = player;
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
     * Hide player to other players
     */
    public void hide() {
        for (HyriGamePlayer player : this.game.getPlayers()) {
            player.getPlayer().hidePlayer(this.player);
        }
    }

    /**
     * Show player to other players
     */
    public void show() {
        for (HyriGamePlayer player : this.game.getPlayers()) {
            player.getPlayer().showPlayer(this.player);
        }
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
     * Get player {@link UUID}
     *
     * @return Player {@link UUID}
     */
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    /**
     * Get the connection time of the player
     *
     * @return A timestamp
     */
    public long getConnectionTime() {
        return this.connectionTime;
    }

    /**
     * Set the player connection time as the current time
     */
    public void setConnectionTime() {
        this.connectionTime = System.currentTimeMillis();
    }

    /**
     * Get the player played time (in millis)
     *
     * @return A timestamp
     */
    public long getPlayedTime() {
        if (this.connectionTime != -1) {
            return System.currentTimeMillis() - this.connectionTime;
        }
        return -1;
    }

    /**
     * Check if player is dead
     *
     * @return <code>true</code> if yes
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Set if player is dead
     *
     * @param dead - <code>true</code> to set as dead
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Check if player is eliminated
     *
     * @return <code>true</code> if yes
     */
    public boolean isEliminated() {
        return this.eliminated;
    }

    /**
     * Set if player is eliminated
     *
     * @param eliminated - <code>true</code> to set as eliminated
     */
    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
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
