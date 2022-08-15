package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:15
 */
public class BooleanOption extends HostOption<Boolean> {

    public BooleanOption(HostDisplay display, boolean defaultValue) {
        super(display, defaultValue);
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_BOOLEAN_FORMATTER.asString(player)
                .replace("%true_color%", String.valueOf(this.value ? ChatColor.AQUA : ChatColor.GRAY))
                .replace("%false_color%", String.valueOf(!this.value ? ChatColor.AQUA : ChatColor.GRAY));
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            this.setValue(!this.value);
        }
    }

}
