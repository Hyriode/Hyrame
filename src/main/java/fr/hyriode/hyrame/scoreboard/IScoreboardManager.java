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
public interface IScoreboardManager {

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
     * Get all scoreboards that have the same class. But also with the {@link UUID} of the players that own them
     *
     * @param scoreboardClass The {@link Class} of the scoreboards to look for
     * @param <T> The type of scoreboards to return
     * @return A map of {@link HyriScoreboard} and {@link UUID}
     */
    <T extends HyriScoreboard> Map<T, UUID> getScoreboardsMap(Class<T> scoreboardClass);


    /**
     * Get all scoreboards that have the same class
     *
     * @param scoreboardClass The {@link Class} of the scoreboards to look for
     * @param <T> The type of the scoreboards to get
     * @return A list of {@link HyriScoreboard}
     */
    <T extends HyriScoreboard> List<T> getScoreboards(Class<T> scoreboardClass);

}
