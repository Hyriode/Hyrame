package fr.hyriode.hyrame.impl.packet;

import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.IPacketInterceptor;
import fr.hyriode.hyrame.packet.PacketType;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:23
 */
public class PacketInterceptor implements IPacketInterceptor {

    private static final String PACKET_HANDLER_KEY = "packet_handler";
    private static final String KEY = "hyriode_interceptor";

    private boolean running;

    private final List<PacketContext> contexts;

    public PacketInterceptor() {
        this.contexts = new ArrayList<>();

        PacketType.init();

        this.startIntercepting();
    }

    public void startIntercepting() {
        HyrameLogger.log("Starting intercepting packets.");

        this.running = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.injectChannel(player);
        }
    }

    public void stopIntercepting() {
        HyrameLogger.log("Stopping intercepting packets.");

        if (!this.running) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.uninjectChannel(player);
        }
    }

    public void injectChannel(Player player) {
        final ChannelPipeline pipeline = this.getPlayerPipeline(player);

        if (pipeline.get(PACKET_HANDLER_KEY) != null) {
            pipeline.addBefore(PACKET_HANDLER_KEY, KEY, new ChannelHandler(player));
        }
    }

    public void uninjectChannel(Player player) {
        final ChannelPipeline pipeline = this.getPlayerPipeline(player);

        if (pipeline.get(KEY) != null) {
            pipeline.remove(KEY);
        }
    }

    private ChannelPipeline getPlayerPipeline(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
    }

    boolean onPacketReceive(Player player, Object packet) {
        final PacketType packetType = this.getPacketTypeFromPacket(packet);

        if (packetType == null) {
            return false;
        }

        final PacketContainer container = new PacketContainer(packetType, packet, player);

        for (IPacketHandler handler : this.getHandlers(packetType)) {
            handler.onReceive(container);
        }
        return container.isCancelled();
    }

    boolean onPacketSend(Player player, Object packet) {
        final PacketType packetType = this.getPacketTypeFromPacket(packet);

        if (packetType == null) {
            return false;
        }

        final PacketContainer container = new PacketContainer(packetType, packet, player);

        for (IPacketHandler handler : this.getHandlers(packetType)) {
            handler.onSend(container);
        }
        return container.isCancelled();
    }

    private PacketType getPacketTypeFromPacket(Object packet) {
        for (PacketType packetType : PacketType.values()) {
            if (packetType.getPacketName().equals(packet.getClass().getSimpleName())) {
                return packetType;
            }
        }
        return null;
    }

    @Override
    public void addHandler(PacketType packetType, IPacketHandler handler) {
        this.contexts.add(new PacketContext(packetType, handler));
    }

    @Override
    public void removeHandler(IPacketHandler handler) {
        this.contexts.removeIf(context -> context.getHandler().equals(handler));
    }

    @Override
    public List<IPacketHandler> getHandlers() {
        final List<IPacketHandler> handlers = new ArrayList<>();

        for (PacketContext context : this.contexts) {
            handlers.add(context.getHandler());
        }
        return handlers;
    }

    public List<IPacketHandler> getHandlers(PacketType packetType) {
        final List<IPacketHandler> handlers = new ArrayList<>();

        for (PacketContext context : this.contexts) {
            final PacketType ctxPacketType = context.getType();

            if (ctxPacketType.equals(packetType) || ctxPacketType == PacketType.ALL) {
                handlers.add(context.getHandler());
            }
        }
        return handlers;
    }

    private class ChannelHandler extends ChannelDuplexHandler {

        private final Player owner;

        public ChannelHandler(Player owner) {
            this.owner = owner;
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
            if (net.minecraft.server.v1_8_R3.Packet.class.isAssignableFrom(packet.getClass())) {
                if (onPacketSend(this.owner, packet)) {
                    return;
                }
            }
            super.write(ctx, packet, promise);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
            if (net.minecraft.server.v1_8_R3.Packet.class.isAssignableFrom(packet.getClass())) {
                if (onPacketReceive(this.owner, packet)) {
                    return;
                }
            }
            super.channelRead(ctx, packet);
        }

    }

}
