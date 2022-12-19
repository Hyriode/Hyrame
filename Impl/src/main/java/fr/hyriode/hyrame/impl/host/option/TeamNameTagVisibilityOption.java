package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.List;

import static fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam.NameTagVisibility;

/**
 * Created by AstFaster
 * on 02/08/2022 at 22:18
 */
public class TeamNameTagVisibilityOption extends HostOption<NameTagVisibility> {

    public TeamNameTagVisibilityOption(HostDisplay display, NameTagVisibility defaultValue) {
        super(display, defaultValue);
        this.valueFormatter = player -> {
            final StringBuilder builder = new StringBuilder();
            final NameTagVisibility[] visibilities = NameTagVisibility.values();

            for (int i = 0; i < visibilities.length; i++) {
                final NameTagVisibility visibility = visibilities[i];

                builder.append(ChatColor.DARK_GRAY)
                        .append(Symbols.DOT_BOLD)
                        .append(this.value == visibility ? ChatColor.AQUA : ChatColor.GRAY)
                        .append(" ")
                        .append(visibility.display().get().getValue(player))
                        .append(i + 1 == visibilities.length ? "" : "\n");
            }
            return HyrameMessage.HOST_OPTION_NAME_TAG_VISIBILITY_FORMATTER.asString(player).replace("%value%", builder.toString());
        };
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        final List<NameTagVisibility> visibilities = Arrays.asList(NameTagVisibility.values());
        final int currentIndex = visibilities.indexOf(this.value);
        final int nextIndex = currentIndex + 1 == visibilities.size() ? 0 : currentIndex + 1;

        this.setValue(visibilities.get(nextIndex));
    }

}
