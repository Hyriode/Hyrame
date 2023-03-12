package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:15
 */
public class StringOption extends HostOption<String> {

    private final int maxLength;

    public StringOption(HostDisplay display, String defaultValue, int maxLength) {
        super(display, defaultValue);
        this.maxLength = maxLength;
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_STRING_FORMATTER.asString(player).replace("%value%", this.value);
    }

    public StringOption(HostDisplay display, String defaultValue) {
        this(display, defaultValue, -1);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        new AnvilGUI(HyrameLoader.getHyrame().getPlugin(), player, this.value, null, false, e -> Bukkit.getScheduler().runTaskLater(IHyrame.get().getPlugin(), () -> player.openInventory(event.getInventory()), 1L), null, null, (p, input) -> {
            this.setValue(input);

            categoryGUIProvider.apply(player).open();

            HostGUI.updateAll();

            return null;
        }).open();
    }

    @Override
    public void setValue(String value) {
        if (value.length() > this.maxLength) {
            value = value.substring(0, this.maxLength);
        }

        super.setValue(value);
    }

}
