package fr.hyriode.tools.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class InventoryHandler implements Listener {

    public InventoryHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() instanceof AbstractInventory) {
            final AbstractInventory inventory = (AbstractInventory) event.getInventory().getHolder();
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
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof AbstractInventory) {
            final AbstractInventory inventory = (AbstractInventory) event.getInventory().getHolder();

            inventory.onOpen(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof AbstractInventory) {
            final AbstractInventory inventory = (AbstractInventory) event.getInventory().getHolder();

            inventory.onClose(event);
        }
    }
}
