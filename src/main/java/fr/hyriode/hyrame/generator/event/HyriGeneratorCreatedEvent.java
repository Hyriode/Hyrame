package fr.hyriode.hyrame.generator.event;

import fr.hyriode.hyrame.generator.HyriGenerator;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/01/2022 at 18:41
 */
public class HyriGeneratorCreatedEvent extends Event implements Cancellable {

    /** Spigot constant */
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    private final HyriGenerator generator;

    public HyriGeneratorCreatedEvent(HyriGenerator generator) {
        this.generator = generator;
    }

    public HyriGenerator getGenerator() {
        return this.generator;
    }

    @Override
    public HandlerList getHandlers() {
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
