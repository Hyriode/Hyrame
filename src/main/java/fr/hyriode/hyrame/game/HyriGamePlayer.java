package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.VoidPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
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
    private boolean spectator;
    /** Player is dead */
    private boolean dead;
    /** The timestamp of the player connection */
    protected long connectionTime = -1;
    /** Is player online or no */
    private boolean online = true;

    /** Player object */
    protected Player player;

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
     * Format the name of the player with his team color
     *
     * @return A formatted player name
     */
    public String formatNameWithTeam() {
        if (this.team != null) {
            return this.team.formatPlayerName(this.player);
        }
        return this.player.getName();
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
       this.hide(true);
    }

    /**
     * Hide player to other players
     *
     * @param removeFromTabList If <code>true</code> the player will not be removed from the tab list
     */
    public void hide(boolean removeFromTabList) {
        if (!(this.player instanceof CraftPlayer)) {
            return;
        }

        for (HyriGamePlayer gamePlayer : this.game.getPlayers()) {
            if (gamePlayer != this) {
                final Player player = gamePlayer.getPlayer();

                player.hidePlayer(this.player);

                if (!removeFromTabList) {
                    PacketUtil.sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) this.player).getHandle()));
                }
            }
        }
    }

    /**
     * Show player to other players
     */
    public void show() {
        for (HyriGamePlayer gamePlayer : this.game.getPlayers()) {
            gamePlayer.getPlayer().showPlayer(this.player);
        }
    }

    /**
     * Get Spigot player object
     *
     * @return {@link Player} object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the game player as a {@link IHyriPlayer} object
     *
     * @return The {@link IHyriPlayer} linked to the game player
     */
    public IHyriPlayer asHyriPlayer() {
        return IHyriPlayer.get(this.player.getUniqueId());
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
    public void initConnectionTime() {
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
     * Check if the player is online or not
     *
     * @return <code>true</code> if yes
     */
    public boolean isOnline() {
        return this.online;
    }

    /**
     * Set if the player is online or not
     *
     * @param online New value for online field
     */
    public void setOnline(boolean online) {
        this.online = online;

        if (online) {
            this.player = Bukkit.getPlayer(this.player.getUniqueId());
            return;
        }

        this.player = new VoidPlayer(this.player.getDisplayName(), this.player.getName(), this.player.getUniqueId());
    }

    /**
     * Set if player is dead
     *
     * @param reason The reason of the player's death
     * @param killers The list of players that killed the player
     * @return The death event fired
     */
    public HyriGameDeathEvent setDead(HyriGameDeathEvent.Reason reason, List<HyriLastHitterProtocol.LastHitter> killers) {
        this.dead = true;

        final HyriGameDeathEvent event = new HyriGameDeathEvent(this.game, this, reason, killers);

        HyriAPI.get().getEventBus().publish(event);

        return event;
    }

    /**
     * Set the player no longer dead
     */
    public void setNotDead() {
        this.dead = false;
    }

    /**
     * Check if player is in spectator
     *
     * @return <code>true</code> if player is spectating
     */
    public boolean isSpectator() {
        return this.spectator;
    }

    /**
     * Change player spectator mode
     *
     * @param spectator <code>true</code> to toggle
     */
    public void setSpectator(boolean spectator) {
        final boolean oldValue = this.spectator;

        this.spectator = spectator;

        if (!oldValue && this.spectator) {
            HyriAPI.get().getEventBus().publish(new HyriGameSpectatorEvent(this.game, this));
        }
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
