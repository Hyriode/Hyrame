package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.Cast;
import fr.hyriode.hyrame.utils.VoidPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action.ADD;
import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action.REMOVE;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:48
 */
public class HyriGamePlayer extends HyriGameSpectator implements Cast<HyriGamePlayer> {

    /** Player is dead */
    private boolean dead = false;
    /** The timestamp of the player connection */
    protected long connectionTime = -1;
    /** Is player online or no */
    private boolean online = true;

    /**
     * Constructor of {@link Player}
     *
     * @param player Spigot player
     */
    public HyriGamePlayer(Player player) {
        super(player);
    }

    /**
     * Format the name of the player with his team color
     *
     * @return A formatted player name
     */
    public String formatNameWithTeam() {
        final HyriGameTeam team = this.getTeam();

        if (team != null) {
            return team.formatPlayerName(this.player);
        }
        return this.player.getName();
    }

    /**
     * Get the game player as a {@link IHyriPlayer} object
     *
     * @return The {@link IHyriPlayer} linked to the game player
     */
    public IHyriPlayer asHyriPlayer() {
        return IHyriPlayer.get(this.uniqueId);
    }

    /**
     * Get the session of the player
     *
     * @return A {@link IHyriPlayerSession} instance
     */
    public IHyriPlayerSession getSession() {
        return IHyriPlayerSession.get(this.uniqueId);
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
    void initConnectionTime() {
        this.connectionTime = System.currentTimeMillis();
    }

    /**
     * Get the player playtime (in millis)
     *
     * @return A timestamp
     */
    public long getPlayTime() {
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
            this.player = Bukkit.getPlayer(this.uniqueId);
            return;
        }

        this.player = new VoidPlayer(this.player.getDisplayName(), this.player.getName(), this.uniqueId);
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

        final HyriGameDeathEvent event = new HyriGameDeathEvent(IHyrame.get().getGame(), this, reason, killers);

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
     * Get player team
     *
     * @return - Player team
     */
    public HyriGameTeam getTeam() {
        for (HyriGameTeam team : IHyrame.get().getGame().getTeams()) {
            if (team.contains(this)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Remove player from his team
     */
    public void removeFromTeam() {
        final HyriGameTeam team = this.getTeam();

        if (team != null) {
            team.removePlayer(this);
        }
    }

    /**
     * Check if player is in a team
     *
     * @param name - Team name
     * @return - <code>true</code> if player is in one
     */
    public boolean isInTeam(String name) {
        return this.getTeam().getName().equals(name);
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
        return this.getTeam() != null;
    }

}
