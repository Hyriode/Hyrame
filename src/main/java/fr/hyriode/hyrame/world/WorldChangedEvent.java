package fr.hyriode.hyrame.world;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.hyrame.HyrameLoader;

/**
 * Created by AstFaster
 * on 17/08/2022 at 21:07
 */
public class WorldChangedEvent extends HyriEvent {

    private final String oldWorld;
    private final String newWorld;

    public WorldChangedEvent(String oldWorld, String newWorld) {
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
    }

    public String getOldWorld() {
        return this.oldWorld;
    }

    public String getNewWorld() {
        return this.newWorld;
    }

    public IWorldProvider getWorldProvider() {
        return HyrameLoader.getHyrame().getWorldProvider();
    }

}
