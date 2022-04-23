package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.PacketUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class NPCHandler extends HyriListener<HyramePlugin> {

    private static final String CHANNEL_HANDLER_NAME = "NPCPacketInjector";

    public NPCHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        this.checkDistance(player, event.getFrom(), event.getTo());
        this.trackPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (NPC npc : NPCManager.getNPCs().keySet()) {
                    NPCManager.sendNPC(player, npc);
                }

                trackPlayer(player);
            }
        }.runTaskLater(this.plugin, 20);
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

        this.inject(player);

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
        }

        this.uninject(player);
    }

    private void trackPlayer(Player player) {
        for (NPC npc : NPCManager.getNPCs().keySet()) {
            if (npc.isTrackingPlayer()) {
                final Location location = npc.getLocation();

                if (location.distance(player.getLocation()) <= 4.0D) {
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
    }

    private void checkDistance(Player player, Location from, Location to) {
        for (NPC npc : NPCManager.getNPCs().keySet()) {
            if (from.distance(npc.getLocation()) > 100 && to.distanceSquared(npc.getLocation()) < 100) {
                NPCManager.sendNPC(player, npc);
            } else if (from.distanceSquared(npc.getLocation()) < 100 && to.distanceSquared(npc.getLocation()) > 100) {
                NPCManager.removeNPC(player, npc);
            }
        }
    }

    private void inject(Player player) {
        final ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (packet == null || packet.getClass() != PacketPlayInUseEntity.class) {
                    super.channelRead(ctx, packet);
                    return;
                }

                final Object entityId = Reflection.invokeField(packet, "a");

                if (entityId instanceof Integer) {
                    for (NPC npc : NPCManager.getNPCs().keySet()) {
                        if (npc.getId() == (int) entityId) {
                            if (npc.getInteractCallback() != null) {
                                if (player.getLocation().distance(npc.getLocation()) <= 3.0D) {
                                    final PacketPlayInUseEntity.EnumEntityUseAction action = (PacketPlayInUseEntity.EnumEntityUseAction) Reflection.invokeField(packet, "action");

                                    if (action != null) {
                                        npc.getInteractCallback().call(action.equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT), player);
                                    }
                                }
                            }
                        }
                    }
                }

                super.channelRead(ctx, packet);
            }
        };

        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", CHANNEL_HANDLER_NAME, channelDuplexHandler);
    }

    private void uninject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        if (pipeline.get(CHANNEL_HANDLER_NAME) != null) {
            pipeline.remove(CHANNEL_HANDLER_NAME);
        }
    }

}