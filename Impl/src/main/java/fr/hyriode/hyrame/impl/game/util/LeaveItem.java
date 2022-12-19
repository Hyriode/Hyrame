package fr.hyriode.hyrame.impl.game.util;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class LeaveItem extends HyriItem<HyramePlugin> {

    public LeaveItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.LEAVE_NAME, () -> HyriLanguageMessage.get("item.global.leave"), null, new ItemStack(Material.INK_SACK, 1, (short) 1));
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        HyriAPI.get().getLobbyAPI().sendPlayerToLobby(event.getPlayer().getUniqueId());
    }

}
