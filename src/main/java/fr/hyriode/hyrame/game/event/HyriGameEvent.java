package fr.hyriode.hyrame.game.event;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.hyrame.game.HyriGame;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:06
 */
public abstract class HyriGameEvent extends HyriEvent {

    /** The {@link HyriGame} instance that fired the event */
    protected final HyriGame<?> game;

    /**
     * Constructor of {@link HyriGameEvent}
     *
     * @param game The {@link HyriGame} instance
     */
    public HyriGameEvent(HyriGame<?> game) {
        this.game = game;
    }

    /**
     * Get the {@link HyriGame} instance that fired the event
     *
     * @return A {@link HyriGame} instance
     */
    public HyriGame<?> getGame() {
        return this.game;
    }

}
