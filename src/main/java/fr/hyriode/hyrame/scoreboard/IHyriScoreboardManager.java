package fr.hyriode.hyrame.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 25/02/2022 at 18:41
 */
public interface IHyriScoreboardManager {

    /**
     * Show a provided scoreboard to a player
     *
     * @param player The concerned player
     * @param scoreboard The scoreboard to show
     */
    void showScoreboardToPlayer(Player player, HyriScoreboard scoreboard);

    /**
     * Remove the current scoreboard of a player
     *
     * @param player The player to remove his scoreboard
     * @return <code>true</code> if the scoreboard has been removed
     */
    boolean removeScoreboardFromPlayer(Player player);

    /**
     * Remove a scoreboard to all the players that have it
     *
     * @param scoreboardClass The class of the scoreboard
     */
    void removeScoreboardFromPlayers(Class<? extends HyriScoreboard> scoreboardClass);

    /**
     * Get the current scoreboard of a player
     *
     * @param player The concerned player
     * @return A {@link HyriScoreboard} object
     */
    HyriScoreboard getPlayerScoreboard(Player player);

    /**
     * Check if the current scoreboard of the player is a given one
     *
     * @param player The concerned player
     * @param scoreboardClass The class of the scoreboard
     * @return <code>true</code> if the player has the given scoreboard
     */
    boolean isPlayerScoreboard(Player player, Class<? extends HyriScoreboard> scoreboardClass);

    /**
     * Get the scoreboard of each player
     *
     * @return A map of player {@link UUID} and {@link HyriScoreboard}
     */
    Map<UUID, HyriScoreboard> getPlayersScoreboards();

    /**
     * Get all scoreboards that have the same class. But also with the {@link UUID} of the players that owned them
     *
     * @param scoreboardClass The {@link Class} of the scoreboard to look for
     * @return A list of {@link HyriScoreboard}
     */
    <T extends HyriScoreboard> Map<T, UUID> getScoreboardsMap(Class<T> scoreboardClass);

    <T extends HyriScoreboard> List<T> getScoreboards(Class<T> scoreboardClass);

}
