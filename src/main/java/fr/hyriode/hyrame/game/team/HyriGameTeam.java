package fr.hyriode.hyrame.game.team;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.language.LanguageMessage;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:45
 */
public class HyriGameTeam {

    private Location spawnLocation;

    private final List<HyriGamePlayer> players;

    private final int teamSize;
    private final HyriGameTeamColor color;
    private final LanguageMessage displayName;
    private final String name;

    public HyriGameTeam(String name, LanguageMessage displayName, HyriGameTeamColor color, int teamSize) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.teamSize = teamSize;
        this.players = new ArrayList<>();
    }

    public void teleport(Location location) {
        this.getOnlinePlayers().forEach(player -> player.getPlayer().getPlayer().teleport(location));
    }

    public void teleportToSpawn() {
        this.teleport(this.spawnLocation);
    }

    public List<HyriGamePlayer> getOnlinePlayers() {
        return this.players.stream().filter(HyriGamePlayer::isOnline).collect(Collectors.toList());
    }

    public List<HyriGamePlayer> getOfflinePlayers() {
        return this.players.stream().filter(hyriGamePlayer -> !hyriGamePlayer.isOnline()).collect(Collectors.toList());
    }

    public HyriGamePlayer getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public void sendMessage(String message) {
        this.players.forEach(player -> player.sendMessage(message));
    }

    public boolean contains(UUID uuid) {
        return this.players.stream().anyMatch(player -> player.getUuid().equals(uuid));
    }

    public boolean contains(HyriGamePlayer player) {
        return this.contains(player.getUuid());
    }

    public boolean isFull() {
        return this.players.size() >= this.teamSize;
    }

    public CancelJoinReason addPlayer(HyriGamePlayer player) {
        if (!player.hasTeam()) {
            if (!this.contains(player)) {
                if (this.players.size() < this.teamSize) {
                    this.players.add(player);
                }
                return CancelJoinReason.FULL;
            }
            return CancelJoinReason.ALREADY_IN;
        }
        return CancelJoinReason.HAS_TEAM;
    }

    public CancelLeaveReason removePlayer(HyriGamePlayer player) {
        if (player.hasTeam()) {
            if (player.isInTeam(this.name)) {
                this.players.remove(player);
            }
            return CancelLeaveReason.NOT_HIS_TEAM;
        }
        return CancelLeaveReason.NO_TEAM;
    }

    public String getName() {
        return this.name;
    }

    public LanguageMessage getDisplayName() {
        return this.displayName;
    }

    public HyriGameTeamColor getColor() {
        return this.color;
    }

    public int getTeamSize() {
        return this.teamSize;
    }

    public List<HyriGamePlayer> getPlayers() {
        return this.players;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public enum CancelJoinReason {
        FULL,
        HAS_TEAM,
        ALREADY_IN,
    }

    public enum CancelLeaveReason {
        NO_TEAM,
        NOT_HIS_TEAM,
    }

}
