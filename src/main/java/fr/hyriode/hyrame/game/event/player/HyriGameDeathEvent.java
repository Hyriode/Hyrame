package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameDeathEvent extends HyriGamePlayerEvent {

    /** The killer. It might be <code>null</code> if the player was not killed by a player*/
    private final HyriGamePlayer killer;

    /**
     * Constructor of {@link HyriGameDeathEvent}
     *  @param game The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     * @param killer The player that killed the player
     */
    public HyriGameDeathEvent(HyriGame<?> game, HyriGamePlayer gamePlayer, HyriGamePlayer killer) {
        super(game, gamePlayer);
        this.killer = killer;
    }

    /**
     * Get the player that killed the player
     *
     * @return A {@link HyriGamePlayer} or <code>null</code> if the player was not killed by a player
     */
    public HyriGamePlayer getKiller() {
        return this.killer;
    }

}

