package fr.hyriode.hyrame.impl.host.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.host.category.HostMainCategory;
import fr.hyriode.hyrame.impl.host.gui.HostMainGUI;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:57
 */
public class HostSettingsItem extends HyriItem<HyramePlugin> {

    public HostSettingsItem(HyramePlugin plugin) {
        super(plugin, "host_settings", () -> HyriLanguageMessage.get("host.item.name"), Material.REDSTONE_COMPARATOR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new HostMainGUI(event.getPlayer(), hyrame.getHostController().getCategory(HostMainCategory.NAME)).open();
    }

}
