package fr.hyriode.hyrame.game;

import java.util.List;
import java.util.Set;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 20:15
 */
public interface IHyriGameManager {

    /**
     * Register a game on this server
     *
     * @param game - Game to register
     */
    void registerGame(HyriGame<?> game);

    /**
     * Unregister a game on this server
     *
     * @param game - Game to unregister
     */
    void unregisterGame(HyriGame<?> game);

    /**
     * Get current game registered on this server
     *
     * @return - {@link HyriGame} object
     */
    HyriGame<?> getCurrentGame();

    /**
     * Get current game server name
     *
     * @return - Server name
     */
    String getGameServerName();

    /**
     * Get all game servers running on the server with a given type
     *
     * @param name - Game name. Example: bedwars, rtf, nexus, etc
     * @return - A list of server name
     */
    List<String> getGames(String name);

    /**
     * Get all game servers running on the server
     *
     * @return - A set of server name
     */
    Set<String> getGames();

}
