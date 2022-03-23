package fr.hyriode.hyrame.game.event.protocol;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocol;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 20:39
 */
public class HyriGameProtocolUnregisteredEvent extends HyriGameProtocolEvent {

    /**
     * Constructor of {@link HyriGameProtocolUnregisteredEvent}
     *
     * @param game     The {@link HyriGame} instance
     * @param protocol The {@link HyriGameProtocol} instance
     */
    public HyriGameProtocolUnregisteredEvent(HyriGame<?> game, HyriGameProtocol protocol) {
        super(game, protocol);
    }

}
