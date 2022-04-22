package fr.hyriode.hyrame.game;

import java.util.List;
import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 20:15
 */
public interface IHyriGameManager {

    /**
     * Register a game on this server
     *
     * @param game Game to register but as a {@link Supplier} to create the instance after registering
     */
    void registerGame(Supplier<HyriGame<?>> game);

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

}
