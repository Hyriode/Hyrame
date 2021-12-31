package fr.hyriode.hyrame.impl.item;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.item.ItemNBT;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 18:38
 */
public class HyriItemHandler extends HyriListener<HyramePlugin> {

    public HyriItemHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        final ItemStack itemStack = event.getItem();

        if (itemStack != null) {
            final ItemNBT nbt = new ItemNBT(itemStack);

            if (nbt.hasTag(HyriItemManager.ITEM_NBT_KEY)) {
                final IHyrame hyrame = this.plugin.getHyrame();
                final HyriItemManager itemManager = (HyriItemManager) hyrame.getItemManager();
                final HyriItem<?> item = itemManager.getItem(nbt.getString(HyriItemManager.ITEM_NBT_KEY));

                if (item != null) {
                    final Action action = event.getAction();

                    if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                        item.onLeftClick(hyrame, event);
                    } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        item.onRightClick(hyrame, event);
                    }
                }
            }
        }
    }

}
