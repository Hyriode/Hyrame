package fr.hyriode.hyrame.game.event.team;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 08/03/2022 at 18:28
 */
public class HyriGamePlayerLeaveTeamEvent extends HyriGameTeamPlayerEvent {

    /**
     * Constructor of {@link HyriGamePlayerLeaveTeamEvent}
     *
     * @param game       The {@link HyriGame} instance
     * @param team       The {@link HyriGameTeam} concerned with the player
     * @param gamePlayer The {@link HyriGamePlayer} that fired the event
     */
    public HyriGamePlayerLeaveTeamEvent(HyriGame<?> game, HyriGameTeam team, HyriGamePlayer gamePlayer) {
        super(game, team, gamePlayer);
    }

}
