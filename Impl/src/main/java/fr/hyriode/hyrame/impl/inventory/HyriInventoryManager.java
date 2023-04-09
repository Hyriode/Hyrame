package fr.hyriode.hyrame.impl.inventory;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.impl.host.gui.HostMainGUI;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.inventory.HyriInventoryEvent;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 09:12
 */
public class HyriInventoryManager implements IHyriInventoryManager {

    private final Map<UUID, HyriInventory> playersInventories;

    public HyriInventoryManager() {
        this.playersInventories = new ConcurrentHashMap<>();

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onInventoryEvent(HyriInventoryEvent event) {
        final Player player = event.getPlayer();
        final HyriInventory inventory = event.getInventory();
        final HyriInventoryEvent.Action action = event.getAction();

        if (action == HyriInventoryEvent.Action.OPEN) {
            this.playersInventories.put(player.getUniqueId(), inventory);
        } else if (action == HyriInventoryEvent.Action.CLOSE) {
            this.playersInventories.remove(player.getUniqueId());
        }
    }

    @Override
    public void closeInventories(Class<? extends HyriInventory> inventoryClass) {
        this.getInventories(inventoryClass).forEach(inventory -> inventory.getOwner().closeInventory());
    }

    @Override
    public HyriInventory getPlayerInventory(Player player) {
        return this.playersInventories.get(player.getUniqueId());
    }

    @Override
    public boolean isPlayerInventory(Player player, Class<? extends HyriInventory> inventoryClass) {
        final HyriInventory inventory = this.getPlayerInventory(player);

        return inventory != null && inventory.getClass() == inventoryClass;
    }

    @Override
    public Map<UUID, HyriInventory> getPlayersInventories() {
        return this.playersInventories;
    }

    @Override
    public <T extends HyriInventory> Map<T, UUID> getInventoriesMap(Class<T> inventoryClass) {
        final Map<T, UUID> inventories = new HashMap<>();

        for (Map.Entry<UUID, HyriInventory> entry : this.playersInventories.entrySet()) {
            final HyriInventory inventory = entry.getValue();

            if (inventory.getClass() == inventoryClass) {
                inventories.put(inventoryClass.cast(inventory), entry.getKey());
            }
        }
        return inventories;
    }

    @Override
    public <T extends HyriInventory> List<T> getInventories(Class<T> inventoryClass) {
        final List<T> inventories = new ArrayList<>();

        for (Map.Entry<UUID, HyriInventory> entry : this.playersInventories.entrySet()) {
            final HyriInventory inventory = entry.getValue();

            if (inventoryClass.isAssignableFrom(inventory.getClass())) {
                inventories.add(inventoryClass.cast(inventory));
            }
        }
        return inventories;
    }

}
