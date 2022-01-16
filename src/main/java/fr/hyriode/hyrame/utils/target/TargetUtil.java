package fr.hyriode.hyrame.utils.target;

import fr.hyriode.hyrame.utils.LocationUtil;
import fr.hyriode.hyrame.utils.Vector3D;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 03/01/2022 at 19:26
 */
public class TargetUtil {

    /**
     * Get the player that a player is looking at.<br>
     * This method returns a {@link Player}
     *
     * @param player The concerned player
     * @param maxRange The maximum of range to check for a target
     * @return A {@link Player} or <code>null</code> if nothing was found
     */
    public static Player getTargetPlayer(Player player, double maxRange) {
        final TargetInfo info = getTarget(player, maxRange, false, new ArrayList<>());

        return info != null ? info.getPlayer() : null;
    }

    /**
     * Get the player that a player is looking at.<br>
     * This method returns a {@link TargetInfo} object with the information of the target
     *
     * @param player The concerned player
     * @param maxRange The maximum of range to check for a target
     * @return A {@link TargetInfo} object
     */
    public static TargetInfo getTarget(Player player, double maxRange) {
        return getTarget(player, maxRange, false, new ArrayList<>());
    }

    /**
     * Get the player that a player is looking at.<br>
     * This method returns a {@link TargetInfo} object with the information of the target
     *
     * @param player The concerned player
     * @param maxRange The maximum of range to check for a target
     * @param throughBlocks If <code>true</code> blocks will not be taken in account
     * @param toIgnore A list of target to ignore
     * @return A {@link TargetInfo} object
     */
    public static TargetInfo getTarget(Player player, double maxRange, boolean throughBlocks, List<Player> toIgnore) {
        final Location eyes = player.getEyeLocation();
        final Vector3D playerDir = new Vector3D(eyes.getDirection());
        final Vector3D playerStart = new Vector3D(eyes);
        final Vector3D playerEnd = playerStart.add(playerDir.multiply(100));

        Player target = null;
        for(Player p : player.getWorld().getPlayers()) {
            final Location location = p.getLocation();
            final Vector3D targetPos = new Vector3D(location);
            final Vector3D minimum = targetPos.add(-0.3, 0, -0.3);
            final Vector3D maximum = targetPos.add(0.3, 1.80, 0.3);

            if (p == player || location.distance(eyes) > maxRange || !hasIntersection(playerStart, playerEnd, minimum, maximum) || toIgnore.contains(p)) {
                continue;
            }

            if (target == null) {
                target = p;
            } else {
                if (target.getLocation().distanceSquared(eyes) > location.distanceSquared(eyes)) {
                    target = p;
                }
            }
        }

        if (target != null) {
            final Location targetLocation = target.getLocation();

            if (!LocationUtil.hasBlockBetween(eyes, targetLocation) || throughBlocks) {
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

        if(Math.abs(c.x) > e.x + ad.x) {
            return false;
        }
        if(Math.abs(c.y) > e.y + ad.y) {
            return false;
        }
        if(Math.abs(c.z) > e.z + ad.z) {
            return false;
        }

        if(Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon) {
            return false;
        }
        if(Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon) {
            return false;
        }
        return !(Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon);
    }

}
