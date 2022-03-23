package fr.hyriode.hyrame.world;

import org.bukkit.World;
import org.bukkit.WorldType;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/03/2022 at 15:36
 */
public class HyriWorldSettings {

    /** The name of the world to */
    private final String worldName;
    /** The type of the world */
    private final WorldType worldType;
    /** The environment of the world */
    private final World.Environment worldEnvironment;

    /**
     * Constructor of {@link HyriWorldSettings}
     *
     * @param worldName The name of the world
     * @param worldType The type of the world
     * @param worldEnvironment The environment of the world
     */
    public HyriWorldSettings(String worldName, WorldType worldType, World.Environment worldEnvironment) {
        this.worldName = worldName;
        this.worldType = worldType;
        this.worldEnvironment = worldEnvironment;
    }

    /**
     * Constructor of {@link HyriWorldSettings}
     *
     * @param worldName The name of the world
     */
    public HyriWorldSettings(String worldName) {
        this(worldName, WorldType.NORMAL, World.Environment.NORMAL);
    }

    /**
     * Get the name of the world.<br>
     * It will represent the name of the folder for the world in the server folder
     *
     * @return A world name
     */
    public String getWorldName() {
        return this.worldName;
    }

    /**
     * Get the type of the world: flat, amplified, normal etc
     *
     * @return A {@link WorldType}
     */
    public WorldType getWorldType() {
        return this.worldType;
    }

    /**
     * Get the environment of the world: nether, end or normal
     *
     * @return A {@link org.bukkit.World.Environment}
     */
    public World.Environment getWorldEnvironment() {
        return this.worldEnvironment;
    }

}
