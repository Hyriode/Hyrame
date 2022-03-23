package fr.hyriode.hyrame.game.event.protocol;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.event.HyriGameEvent;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocol;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 20:37
 */
public abstract class HyriGameProtocolEvent extends HyriGameEvent {

    /** The {@link HyriGameProtocol} that fired the event */
    protected final HyriGameProtocol protocol;

    /**
     * Constructor of {@link HyriGameProtocolEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param protocol The {@link HyriGameProtocol} instance
     */
    public HyriGameProtocolEvent(HyriGame<?> game, HyriGameProtocol protocol) {
        super(game);
        this.protocol = protocol;
    }

    /**
     * Get the {@link HyriGameProtocol} that fired the event
     *
     * @return The {@link HyriGameProtocol}
     */
    public HyriGameProtocol getProtocol() {
        return this.protocol;
    }

}
