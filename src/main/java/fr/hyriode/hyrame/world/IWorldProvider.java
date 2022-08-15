package fr.hyriode.hyrame.world;

import org.bukkit.World;

/**
 * Created by AstFaster
 * on 12/08/2022 at 09:52
 */
public interface IWorldProvider {

    /**
     * Get the current world of the server
     *
     * @return A {@link World} instance
     */
    World getCurrentWorld();

    /**
     * Get the name of the current world
     *
     * @return A world name
     */
    String getCurrentWorldName();

    /**
     * Set the current world
     *
     * @param worldName The name of the new current world
     */
    void setCurrentWorld(String worldName);

}
