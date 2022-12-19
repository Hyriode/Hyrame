package fr.hyriode.hyrame.impl.host.option;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AstFaster
 * on 02/08/2022 at 22:09
 */
public class TeamNameOption extends HostOption<String> {

    private final Map<HyriLanguage, String> oldValues;

    private final HyriGameTeam team;
    private final HyriLanguageMessage teamName;

    public TeamNameOption(HostDisplay display, HyriGameTeam team) {
        super(display, null);
        this.team = team;
        this.teamName = team.getDisplayName();
        this.oldValues = new HashMap<>(this.teamName.getValues());
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_TEAM_NAME_FORMATTER.asString(player)
                .replace("%color%", String.valueOf(this.team.getColor().getChatColor()))
                .replace("%value%", this.value == null ? this.teamName.getValue(player) : this.value);
        this.onChanged = teamName -> {
            this.teamName.getValues().clear();
            this.teamName.addValue(HyriLanguage.EN, teamName);

            this.getGame().getTabListManager().updateTeam(team);
        };
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        new AnvilGUI(HyrameLoader.getHyrame().getPlugin(), player, this.value == null ? this.teamName.getValue(HyriLanguage.FR) : this.value, null, false, e -> player.openInventory(event.getInventory()), null, null, (p, input) -> {
            this.setValue(input);

            TeamChooserGUI.refresh(this.getHyrame());

            categoryGUIProvider.apply(player).open();

            return null;
        }).open();
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            this.value = null;
        } else if (value.length() > 16) {
            value = value.substring(0, 16);
        }

        super.setValue(value);
    }

    @Override
    public void reset() {
        this.teamName.getValues().clear();

        for (Map.Entry<HyriLanguage, String> entry : this.oldValues.entrySet()) {
            this.teamName.addValue(entry.getKey(), entry.getValue());
        }

        this.value = null;

        this.getGame().getTabListManager().updateTeam(team);
    }

}
