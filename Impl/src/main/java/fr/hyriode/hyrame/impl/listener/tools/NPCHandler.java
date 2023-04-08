package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCInteractCallback;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.reflection.entity.EntityUseAction;
import fr.hyriode.hyrame.utils.ThreadUtil;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
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

                for (NPC npc : NPCManager.getNPCs()) {
                    final NPCInteractCallback callback = npc.getInteractCallback();

                    if (npc.getId() != entityId || callback == null || location.distance(npc.getLocation()) > 3.0D) {
                        continue;
                    }

                    final Object object = container.getValue("action");

                    if (object != null) {
                        ThreadUtil.backOnMainThread(plugin, () -> callback.call(object.toString().equals(EntityUseAction.INTERACT.name()), player));
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
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (NPC npc : NPCManager.getNPCs()) {
                NPCManager.sendNPC(player, npc);
            }

            this.trackPlayer(player);
        }, 2L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        for (NPC npc : NPCManager.getNPCs()) {
            NPCManager.removeNPC(player, npc);

            npc.removePlayer(player);
        }
    }

    private void checkDistance(Player player, Location from, Location to) {
        if (from == null || to == null) {
            return;
        }

        final String fromWorld = from.getWorld().getName();
        final String toWorld = to.getWorld().getName();

        if (!fromWorld.equals(toWorld)) {
            return;
        }

        for (NPC npc : NPCManager.getNPCs()) {
            final Location location = npc.getLocation();
            final String world = location.getWorld().getName();

            if (!world.equals(fromWorld)) {
                continue;
            }

            if (from.distanceSquared(location) > 2500 && to.distanceSquared(location) < 2500) {
                NPCManager.sendNPC(player, npc);
            }
        }
    }

    private void trackPlayer(Player player) {
        for (NPC npc : NPCManager.getNPCs()) {
            if (!npc.isTrackingPlayer()) {
                continue;
            }

            final Location location = npc.getLocation().clone();

            if (player.getLocation().getWorld() != location.getWorld()) {
                continue;
            }

            if (location.distance(player.getLocation()) > 4.0D) {
                continue;
            }

            final Vector direction = player.getLocation().toVector().subtract(npc.getLocation().toVector());

            location.setDirection(direction);

            PacketUtil.sendPacket(player, npc.getTeleportPacket());

            for (Packet<?> packet : npc.getRotationPackets(location.getYaw(), location.getPitch())) {
                PacketUtil.sendPacket(player, packet);
            }
        }
    }

}