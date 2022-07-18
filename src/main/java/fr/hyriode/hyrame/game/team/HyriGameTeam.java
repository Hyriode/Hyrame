package fr.hyriode.hyrame.game.team;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.team.HyriGamePlayerJoinTeamEvent;
import fr.hyriode.hyrame.game.event.team.HyriGamePlayerLeaveTeamEvent;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
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
    /** Can members of the team attack theme */
    protected boolean friendlyFire;
    /*** The name tag visibility of the team */
    protected HyriScoreboardTeam.NameTagVisibility nameTagVisibility;
    /** Team's size */
    protected final int teamSize;

    /** The game that handles the team */
    protected final HyriGame<?> game;

    /**
     * Constructor of {@link HyriGameTeam}
     *
     * @param game The game that handles the team
     * @param name Team's name
     * @param displayName Team's display name
     * @param color Team's color
     * @param friendlyFire boolean that defines if member of the team can attack them
     * @param nameTagVisibility Team's name tag visibility
     * @param teamSize Team's size
     */
    public HyriGameTeam(HyriGame<?> game, String name, HyriLanguageMessage displayName, HyriGameTeamColor color, boolean friendlyFire, HyriScoreboardTeam.NameTagVisibility nameTagVisibility, int teamSize) {
        this.game = game;
        this.name = name;
        this.displayName = displayName;
        this.color = color;
        this.friendlyFire = friendlyFire;
        this.nameTagVisibility = nameTagVisibility;
        this.teamSize = teamSize;
        this.players = new ArrayList<>();
    }

    /**
     * Constructor of {@link HyriGameTeam}
     *
     * @param game The game that handles the team
     * @param name Team's name
     * @param displayName Team's display name
     * @param color Team's color
     * @param nameTagVisibility Team's name tag visibility
     * @param teamSize Team's size
     */
    public HyriGameTeam(HyriGame<?> game, String name, HyriLanguageMessage displayName, HyriGameTeamColor color, HyriScoreboardTeam.NameTagVisibility nameTagVisibility, int teamSize) {
        this(game, name, displayName, color, false, nameTagVisibility, teamSize);
    }

    /**
     * Constructor of {@link HyriGameTeam}
     *
     * @param game The game that handles the team
     * @param name Team's name
     * @param displayName Team's display name
     * @param color Team's color
     * @param teamSize Team's size
     */
    public HyriGameTeam(HyriGame<?> game, String name, HyriLanguageMessage displayName, HyriGameTeamColor color, int teamSize) {
        this(game, name, displayName, color, HyriScoreboardTeam.NameTagVisibility.ALWAYS, teamSize);
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
    public String formatPlayerName(Player player) {
        if (this.getPlayer(player.getUniqueId()) != null) {
            return this.color.getChatColor() + player.getName();
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
        return this.players.stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Send a message to all team players
     *
     * @param message Message to send
     */
    public void sendMessage(Function<Player, String> message) {
        this.players.forEach(target -> target.sendMessage(message.apply(target.getPlayer())));
    }

    /**
     * Send a message to all team players
     *
     * @param message Message to send
     */
    public void sendMessage(HyriLanguageMessage message) {
        this.sendMessage(target -> message.getValue(target));
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
        return this.players.stream().anyMatch(player -> player.getUniqueId().equals(uuid));
    }

    /**
     * Check if the team contains a given player
     *
     * @param player - Player object
     * @return - <code>true</code> if yes
     */
    public boolean contains(HyriGamePlayer player) {
        return this.contains(player.getUniqueId());
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
                    this.players.add(player);

                    player.setTeam(this);

                    HyriAPI.get().getEventBus().publishAsync(new HyriGamePlayerJoinTeamEvent(this.game, this, player));

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

                HyriAPI.get().getEventBus().publishAsync(new HyriGamePlayerLeaveTeamEvent(this.game, this, player));

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
        return this.color.getChatColor() + this.displayName.getValue(target);
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
     * Check if friendly fire is enabled for this team
     *
     * @return <code>true</code> if yes
     */
    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    /**
     * Set if the team is in friendly fire mode
     *
     * @param friendlyFire New friendly fire mode
     */
    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Get the name tag visibility of the team
     *
     * @return A {@link HyriScoreboardTeam.NameTagVisibility}
     */
    public HyriScoreboardTeam.NameTagVisibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public void setNameTagVisibility(HyriScoreboardTeam.NameTagVisibility nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
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
        return this.players.stream().filter(player -> !player.isSpectator()).collect(Collectors.toList());
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
