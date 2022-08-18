package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.HyriGameEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameSpectatorEvent extends HyriGameEvent {

    private final HyriGameSpectator spectator;
    private final Action action;

    public HyriGameSpectatorEvent(HyriGame<?> game,  HyriGameSpectator spectator, Action action) {
        super(game);
        this.spectator = spectator;
        this.action = action;
    }

    public Action getAction() {
        return this.action;
    }

    public HyriGameSpectator getSpectator() {
        return this.spectator;
    }

    public enum Action {
        ADD, REMOVE
    }

}
