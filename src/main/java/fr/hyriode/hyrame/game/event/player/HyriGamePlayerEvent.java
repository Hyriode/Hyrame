package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.HyriGameEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 18:20
 */
public abstract class HyriGamePlayerEvent extends HyriGameEvent {

    /** The concerned game player */
    protected final HyriGamePlayer gamePlayer;

    /**
     * Constructor of {@link HyriGamePlayerEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     */
    public HyriGamePlayerEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game);
        this.gamePlayer = gamePlayer;
    }

    /**
     * Get the {@link HyriGamePlayer} that fired the event
     *
     * @return The {@link HyriGamePlayer}
     */
    public HyriGamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

}
