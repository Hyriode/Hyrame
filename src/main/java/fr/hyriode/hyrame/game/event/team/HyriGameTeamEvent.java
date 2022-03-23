package fr.hyriode.hyrame.game.event.team;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.event.HyriGameEvent;
import fr.hyriode.hyrame.game.team.HyriGameTeam;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 18:22
 */
public abstract class HyriGameTeamEvent extends HyriGameEvent {

    /** The concerned {@link HyriGameTeam} */
    protected final HyriGameTeam team;

    /**
     * Constructor of {@link HyriGameTeamEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param team The {@link HyriGameTeam} that fired the event
     */
    public HyriGameTeamEvent(HyriGame<?> game, HyriGameTeam team) {
        super(game);
        this.team = team;
    }

    /**
     * Get the {@link HyriGameTeam} that fired the event
     *
     * @return The {@link HyriGameTeam}
     */
    public HyriGameTeam getTeam() {
        return this.team;
    }

}
