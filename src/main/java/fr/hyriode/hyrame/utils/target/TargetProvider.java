package fr.hyriode.hyrame.utils.target;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/02/2022 at 16:19
 */
public class TargetProvider {

    /** The {@link Player} used to get his target */
    private Player player;
    /** The maximum of range to look for targets */
    private double maxRange;
    /** The aiming tolerance. It's a percentage that will "increase" the size of the target hit box in the x-axis */
    private double aimingXTolerance = 1.0D;
    /** The aiming tolerance. It's a percentage that will "increase" the size of the target hit box in the y-axis */
    private double aimingYTolerance = 1.0D;
    /** If <code>true</code>, blocks will not be taken in account */
    private boolean throughBlocks = false;
    /** A list of {@link Entity} to ignore */
    private List<Entity> ignoredEntities = new ArrayList<>();
    /** A list of block to ignore */
    private List<Material> ignoredBlocks = new ArrayList<>();

    /**
     * Constructor of {@link TargetProvider}
     *
     * @param player The concerned {@link Player}
     * @param maxRange The maximum of range to check for a target
     */
    public TargetProvider(Player player, double maxRange) {
        this.player = player;
        this.maxRange = maxRange;
    }

    /**
     * Set the player to use
     *
     * @param player A {@link Player}
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Set the maximum of range to use
     *
     * @param maxRange A range
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    /**
     * Set the aiming tolerance (x-axis) to look for targets
     *
     * @param aimingXTolerance A percentage (ex: 1.20 will be 20%)
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withAimingXTolerance(double aimingXTolerance) {
        this.aimingXTolerance = aimingXTolerance;
        return this;
    }

    /**
     * Set the aiming tolerance (y-axis) to look for targets
     *
     * @param aimingYTolerance A percentage (ex: 1.20 will be 20%)
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withAimingYTolerance(double aimingYTolerance) {
        this.aimingYTolerance = aimingYTolerance;
        return this;
    }

    /**
     * Set the aiming tolerance to look for targets
     *
     * @param aimingTolerance A percentage (ex: 1.20 will be 20%)
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withAimingTolerance(double aimingTolerance) {
        this.aimingXTolerance = aimingTolerance;
        this.aimingYTolerance = aimingTolerance;
        return this;
    }

    /**
     * Set if blocks will be taken in account while looking for a target
     *
     * @param throughBlocks If code <code>true</code> blocks will be taken in account
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withThroughBlocks(boolean throughBlocks) {
        this.throughBlocks = throughBlocks;
        return this;
    }

    /**
     * Set the list of entities to ignore
     *
     * @param ignoredEntities A list of {@link Entity}
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withIgnoredEntities(List<Entity> ignoredEntities) {
        this.ignoredEntities = ignoredEntities;
        return this;
    }

    /**
     * Add an entity ot ignore
     *
     * @param entity An {@link Entity}
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withIgnoredEntity(Entity entity) {
        this.ignoredEntities.add(entity);
        return this;
    }

    /**
     * Set the list of blocks' material to ignore
     *
     * @param ignoredBlocks A list of {@link Material}
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withIgnoredBlocks(List<Material> ignoredBlocks) {
        this.ignoredBlocks = ignoredBlocks;
        return this;
    }

    /**
     * Add a blocks' material to ignore
     *
     * @param block A {@link Material}
     * @return This {@link TargetProvider} instance
     */
    public TargetProvider withIgnoredBlock(Material block) {
        this.ignoredBlocks.add(block);
        return this;
    }

    /**
     * Get the target with a giving condition to validate
     *
     * @param condition A {@link Condition} to validate
     * @return A {@link TargetInfo} object
     */
    public TargetInfo get(Condition condition) {
        final TargetInfo info = TargetUtil.getTarget(this.player, this.maxRange, this.aimingXTolerance, this.aimingYTolerance, this.throughBlocks, this.ignoredEntities, this.ignoredBlocks);

        if (info != null && condition.validate(info.getEntity())) {
            return info;
        }
        return null;
    }

    /**
     * Get the target without using a {@link Condition}
     *
     * @return A {@link TargetInfo} object
     */
    public TargetInfo get() {
        return this.get(Condition.NONE);
    }

    /**
     * The enumeration of all condition that can be used to validate targets
     */
    public enum Condition {

        /** The condition to look for targets that are players */
        PLAYER(entity -> entity instanceof Player),
        /** The condition to look for targets that are not players */
        NOT_PLAYER(entity -> !(entity instanceof Player)),
        /** A null condition */
        NONE(entity -> true);

        /** The validation function of the condition */
        private final Function<Entity, Boolean> validation;

        /**
         * Constructor of {@link Condition}
         *
         * @param validation The validation function
         */
        Condition(Function<Entity, Boolean> validation) {
            this.validation = validation;
        }

        /**
         * Validate the condition for an {@link Entity}
         *
         * @param entity The {@link Entity} to validate
         * @return <code>true</code> if the entity has been validated
         */
        public boolean validate(Entity entity) {
            return this.validation.apply(entity);
        }

    }

}
