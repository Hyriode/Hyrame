package fr.hyriode.hyrame.game.team;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 10/09/2021 at 10:45
 */
public class HyriGameTeam {

    /** Team spawn location */
    protected Location spawnLocation;

    /** Players in team */
    protected final List<HyriGamePlayer> players;

    /** Team's name */
    protected final String name;
    /** Team's display name */
    protected final HyriLanguageMessage displayName;
    /** Team's color */
    protected final HyriGameTeamColor color;
    /** Team's size */
    protected final int teamSize;

    /**
     * Constructor of {@link HyriGameTeam}
     *
     * @param name - Team's name
     * @param displayName - Team's display name
     * @param color - Team's color
     * @param teamSize - Team's size
     */
    public HyriGameTeam(String name, HyriLanguageMessage displayName, HyriGameTeamColor color, int teamSize) {
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.teamSize = teamSize;
        this.players = new ArrayList<>();
    }

    /**
     * Teleport all players to a location
     *
     * @param location - Location to teleport
     */
    public void teleport(Location location) {
        this.getPlayers().forEach(player -> player.getPlayer().teleport(location));
    }

    /**
     * Teleport all players to team spawn
     */
    public void teleportToSpawn() {
        this.teleport(this.spawnLocation);
    }

    /**
     * Format the name of a player with his team color
     *
     * @param player A given player
     * @return A formatted name
     */
    public String formatName(Player player) {
        if (this.getPlayer(player.getUniqueId()) != null) {
            return this.color.getChatColor() + player.getDisplayName();
        }
        return null;
    }

    /**
     * Get game player by his {@link UUID}
     *
     * @param uuid - Player {@link UUID}
     * @return - Game player object
     */
    public HyriGamePlayer getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Send a message to all team players
     *
     * @param message - Message to send
     */
    public void sendMessage(Function<Player, String> message) {
        this.players.forEach(target -> target.sendMessage(message.apply(target.getPlayer())));
    }

    /**
     * Send a title to all players in the team
     *
     * @param title The title to show
     * @param subtitle The subtitle to show
     * @param fadeIn The time to appear
     * @param stay The time to stay
     * @param fadeOut The time to disappear
     */
    public void sendTitle(Function<Player, String> title, Function<Player, String> subtitle, int fadeIn, int stay, int fadeOut) {
        for (HyriGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();

            Title.sendTitle(player, title.apply(player), subtitle.apply(player), fadeIn, stay, fadeOut);
        }
    }

    /**
     * Check if the team contains a given player
     *
     * @param uuid - Player {@link UUID}
     * @return - <code>true</code> if yes
     */
    public boolean contains(UUID uuid) {
        return this.players.stream().anyMatch(player -> player.getUUID().equals(uuid));
    }

    /**
     * Check if the team contains a given player
     *
     * @param player - Player object
     * @return - <code>true</code> if yes
     */
    public boolean contains(HyriGamePlayer player) {
        return this.contains(player.getUUID());
    }

    /**
     * Check if team is full
     *
     * @return <code>true</code> if yes
     */
    public boolean isFull() {
        return this.players.size() >= this.teamSize;
    }

    /**
     * Add a player to the team
     *
     * @param player - Player to add
     * @return - Return a reason if it failed
     */
    public CancelJoinReason addPlayer(HyriGamePlayer player) {
        if (!player.hasTeam()) {
            if (!this.contains(player)) {
                if (this.players.size() < this.teamSize) {
                    final Player p = player.getPlayer();

                    this.players.add(player);

                    player.setTeam(this);

                    return null;
                }
                return CancelJoinReason.FULL;
            }
            return CancelJoinReason.ALREADY_IN;
        }
        return CancelJoinReason.HAS_TEAM;
    }

    /**
     * Remove a player to the team
     *
     * @param player - Player to remove
     * @return - Return a reason if it failed
     */
    public CancelLeaveReason removePlayer(HyriGamePlayer player) {
        if (player.hasTeam()) {
            if (player.isInTeam(this.name)) {
                this.players.remove(player);

                player.setTeam(null);

                return null;
            }
            return CancelLeaveReason.NOT_HIS_TEAM;
        }
        return CancelLeaveReason.NO_TEAM;
    }

    /**
     * Get team's name
     *
     * @return - Team's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get team's display name
     *
     * @return - Team's display name
     */
    public HyriLanguageMessage getDisplayName() {
        return this.displayName;
    }

    /**
     * Get the formatted team's display name for a target
     *
     * @param target The target
     * @return A formatted message
     */
    public String getFormattedDisplayName(Player target) {
        return this.color.getChatColor() + this.getDisplayName().getForPlayer(target);
    }

    /**
     * Get team's color
     *
     * @return - Team's color
     */
    public HyriGameTeamColor getColor() {
        return this.color;
    }

    /**
     * Get team's size
     *
     * @return - Team's size
     */
    public int getTeamSize() {
        return this.teamSize;
    }

    /**
     * Get all game players in the team
     *
     * @return A list of game players
     */
    public List<HyriGamePlayer> getPlayers() {
        return this.players;
    }

    /**
     * Get all players that are still playing
     *
     * @return A list of game players
     */
    public List<HyriGamePlayer> getPlayersPlaying() {
        return this.players.stream().filter(player -> !player.isEliminated()).collect(Collectors.toList());
    }

    /**
     * Check if the team has still players playing the game
     *
     * @return <code>true</code> if yes
     */
    public boolean hasPlayersPlaying() {
        return this.getPlayersPlaying().size() > 0;
    }

    /**
     * Get team's spawn location
     *
     * @return A location
     */
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    /**
     * Set team's spawn location
     *
     * @param spawnLocation New spawn location
     */
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /**
     * Simple enum of reason if the join of a player is cancelled
     */
    public enum CancelJoinReason {
        /** Team is full */
        FULL,
        /** Player has already a team */
        HAS_TEAM,
        /** Player is already in this team */
        ALREADY_IN,
    }

    /**
     * Simple enum of reason if the leave of a player is cancelled
     */
    public enum CancelLeaveReason {
        /** Player doesn't have team */
        NO_TEAM,
        /** It's not the player team */
        NOT_HIS_TEAM,
    }

}
