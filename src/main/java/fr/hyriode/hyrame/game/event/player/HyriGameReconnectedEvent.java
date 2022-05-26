package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Created by AstFaster
 * on 25/05/2022 at 19:24
 */
public class HyriGameReconnectedEvent extends HyriGamePlayerEvent {

    public HyriGameReconnectedEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

}
