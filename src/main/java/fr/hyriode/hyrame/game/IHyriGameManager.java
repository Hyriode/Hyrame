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
     * @param game Game to register
     */
    void registerGame(HyriGame<?> game);

    /**
     * Unregister a game on this server
     *
     * @param game Game to unregister
     */
    void unregisterGame(HyriGame<?> game);

    /**
     * Get current game registered on this server
     *
     * @return {@link HyriGame} object
     */
    HyriGame<?> getCurrentGame();

    /**
     * Get all game servers running on the network with a given game name and type
     *
     * @param name Game name. Example: bedwars, rtf, nexus, etc
     * @param type Game type. Example: 1v1, 2v2
     * @return A list of server name
     */
    List<String> getGames(String name, String type);

    /**
     * Get all game servers running on the network with a given game name
     *
     * @param name Game name. Example: bedwars, rtf, nexus, etc
     * @return A list of server name
     */
    List<String> getGames(String name);

    /**
     * Get all game servers running on the network
     *
     * @return A list of server name
     */
    List<String> getGames();

}
