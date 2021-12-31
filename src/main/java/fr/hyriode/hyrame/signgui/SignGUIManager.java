package fr.hyriode.hyrame.signgui;

import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class SignGUIManager {

    /** Instance of manager */
    private static SignGUIManager instance;

    /** Map of all signs */
    private static final Map<UUID, SignGUI> SIGNS = new HashMap<>();

    /**
     * Constructor of {@link SignGUIManager}
     *
     * @param plugin - Spigot plugin
     */
    public SignGUIManager(JavaPlugin plugin) {
        instance = this;

        Hyrame.log("Registered SignGUI manager.");
    }

    /**
     * Add a sign gui for a player
     *
     * @param uuid - Player uuid
     * @param sign - Sign
     */
    protected void addGUI(UUID uuid, SignGUI sign) {
        SIGNS.put(uuid, sign);
    }

    /**
     * Get all signs
     *
     * @return - A map of signs
     */
    public static Map<UUID, SignGUI> getSigns() {
        return SIGNS;
    }

    /**
     * Get the instance of this object
     *
     * @return - {@link SignGUIManager} instance
     */
    public static SignGUIManager get() {
        return instance;
    }

}
