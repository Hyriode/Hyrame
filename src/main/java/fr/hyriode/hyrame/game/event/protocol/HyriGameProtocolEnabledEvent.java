package fr.hyriode.hyrame.game.event.protocol;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocol;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 20:39
 */
public class HyriGameProtocolEnabledEvent extends HyriGameProtocolEvent {

    /**
     * Constructor of {@link HyriGameProtocolEnabledEvent}
     *
     * @param game     The {@link HyriGame} instance
     * @param protocol The {@link HyriGameProtocol} instance
     */
    public HyriGameProtocolEnabledEvent(HyriGame<?> game, HyriGameProtocol protocol) {
        super(game, protocol);
    }

}
