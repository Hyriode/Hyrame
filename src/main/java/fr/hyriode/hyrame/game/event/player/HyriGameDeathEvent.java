package fr.hyriode.hyrame.game.event;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameDeathEvent extends HyriGameEvent {

    /** The player that is dead */
    private final HyriGamePlayer gamePlayer;

    /**
     * Constructor of {@link HyriGameDeathEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     */
    public HyriGameDeathEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game);
        this.gamePlayer = gamePlayer;
    }

    /**
     * Get the {@link HyriGamePlayer} that is now dead
     *
     * @return A {@link HyriGamePlayer}
     */
    public HyriGamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

}
