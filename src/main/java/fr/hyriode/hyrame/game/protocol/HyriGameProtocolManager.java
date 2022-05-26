package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.event.protocol.HyriGameProtocolDisabledEvent;
import fr.hyriode.hyrame.game.event.protocol.HyriGameProtocolEnabledEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 25/02/2022 at 18:59
 */
public class HyriGameProtocolManager {

    /** The map of all the {@link HyriGameProtocol} linked with their name */
    private final Map<String, HyriGameProtocol> activeProtocols;

    /** The {@link JavaPlugin} instance */
    private final JavaPlugin plugin;
    /** The {@link HyriGame} instance linked to the protocol manager */
    private final HyriGame<?> game;

    /**
     * Constructor of {@link HyriGameProtocolManager}
     *
     * @param game The linked {@link HyriGame} instance
     */
    public HyriGameProtocolManager(JavaPlugin plugin, HyriGame<?> game) {
        this.plugin = plugin;
        this.game = game;
        this.activeProtocols = new ConcurrentHashMap<>();
    }

    /**
     * Enable a given protocol
     *
     * @param protocol The {@link HyriGameProtocol} to enable
     * @return <code>true</code> if the protocol has been enabled, else it means that the protocol is already active
     */
    public boolean enableProtocol(HyriGameProtocol protocol) {
        final String protocolName = protocol.getName();

        if (!this.activeProtocols.containsKey(protocolName)) {
            protocol.enable();

            protocol.getRequiredProtocols().forEach(protocolClass -> {
                if (this.getProtocol(protocolClass) == null) {
                    throw new RuntimeException("Cannot enable '" + protocolName + "' protocol because it requires '" + protocolClass.getName() + "' protocol it's not enabled! Please enable it to fix this issue.");
                }
            });

            this.plugin.getServer().getPluginManager().registerEvents(protocol, this.plugin);

            this.activeProtocols.put(protocolName, protocol);

            HyriAPI.get().getEventBus().publish(new HyriGameProtocolEnabledEvent(this.game, protocol));

            return true;
        }
        return false;
    }

    /**
     * Disable a protocol by giving its name
     *
     * @param protocolName The name of the {@link HyriGameProtocol} to disable
     * @return <code>true</code> if the protocol has been disabled, else it means that the protocol is not active
     */
    public boolean disableProtocol(String protocolName) {
        final HyriGameProtocol protocol = this.activeProtocols.remove(protocolName);

        if (protocol != null) {
            HandlerList.unregisterAll(protocol);

            HyriAPI.get().getEventBus().unregister(protocol);
            HyriAPI.get().getNetworkManager().getEventBus().unregister(protocol);

            protocol.disable();

            HyriAPI.get().getEventBus().publish(new HyriGameProtocolDisabledEvent(this.game, protocol));

            return true;
        }
        return false;
    }

    /**
     * Disable a protocol by giving its class
     *
     * @param protocolClass The class of the {@link HyriGameProtocol} to disable
     * @return <code>true</code> if the protocol has been disabled, else it means that the protocol is not active
     */
    public boolean disableProtocol(Class<? extends HyriGameProtocol> protocolClass) {
        final HyriGameProtocol protocol = this.getProtocol(protocolClass);

        if (protocol != null && this.canDisableProtocol(protocol)) {
            return this.disableProtocol(protocol.getName());
        }
        return false;
    }

    /**
     * Disable the protocol manager by disabling all the current active protocols
     */
    public void disable() {
        this.activeProtocols.values().forEach(protocol -> this.disableProtocol(protocol.getClass()));
    }

    /**
     * Get a protocol by giving its name
     *
     * @param protocolName The name of protocol to get
     * @return A {@link HyriGameProtocol} instance
     */
    public HyriGameProtocol getProtocol(String protocolName) {
        return this.activeProtocols.get(protocolName);
    }

    /**
     * Get a protocol by giving its class
     *
     * @param protocolClass The class of protocol to get
     * @return A {@link HyriGameProtocol} instance
     */
    public <T extends HyriGameProtocol> T getProtocol(Class<T> protocolClass) {
        for (HyriGameProtocol protocol : this.activeProtocols.values()) {
            if (protocol.getClass() == protocolClass) {
                return protocolClass.cast(protocol);
            }
        }
        return null;
    }

    /**
     * Check if a protocol is active by giving its name
     *
     * @param protocolName The name of the protocol
     * @return <code>true</code> if the protocol is active
     */
    public boolean isProtocolActive(String protocolName) {
        return this.getProtocol(protocolName) != null;
    }

    /**
     * Check if a protocol is active by giving its class
     *
     * @param protocolClass The class of the protocol
     * @return <code>true</code> if the protocol is active
     */
    public boolean isProtocolActive(Class<? extends HyriGameProtocol> protocolClass) {
        return this.getProtocol(protocolClass) != null;
    }

    /**
     * Get all current actives protocols
     *
     * @return The map of {@link HyriGameProtocol}
     */
    public Map<String, HyriGameProtocol> getProtocols() {
        return this.activeProtocols;
    }

    private boolean canDisableProtocol(HyriGameProtocol gameProtocol) {
        for (HyriGameProtocol protocol : this.activeProtocols.values()) {
            if (protocol.getRequiredProtocols().contains(gameProtocol.getClass())) {
                return false;
            }
        }
        return true;
    }

}
