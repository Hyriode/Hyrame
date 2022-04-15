package fr.hyriode.hyrame.game.event.player;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 07/03/2022 at 20:25
 */
public class HyriGameDeathEvent extends HyriGamePlayerEvent {

    /** The reason of the death */
    private final Reason reason;
    /** The list of  killers. It might be <code>null</code> if the player was not killed by a player*/
    private final List<HyriLastHitterProtocol.LastHitter> killers;

    /**
     * Constructor of {@link HyriGameDeathEvent}
     * @param game The {@link HyriGame} instance
     * @param gamePlayer The concerned {@link HyriGamePlayer}
     * @param reason The reason of the death
     * @param killers The list of players that killed the player
     */
    public HyriGameDeathEvent(HyriGame<?> game, HyriGamePlayer gamePlayer, Reason reason, List<HyriLastHitterProtocol.LastHitter> killers) {
        super(game, gamePlayer);
        this.reason = reason;
        this.killers = killers;
    }

    /**
     * Get the reason of the death
     *
     * @return A {@link Reason}
     */
    public Reason getReason() {
        return this.reason;
    }

    /**
     * Get the list of killers
     *
     * @return A list of {@link fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol.LastHitter}
     */
    public List<HyriLastHitterProtocol.LastHitter> getKillers() {
        return this.killers;
    }

    /**
     * Get the best killer
     *
     * @return A {@link fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol.LastHitter} that represents the killer
     */
    public HyriLastHitterProtocol.LastHitter getBestKiller() {
        if (this.killers != null && this.killers.size() > 0) {
            return this.killers.get(0);
        }
       return null;
    }

    /**
     * All the reasons available for a death
     */
    public enum Reason {

        /** The player has been mostly killed by other players */
        PLAYERS,
        /** The player died from void */
        VOID,
        /** The player died from fall damage */
        FALL,
        /** The player died from a block explosion (tnt) */
        BLOCK_EXPLOSION,
        /** The player died from an entity explosion (creeper, fireball, etc) */
        ENTITY_EXPLOSION,
        /** The player died from fire */
        FIRE,
        /** The player died from lava */
        LAVA,
        /** The player died from a lightning */
        LIGHTNING

    }


}

