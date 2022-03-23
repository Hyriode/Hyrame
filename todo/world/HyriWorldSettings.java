package fr.hyriode.hyrame.world;

import com.google.gson.Gson;
import org.bukkit.World;
import org.bukkit.WorldType;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/03/2022 at 15:36
 */
public class HyriWorldSettings {

    private final String worldName;
    private final WorldType worldType;
    private final World.Environment worldEnvironment;

    public HyriWorldSettings(String worldName, WorldType worldType, World.Environment worldEnvironment) {
        this.worldName = worldName;
        this.worldType = worldType;
        this.worldEnvironment = worldEnvironment;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public World.Environment getWorldEnvironment() {
        return this.worldEnvironment;
    }

}
