package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:22
 */
public abstract class HyriGameProtocol implements Listener {

    /** A list of the protocol that are required for the protocol to work */
    private final List<Class<? extends HyriGameProtocol>> requiredProtocols = new ArrayList<>();

    /** The {@link IHyrame} instance */
    protected final IHyrame hyrame;
    /** The name of the protocol */
    protected final String name;

    /**
     * Constructor of {@link HyriGameProtocol}
     *
     * @param hyrame The {@link IHyrame} instance
     * @param name A name
     */
    public HyriGameProtocol(IHyrame hyrame, String name) {
        this.hyrame = hyrame;
        this.name = name;
    }

    /**
     * Enable the protocol
     */
    abstract void enable();

    /**
     * Disable the protocol
     */
    abstract void disable();


    /**
     * Add a protocol that is required for the protocol to work
     *
     * @param requiredProtocol The {@link Class} of the required protocol
     */
    protected void require(Class<? extends HyriGameProtocol> requiredProtocol) {
        this.requiredProtocols.add(requiredProtocol);
    }

    /**
     * Get the name of the protocol
     *
     * @return A name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get all the protocols that are required for this protocol
     *
     * @return A list of {@link HyriGameProtocol}
     */
    List<Class<? extends HyriGameProtocol>> getRequiredProtocols() {
        return this.requiredProtocols;
    }

    /**
     * Get the game that handles the protocol
     *
     * @return The {@link HyriGame} instance
     */
    protected HyriGame<?> getGame() {
        return this.hyrame.getGameManager().getCurrentGame();
    }

    /**
     * Get a {@link HyriGamePlayer} from a {@link Player}
     *
     * @param player The concerned {@link Player}
     * @return The {@link HyriGamePlayer}
     */
    protected HyriGamePlayer getGamePlayer(Player player) {
        return this.getGame().getPlayer(player.getUniqueId());
    }

    /**
     * Get the {@link HyriGameProtocolManager} instance
     *
     * @return The {@link HyriGameProtocolManager} instance
     */
    protected HyriGameProtocolManager getProtocolManager() {
        return this.getGame().getProtocolManager();
    }

}
