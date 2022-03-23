package fr.hyriode.hyrame.impl.game.util;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class SpectatorSettingsItem extends HyriItem<HyramePlugin> {

    private static final HyriLanguageMessage TITLE = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "Spectator Settings")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Paramètres Spectateur");

    public SpectatorSettingsItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.SPECTATOR_SETTINGS_NAME, () -> TITLE, Material.REDSTONE_COMPARATOR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        // TODO
    }

}
