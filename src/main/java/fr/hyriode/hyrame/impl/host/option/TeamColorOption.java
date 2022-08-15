package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by AstFaster
 * on 02/08/2022 at 22:31
 */
public class TeamColorOption extends HostOption<HyriGameTeamColor> {

    public TeamColorOption(HostDisplay display, HyriGameTeam team) {
        super(display, team.getColor());
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_COLOR_FORMATTER.asString(player).replace("%color%", this.value.getChatColor() + this.value.getDisplay().getValue(player));
        this.onChanged = color -> {
            team.setColor(color);

            TeamChooserGUI.refresh(this.getHyrame());
        };
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        final List<HyriGameTeamColor> colors = Arrays.asList(HyriGameTeamColor.values());
        final int currentIndex = colors.indexOf(this.value);
        final int nextIndex = currentIndex + 1 == colors.size() ? 0 : currentIndex + 1;

        this.setValue(colors.get(nextIndex));
    }

}
