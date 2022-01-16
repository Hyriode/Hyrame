package fr.hyriode.hyrame.generator.event;

import fr.hyriode.hyrame.generator.HyriGenerator;
import fr.hyriode.hyrame.generator.IHyriGeneratorTier;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/01/2022 at 18:48
 */
public class HyriGeneratorUpgradedEvent extends Event implements Cancellable {

    /** Spigot constant */
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;

    private final HyriGenerator generator;
    private final IHyriGeneratorTier tier;

    public HyriGeneratorUpgradedEvent(HyriGenerator generator, IHyriGeneratorTier tier) {
        this.generator = generator;
        this.tier = tier;
    }

    public HyriGenerator getGenerator() {
        return this.generator;
    }

    public IHyriGeneratorTier getTier() {
        return this.tier;
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