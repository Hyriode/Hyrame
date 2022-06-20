package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameSpectatorEvent extends HyriGamePlayerEvent {

    private final Action action;

    /**
     * Constructor of {@link HyriGamePlayerEvent}
     *
     * @param game       The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     * @param action The action done on the event
     */
    public HyriGameSpectatorEvent(HyriGame<?> game, HyriGamePlayer gamePlayer, Action action) {
        super(game, gamePlayer);
        this.action = action;
    }

    public Action getAction() {
        return this.action;
    }

    public enum Action {
        ADD, REMOVE
    }

}
