package fr.hyriode.hyrame.game.event;

import fr.hyriode.hyrame.game.HyriGame;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:13
 */
public class HyriGameUnregisteredEvent extends HyriGameEvent {

    /**
     * Constructor of {@link HyriGameEvent}
     *
     * @param game The {@link HyriGame} instance
     */
    public HyriGameUnregisteredEvent(HyriGame<?> game) {
        super(game);
    }

}
