package fr.hyriode.hyrame.signgui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriSignGUIManager {

    /** Instance of manager */
    private static HyriSignGUIManager instance;

    /** Map of all signs */
    private final Map<UUID, HyriSignGUI> signs;

    /**
     * Constructor of {@link HyriSignGUIManager}
     */
    public HyriSignGUIManager() {
        instance = this;
        this.signs = new HashMap<>();
    }

    /**
     * Add a sign gui for a player
     *
     * @param uuid - Player uuid
     * @param sign - Sign
     */
    protected void addGUI(UUID uuid, HyriSignGUI sign) {
        this.signs.put(uuid, sign);
    }

    /**
     * Get all signs
     *
     * @return - A map of signs
     */
    public Map<UUID, HyriSignGUI> getSigns() {
        return this.signs;
    }

    /**
     * Get the instance of this object
     *
     * @return - {@link HyriSignGUIManager} instance
     */
    public static HyriSignGUIManager get() {
        return instance;
    }

}
