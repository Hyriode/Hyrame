package fr.hyriode.hyrame.game.event;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.team.HyriGameTeam;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/03/2022 at 18:55
 */
public class HyriGameWinEvent extends HyriGameEvent {

    /** The winner {@link HyriGameTeam} object */
    private final HyriGameTeam winner;

    /**
     * Constructor of {@link HyriGameWinEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param winner The team that won the game
     */
    public HyriGameWinEvent(HyriGame<?> game, HyriGameTeam winner) {
        super(game);
        this.winner = winner;
    }

    /**
     * Get the team that won the game
     *
     * @return A {@link HyriGameTeam}
     */
    public HyriGameTeam getWinner() {
        return this.winner;
    }

}
