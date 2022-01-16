package fr.hyriode.hyrame.generator.event;

import fr.hyriode.hyrame.generator.HyriGenerator;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/01/2022 at 18:44
 */
public class HyriGeneratorDropEvent extends Event implements Cancellable {

    /** Spigot constant */
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;

    private final HyriGenerator generator;
    private final ItemStack item;

    public HyriGeneratorDropEvent(HyriGenerator generator, ItemStack item) {
        this.generator = generator;
        this.item = item;
    }

    public HyriGenerator getGenerator() {
        return this.generator;
    }

    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
