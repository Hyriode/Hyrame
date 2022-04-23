package fr.hyriode.hyrame.impl.game.util;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class LeaveItem extends HyriItem<HyramePlugin> {

    public LeaveItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.LEAVE_NAME, displayName("item.global.leave"), Material.INK_SACK, (byte) 1);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        HyriAPI.get().getServerManager().sendPlayerToLobby(event.getPlayer().getUniqueId());
    }

}
