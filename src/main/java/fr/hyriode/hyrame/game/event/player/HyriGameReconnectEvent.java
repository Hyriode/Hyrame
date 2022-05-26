package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Created by AstFaster
 * on 25/05/2022 at 19:24
 */
public class HyriGameReconnectEvent extends HyriGamePlayerEvent {

    private boolean allowed = true;

    public HyriGameReconnectEvent(HyriGame<?> game, HyriGamePlayer gamePlayer) {
        super(game, gamePlayer);
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public void allow() {
        this.allowed = true;
    }

    public void disallow() {
        this.allowed = false;
    }

}
