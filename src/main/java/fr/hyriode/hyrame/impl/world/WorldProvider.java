package fr.hyriode.hyrame.impl.world;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.world.IWorldProvider;
import fr.hyriode.hyrame.world.WorldChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Created by AstFaster
 * on 12/08/2022 at 09:55
 */
public class WorldProvider implements IWorldProvider {

    private String worldName;

    public WorldProvider() {
        this.worldName = "world";
    }

    @Override
    public World getCurrentWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    @Override
    public String getCurrentWorldName() {
        return this.worldName;
    }

    @Override
    public void setCurrentWorld(String worldName) {
        final String oldWorld = this.worldName;

        this.worldName = worldName;

        HyriAPI.get().getEventBus().publish(new WorldChangedEvent(oldWorld, this.worldName));
    }

}
