package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCInteractCallback;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class NPCHandler extends HyriListener<HyramePlugin> {

    public NPCHandler(HyramePlugin plugin) {
        super(plugin);

        this.plugin.getHyrame().getPacketInterceptor().addHandler(PacketType.Play.Client.USE_ENTITY, new IPacketHandler() {
            @Override
            public void onReceive(IPacketContainer container) {
                final Player player = container.getPlayer();
                final Location location = player.getLocation();
                final int entityId = container.getIntegers().read(0);

                for (NPC npc : NPCManager.getNPCs().keySet()) {
                    final NPCInteractCallback callback = npc.getInteractCallback();

                    if (npc.getId() != entityId || callback == null || location.distance(npc.getLocation()) > 3.0D) {
                        continue;
                    }

                    final Object object = container.getValue("action");

                    if (object != null) {
                        npc.getInteractCallback().call(object.toString().equals("INTERACT"), player);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        this.checkDistance(player, event.getFrom(), event.getTo());
        this.trackPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        this.checkDistance(player, event.getFrom(), event.getTo());
        this.trackPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        this.checkDistance(player, player.getLocation(), event.getRespawnLocation());
        this.trackPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        for (NPC npc : NPCManager.getNPCs().keySet()) {
            NPCManager.sendNPC(player, npc);
        }

        this.trackPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        for (NPC npc : NPCManager.getNPCs().keySet()) {
            NPCManager.removeNPC(player, npc);

            npc.removePlayer(player);
        }
    }

    private void checkDistance(Player player, Location from, Location to) {
        for (NPC npc : NPCManager.getNPCs().keySet()) {
            final Location location = npc.getLocation();

            if (from.distanceSquared(location) > 2500 && to.distanceSquared(location) < 2500) {
                NPCManager.sendNPC(player, npc);
            }
        }
    }

    private void trackPlayer(Player player) {
        for (NPC npc : NPCManager.getNPCs().keySet()) {
            if (!npc.isTrackingPlayer()) {
                continue;
            }

            final Location location = npc.getLocation();

            if (location.distance(player.getLocation()) > 4.0D) {
                continue;
            }

            final Vector direction = player.getLocation().toVector().subtract(npc.getLocation().toVector());

            location.setDirection(direction);

            npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            PacketUtil.sendPacket(player, npc.getTeleportPacket());

            for (Packet<?> packet : npc.getRotationPackets(location.getYaw(), location.getPitch())) {
                PacketUtil.sendPacket(player, packet);
            }
        }
    }

}