package fr.hyriode.hyrame.game.event.protocol;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocol;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 20:39
 */
public class HyriGameProtocolDisabledEvent extends HyriGameProtocolEvent {

    /**
     * Constructor of {@link HyriGameProtocolDisabledEvent}
     *
     * @param game     The {@link HyriGame} instance
     * @param protocol The {@link HyriGameProtocol} instance
     */
    public HyriGameProtocolDisabledEvent(HyriGame<?> game, HyriGameProtocol protocol) {
        super(game, protocol);
    }

}
