package fr.hyriode.hyrame.utils.target;

import org.bukkit.entity.Entity;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/02/2022 at 16:51
 */
public class TargetInfo {

    /** The target found */
    private final Entity entity;
    /** The distance between the entity and the target */
    private final double distance;

    /**
     * Constructor of {@link TargetInfo}
     *
     * @param entity The target found
     * @param distance The distance
     */
    public TargetInfo(Entity entity, double distance) {
        this.entity = entity;
        this.distance = distance;
    }

    /**
     * Get the target found
     *
     * @return An object that inherits of {@link Entity}
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Get the distance between the target and the entity
     *
     * @return A distance
     */
    public double getDistance() {
        return this.distance;
    }

}
