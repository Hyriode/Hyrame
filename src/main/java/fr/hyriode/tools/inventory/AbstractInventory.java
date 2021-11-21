package fr.hyriode.tools.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public abstract class AbstractInventory implements InventoryHolder {

    protected boolean cancelClickEvent = true;

    protected final Inventory inventory;
    protected final Player owner;
    protected final String name;
    protected final int size;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickConsumers;

    public AbstractInventory(Player owner, String name, int size) {
        this.owner = owner;
        this.name = name;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, this.size, this.name);
        this.clickConsumers = new HashMap<>();
    }

    public AbstractInventory(Player owner, int size) {
        this(owner, "", size);
    }

    public AbstractInventory(Player owner, String name, InventoryType inventoryType) {
        this(owner, name, inventoryType.getDefaultSize());
    }

    public AbstractInventory(Player owner, InventoryType inventoryType) {
        this(owner, "", inventoryType.getDefaultSize());
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.clickConsumers.remove(slot);
        this.inventory.setItem(slot, itemStack);
    }

    public void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        this.setItem(slot, itemStack);
        if (clickConsumer != null) this.clickConsumers.put(slot, clickConsumer);
    }

    public void addItem(ItemStack itemStack) {
        this.setItem(this.inventory.firstEmpty(), itemStack);
    }

    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        final int slot = this.inventory.firstEmpty();
        this.setItem(slot, itemStack);
        if (clickConsumer != null) this.clickConsumers.put(slot, clickConsumer);
    }

    public void setVerticalLine(int startSlot, int endSlot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        for (int i = startSlot; i <= endSlot; i += 9) {
            this.setItem(i, itemStack, clickConsumer);
        }
    }

    public void setVerticalLine(int startSlot, int endSlot, ItemStack itemStack) {
        this.setVerticalLine(startSlot, endSlot, itemStack, null);
    }

    public void setHorizontalLine(int startSlot, int endSlot, ItemStack itemStack, Consumer<InventoryClickEvent> clickConsumer) {
        for (int i = startSlot; i <= endSlot; i++) {
            this.setItem(i, itemStack, clickConsumer);
        }
    }

    public void setHorizontalLine(int startSlot, int endSlot, ItemStack itemStack) {
        this.setHorizontalLine(startSlot, endSlot, itemStack, null);
    }

    public void setFill(ItemStack itemStack) {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                this.setItem(i, itemStack);
            }
        }
    }

    public void open() {
        this.owner.openInventory(this.inventory);
    }

    public void update() {}

    protected void onOpen(InventoryOpenEvent event){}

    protected void onClose(InventoryCloseEvent event){}

    protected void onClick(InventoryClickEvent event){}

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public Player getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> getClickConsumers() {
        return this.clickConsumers;
    }

    public boolean isCancelClickEvent() {
        return this.cancelClickEvent;
    }

    public void setCancelClickEvent(boolean cancelClickEvent) {
        this.cancelClickEvent = cancelClickEvent;
    }
}
