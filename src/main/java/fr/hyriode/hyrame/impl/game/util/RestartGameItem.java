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
public class RestartGameItem extends HyriItem<HyramePlugin> {

    private static final HyriLanguageMessage TITLE = new HyriLanguageMessage("")
            .addValue(HyriLanguage.FR, ChatColor.GOLD + "Relancer")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "Play Again");

    public RestartGameItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.RESTART_GAME_NAME, () -> TITLE, Material.NETHER_STAR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        // TODO
    }

}
