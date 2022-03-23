package fr.hyriode.hyrame.impl.item;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemNBT;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
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
            final HyriItem<?> item = this.checkItem(itemStack);

            if (item != null) {
                final IHyrame hyrame = this.plugin.getHyrame();
                final Action action = event.getAction();

                if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                    item.onLeftClick(hyrame, event);
                } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    item.onRightClick(hyrame, event);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        final ItemStack itemStack = event.getCurrentItem();

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            final HyriItem<?> item = this.checkItem(itemStack);

            if (item != null) {
                item.onInventoryClick(this.plugin.getHyrame(), event);
            }
        }
    }

    private HyriItem<?> checkItem(ItemStack itemStack) {
        final ItemNBT nbt = new ItemNBT(itemStack);

        if (nbt.hasTag(HyriItemManager.ITEM_NBT_KEY)) {
            return this.plugin.getHyrame().getItemManager().getItem(nbt.getString(HyriItemManager.ITEM_NBT_KEY));
        }
        return null;
    }

}
