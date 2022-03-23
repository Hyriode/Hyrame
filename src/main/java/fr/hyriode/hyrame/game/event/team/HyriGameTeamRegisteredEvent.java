package fr.hyriode.hyrame.game.event.team;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.team.HyriGameTeam;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 18:20
 */
public class HyriGameTeamRegisteredEvent extends HyriGameTeamEvent {

    /**
     * Constructor of {@link HyriGameTeamRegisteredEvent}
     *
     * @param game The {@link HyriGame} instance
     * @param team The {@link HyriGameTeam} that fired the event
     */
    public HyriGameTeamRegisteredEvent(HyriGame<?> game, HyriGameTeam team) {
        super(game, team);
    }

}
