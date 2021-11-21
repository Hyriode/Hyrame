package fr.hyriode.tools.scoreboard.team;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ScoreboardTeam {

    /** Players in team */
    private final List<String> players = new CopyOnWriteArrayList<>();

    /** Real team's name */
    private String realName;

    /** Team's display */
    private String display;

    /** Team's prefix */
    private String prefix;

    /** Team's suffix */
    private String suffix;

    /** Is hiding to other teams */
    private boolean hideToOtherTeams = false;

    /** Team's name */
    private final String name;

    /**
     * Constructor of {@link ScoreboardTeam}
     *
     * @param name - Team's name
     * @param display - Team's display name
     */
    public ScoreboardTeam(String name, String display) {
        this.name = name;
        this.display = display;
    }

    /**
     * Constructor of {@link ScoreboardTeam}
     *
     * @param name - Team's name
     * @param realName - Team's real name
     * @param display - Team's display name
     * @param prefix - Team's prefix
     * @param suffix - Team's suffix
     */
    public ScoreboardTeam(String name, String realName, String display, String prefix, String suffix) {
        this.name = name;
        this.realName = realName;
        this.display = display;
        this.prefix = prefix;
        this.suffix = suffix;
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
     * Get team's real name
     *
     * @return - Team's real name
     */
    public String getRealName() {
        return this.realName;
    }

    /**
     * Set team's real name
     *
     * @param realName - New real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Get team's display name
     *
     * @return - Team's display name
     */
    public String getDisplay() {
        return this.display;
    }

    /**
     * Set team's display name
     *
     * @param display - New display name
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * Get team's prefix
     *
     * @return - Team's prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Set team's prefix
     *
     * @param prefix - New prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix.substring(0, Math.min(prefix.length(), 16));
    }

    /**
     * Get team's suffix
     *
     * @return - Team's suffix
     */
    public String getSuffix() {
        return this.suffix;
    }

    /**
     * Set team's suffix
     *
     * @param suffix - New suffix
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix.substring(0, Math.min(suffix.length(), 16));
    }

    /**
     * Is team hiding to other teams
     *
     * @return - <code>true</code> if yes
     */
    public boolean isHideToOtherTeams() {
        return this.hideToOtherTeams;
    }

    /**
     * Set if team is hiding to other teams
     *
     * @param hideToOtherTeams - New value
     */
    public void setHideToOtherTeams(boolean hideToOtherTeams) {
        this.hideToOtherTeams = hideToOtherTeams;
    }

    /**
     * Get players in the team
     *
     * @return - A list of players names
     */
    public List<String> getPlayers() {
        return this.players;
    }

    /**
     * Check if the team contains a given player
     *
     * @param playerName - Player's name
     * @return - <code>true</code> if yes
     */
    public boolean contains(String playerName) {
        return this.players.contains(playerName);
    }

    /**
     * Check if the team contains a given player
     *
     * @param player - Player
     * @return - <code>true</code> if yes
     */
    public boolean contains(OfflinePlayer player) {
        return this.players.contains(player.getName());
    }

    /**
     * Add a player to the team
     *
     * @param player - Player to add
     */
    public void addPlayer(OfflinePlayer player) {
        this.players.add(player.getName());
    }

    /**
     * Add a player to the team
     *
     * @param playerName - Player's name to add
     */
    public void addPlayer(String playerName) {
        this.players.add(playerName);
    }

    /**
     * Remove a player from the team
     *
     * @param player - Player to remove
     */
    public void removePlayer(OfflinePlayer player) {
        this.players.remove(player.getName());
    }

    /**
     * Remove a player from the team
     *
     * @param playerName - Player's name to remove
     */
    public void removePlayer(String playerName) {
        this.players.remove(playerName);
    }

    /**
     * Get team's size
     *
     * @return - Team's size
     */
    public int getSize() {
        return this.players.size();
    }

}
