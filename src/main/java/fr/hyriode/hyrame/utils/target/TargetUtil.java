package fr.hyriode.hyrame.utils.target;

import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.hyrame.utils.Vector3D;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
     * @param aimingTolerance The tolerance of aiming from the player (it's a percentage, so 1.20 will be 20% for example)
     * @param throughBlocks If <code>true</code> blocks will not be taken in account
     * @param toIgnore A list of target to ignore
     * @param ignoredBlocks A list of material to ignore
     * @return A {@link TargetInfo} object
     */
    public static TargetInfo getTarget(Player player, double maxRange, double aimingTolerance, boolean throughBlocks, List<Entity> toIgnore, List<Material> ignoredBlocks) {
        final Location eyes = player.getEyeLocation();
        final Vector3D playerDir = new Vector3D(eyes.getDirection());
        final Vector3D playerStart = new Vector3D(eyes);
        final Vector3D playerEnd = playerStart.add(playerDir.multiply(100));

        Entity target = null;
        for(Entity entity : player.getWorld().getEntities()) {
            final Location location = entity.getLocation();
            final Vector3D targetPos = new Vector3D(location);
            final Vector3D minimum = targetPos.add(-0.3 * aimingTolerance, 0 * aimingTolerance, -0.3 * aimingTolerance);
            final Vector3D maximum = targetPos.add(0.3 * aimingTolerance, 1.80 * aimingTolerance, 0.3 * aimingTolerance);

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

    private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;
        final Vector3D d = p2.subtract(p1).multiply(0.5);
        final Vector3D e = max.subtract(min).multiply(0.5);
        final Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        final Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x) {
            return false;
        }
        if (Math.abs(c.y) > e.y + ad.y) {
            return false;
        }
        if (Math.abs(c.z) > e.z + ad.z) {
            return false;
        }

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon) {
            return false;
        }
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) {
            return false;
        }
        return !(Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon);
    }

}
