package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Created by AstFaster
 * on 29/05/2022 at 16:59
 */
public class HyriGameRespawnEvent extends HyriGamePlayerEvent {

    /**
     * Constructor of {@link HyriGameRespawnEvent}
     *
     * @param game       The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     */
    public HyriGameRespawnEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

}
