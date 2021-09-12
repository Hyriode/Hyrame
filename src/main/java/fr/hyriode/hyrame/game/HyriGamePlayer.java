package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:48
 */
public class HyriGamePlayer {

    protected HyriGameTeam team;

    protected boolean spectator;

    protected final UUID uuid;

    public HyriGamePlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(this.uuid);
    }

    public boolean isOnline() {
        return this.getPlayer().isOnline();
    }

    public void sendMessage(String message) {
        if (this.isOnline()) {
            ((Player) this.getPlayer()).sendMessage(message);
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public HyriGameTeam getTeam() {
        return this.team;
    }

    public void setTeam(HyriGameTeam team) {
        this.team = team;
    }

    public boolean isInTeam(String name) {
        return this.team.getName().equals(name);
    }

    public boolean isInTeam(HyriGameTeam team) {
        return this.isInTeam(team.getName());
    }

    public boolean hasTeam() {
        return this.team != null;
    }

}
