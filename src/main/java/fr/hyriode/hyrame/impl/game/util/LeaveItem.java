package fr.hyriode.hyrame.impl.game.util;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class LeaveItem extends HyriItem<HyramePlugin> {

    private static final HyriLanguageMessage EXIT_MESSAGE = new HyriLanguageMessage("exit")
            .addValue(HyriLanguage.FR, ChatColor.RED + "Quitter")
            .addValue(HyriLanguage.EN, ChatColor.RED + "Exit");

    public LeaveItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.LEAVE_NAME, () -> EXIT_MESSAGE, Material.INK_SACK, (byte) 1);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        HyriAPI.get().getServerManager().sendPlayerToLobby(event.getPlayer().getUniqueId());
    }

}
