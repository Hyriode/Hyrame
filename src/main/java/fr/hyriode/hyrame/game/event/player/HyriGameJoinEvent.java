package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 19:47
 */
public class HyriGameJoinEvent extends HyriGamePlayerEvent {

    /**
     * Constructor of {@link HyriGameJoinEvent}
     *
     * @param game       The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     */
    public HyriGameJoinEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

}
