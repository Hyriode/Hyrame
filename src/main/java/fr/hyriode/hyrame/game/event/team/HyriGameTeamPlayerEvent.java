package fr.hyriode.hyrame.game.event.team;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 18:26
 */
public abstract class HyriGameTeamPlayerEvent extends HyriGameTeamEvent {

    /** The concerned {@link HyriGamePlayer} */
    protected final HyriGamePlayer gamePlayer;

    /**
     * Constructor of {@link HyriGameTeamPlayerEvent}
     *  @param game The {@link HyriGame} instance
     * @param team The {@link HyriGameTeam} concerned with the player
     * @param gamePlayer The {@link HyriGamePlayer} that fired the event
     */
    public HyriGameTeamPlayerEvent(HyriGame<?> game, HyriGameTeam team, HyriGamePlayer gamePlayer) {
        super(game, team);
        this.gamePlayer = gamePlayer;
    }

    /**
     * Get the {@link HyriGamePlayer} that fired the event
     *
     * @return The {@link HyriGamePlayer}
     */
    public HyriGamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

}
