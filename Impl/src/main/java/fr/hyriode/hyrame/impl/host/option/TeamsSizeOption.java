package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.PlayersOption;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;
import fr.hyriode.hyrame.impl.game.util.TeamChooserItem;

/**
 * Created by AstFaster
 * on 02/08/2022 at 21:11
 */
public class TeamsSizeOption extends PlayersOption {

    public TeamsSizeOption(HostDisplay display) {
        super(display, -1, 1, Integer.MAX_VALUE);

        final HyriGame<?> game = this.getGame();

        this.value = game.getTeams().get(0).getTeamSize();
        this.onChanged = teamsSize -> {
            for (HyriGameTeam team : game.getTeams()) {
                team.setTeamSize(teamsSize);
                team.clearPlayers();

                for (HyriGamePlayer gamePlayer : team.getPlayers()) {
                    this.getHyrame().getItemManager().giveItem(gamePlayer.getPlayer(), 0, TeamChooserItem.class);
                }

                TeamChooserGUI.refresh(this.getHyrame());
            }
        };
    }

    @Override
    public void setValue(Integer value) {
        if (value * this.getGame().getTeams().size() < HyriAPI.get().getServer().getSlots()) {
            return;
        }

        super.setValue(value);
    }

}
