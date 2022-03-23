package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.inventory.HyriInventoryEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
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

            HyriAPI.get().getEventBus().publish(new HyriInventoryEvent(inventory, (Player) event.getPlayer(), HyriInventoryEvent.Action.OPEN));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof HyriInventory) {
            final HyriInventory inventory = (HyriInventory) event.getInventory().getHolder();

            inventory.onClose(event);

            HyriAPI.get().getEventBus().publish(new HyriInventoryEvent(inventory, (Player) event.getPlayer(), HyriInventoryEvent.Action.CLOSE));
        }
    }

}
