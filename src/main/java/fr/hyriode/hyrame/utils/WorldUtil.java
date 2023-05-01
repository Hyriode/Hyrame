package fr.hyriode.hyrame.utils;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 01/05/2023 at 16:22
 */
public class WorldUtil {

    public static void createExplosion(Location location, int power, boolean fire) {
        location.getWorld().playSound(location, Sound.FUSE, 0.8F, 0.1F);

        for (int i = 0; i < power; i++) {
            final TNTPrimed tnt = (TNTPrimed) IHyrame.WORLD.get().spawnEntity(location, EntityType.PRIMED_TNT);

            if (fire) {
                tnt.setIsIncendiary(true);
            }

            PacketUtil.sendPacket(new PacketPlayOutEntityDestroy(tnt.getEntityId()));

            tnt.setFuseTicks(0);
        }
    }

    public static List<Chunk> getChunksAround(Chunk origin, int radius) {
        final int length = (radius * 2) + 1;
        final List<Chunk> chunks = new ArrayList<>(length * length);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(origin.getWorld().getChunkAt(origin.getX() + x, origin.getZ() + z));
            }
        }
        return chunks;
    }

}