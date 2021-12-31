package fr.hyriode.hyrame.impl.listener.model.tools;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/12/2021 at 23:37
 */
public class InventoryHandler extends HyriListener<HyramePlugin> {

    public InventoryHandler(HyramePlugin plugin) {
        super(plugin);

        Hyrame.log("Registered inventory handler.");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() instanceof HyriInventory) {
            final HyriInventory inventory = (HyriInventory) event.getInventory().getHolder();

            final int clickedSlot = event.getRawSlot();

            event.setCancelled(inventory.isCancelClickEvent());

            inventory.onClick(event);

            if (inventory.getClickConsumers().containsKey(clickedSlot)) {
                inventory.getClickConsumers().get(clickedSlot).accept(event);
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof HyriInventory) {
            final HyriInventory inventory = (HyriInventory) event.getInventory().getHolder();

            inventory.onOpen(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof HyriInventory) {
            final HyriInventory inventory = (HyriInventory) event.getInventory().getHolder();

            inventory.onClose(event);
        }
    }

}
