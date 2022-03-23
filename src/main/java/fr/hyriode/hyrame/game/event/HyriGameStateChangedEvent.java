package fr.hyriode.hyrame.game.event;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:08
 */
public class HyriGameStateChangedEvent extends HyriGameEvent {

    /** The old state before changing */
    private final HyriGameState oldState;
    /** The new state after changing */
    private final HyriGameState newState;

    /**
     * Constructor of {@link HyriGameEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param oldState The old {@link HyriGameState}
     * @param newState The new {@link HyriGameState}
     */
    public HyriGameStateChangedEvent(HyriGame<?> game, HyriGameState oldState, HyriGameState newState) {
        super(game);
        this.oldState = oldState;
        this.newState = newState;
    }

    /**
     * Get the old {@link HyriGameState} before changing it
     *
     * @return A {@link HyriGameState}
     */
    public HyriGameState getOldState() {
        return this.oldState;
    }

    /**
     * Get the new {@link HyriGameState} after changing it
     *
     * @return A {@link HyriGameState}
     */
    public HyriGameState getNewState() {
        return this.newState;
    }

}
