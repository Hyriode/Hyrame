package fr.hyriode.hyrame.utils;

import fr.hyriode.hyrame.IHyrame;
import org.bukkit.World;

/**
 * Created by AstFaster
 * on 23/07/2022 at 13:43
 */
public class AreaWrapper {

    private transient Area area;

    private final LocationWrapper min;
    private final LocationWrapper max;

    public AreaWrapper(LocationWrapper min, LocationWrapper max) {
        this.min = min;
        this.max = max;
    }

    public LocationWrapper getMin() {
        return this.min;
    }

    public LocationWrapper getMax() {
        return this.max;
    }

    public Area asArea() {
        return this.asArea(IHyrame.WORLD.get());
    }

    public Area asArea(World world) {
        return this.area == null ? this.area = new Area(this.max.asBukkit(world), this.min.asBukkit(world)) : this.area;
    }

}
