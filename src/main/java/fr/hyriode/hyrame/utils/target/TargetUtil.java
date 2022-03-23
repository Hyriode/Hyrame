package fr.hyriode.hyrame.utils.target;

import fr.hyriode.hyrame.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 03/01/2022 at 19:26
 */
public class TargetUtil {

    /**
     * Get the entity that a player is looking at.<br>
     * This method returns a {@link TargetInfo} object with the information of the target.<br>
     * Warning: this method is assuming the entity with the same hit box as a player !
     *
     * @param player The concerned player
     * @param maxRange The maximum of range to check for a target
     * @param aimingXTolerance The tolerance of aiming (x-axis) from the player (it's a percentage, so 1.20 will be 20% for example)
     * @param aimingYTolerance The tolerance of aiming (y-axis) from the player (it's a percentage, so 1.20 will be 20% for example)
     * @param throughBlocks If <code>true</code> blocks will not be taken in account
     * @param toIgnore A list of target to ignore
     * @param ignoredBlocks A list of material to ignore
     * @return A {@link TargetInfo} object
     */
    public static TargetInfo getTarget(Player player, double maxRange, double aimingXTolerance, double aimingYTolerance, boolean throughBlocks, List<Entity> toIgnore, List<Material> ignoredBlocks) {
        final Location eyes = player.getEyeLocation();
        final Vector playerDir = eyes.getDirection();
        final Vector playerStart = eyes.toVector();
        final Vector playerEnd = playerStart.clone().add(playerDir.clone().multiply(100));

        Entity target = null;
        for(Entity entity : player.getWorld().getEntities()) {
            final Location location = entity.getLocation();
            final Vector targetPos = location.toVector();
            final Vector minimum = targetPos.clone().add(new Vector(-0.3 * aimingXTolerance, 0 * aimingYTolerance, -0.3 * aimingXTolerance));
            final Vector maximum = targetPos.clone().add(new Vector(0.3 * aimingXTolerance, 1.80 * aimingYTolerance, 0.3 * aimingXTolerance));

            if (entity == player || location.distance(eyes) > maxRange || !hasIntersection(playerStart, playerEnd, minimum, maximum) || toIgnore.contains(entity)) {
                continue;
            }

            if (target == null) {
                target = entity;
            } else {
                if (target.getLocation().distanceSquared(eyes) > location.distanceSquared(eyes)) {
                    target = entity;
                }
            }
        }

        if (target != null) {
            final double distance = eyes.distance(target.getLocation());
            final Vector vector = eyes.getDirection();

            vector.normalize().multiply(distance);

            final Location targetLocation = target.getLocation();

            if (!LocationUtil.hasBlockBetween(eyes, eyes.clone().add(vector), ignoredBlocks) || throughBlocks) {
                return new TargetInfo(target, targetLocation.distance(eyes));
            }
        }
        return null;
    }

    private static boolean hasIntersection(Vector p1, Vector p2, Vector min, Vector max) {
        final double epsilon = 0.0001f;
        final Vector d = p2.clone().subtract(p1).multiply(0.5);
        final Vector e = max.clone().subtract(min).multiply(0.5);
        final Vector c = p1.clone().add(d).subtract(min.clone().add(max).multiply(0.5));
        final Vector ad = new Vector(Math.abs(d.getX()), Math.abs(d.getY()), Math.abs(d.getZ()));

        if (Math.abs(c.getX()) > e.getX() + ad.getX()) {
            return false;
        }
        if (Math.abs(c.getY()) > e.getY() + ad.getY()) {
            return false;
        }
        if (Math.abs(c.getZ()) > e.getZ() + ad.getZ()) {
            return false;
        }

        if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY() + epsilon) {
            return false;
        }
        if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ() + epsilon) {
            return false;
        }
        return !(Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX() + epsilon);
    }

}
