package fr.hyriode.hyrame.packet;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class PacketUtil {

    /**
     * Send a packet to a player's client
     *
     * @param player Player
     * @param packet Packet to send
     */
    public static void sendPacket(Player player, Packet<?> packet) {
        if (player instanceof CraftPlayer) {
            final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

            if (playerConnection == null) {
                return;
            }

            playerConnection.sendPacket(packet);
        }
    }

    /**
     * Send a packet to all the online players
     *
     * @param packet The packet to send
     */
    public static void sendPacket(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

}
