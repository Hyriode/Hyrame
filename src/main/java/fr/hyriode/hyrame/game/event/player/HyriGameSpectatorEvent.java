package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameSpectatorEvent extends HyriGamePlayerEvent {

    /**
     * Constructor of {@link HyriGamePlayerEvent}
     *
     * @param game       The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     */
    public HyriGameSpectatorEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

}
