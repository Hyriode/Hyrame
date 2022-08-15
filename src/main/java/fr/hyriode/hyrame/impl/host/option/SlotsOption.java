package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.PreciseIntegerOption;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;

import java.util.List;

/**
 * Created by AstFaster
 * on 02/08/2022 at 21:17
 */
public class SlotsOption extends PreciseIntegerOption {

    public SlotsOption(HostDisplay display) {
        super(display, HyriAPI.get().getServer().getSlots(), 1, 200, new int[] {1, 5});

        this.onChanged = slots -> {
            final int currentPlayers = HyriAPI.get().getServer().getPlayersPlaying().size();

            if (slots < currentPlayers) {
                this.value = currentPlayers;
                return;
            }

            HyriAPI.get().getServer().setSlots(slots);

            final IHyrame hyrame = HyrameLoader.getHyrame();
            final List<HyriGameTeam> teams = hyrame.getGameManager().getCurrentGame().getTeams();

            for (HyriGameTeam gameTeam : teams) {
                if (gameTeam.getTeamSize() * teams.size() < slots) {
                    gameTeam.setTeamSize((int) Math.ceil((double) slots / teams.size()));
                }
            }

            TeamChooserGUI.refresh(hyrame);
            HyriWaitingScoreboard.updateAll();
        };
    }

}
