package fr.hyriode.hyrame.impl.listener.global;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 27/02/2022 at 10:57
 */
public class PacketInterceptor {

    private static final String CHANNEL_NAME = "PacketInterceptor";

    private Channel channel;

    private final Player player;

    public PacketInterceptor(Player player) {
        this.player = player;

        this.inject();
    }

    private void inject() {
        this.channel = ((CraftPlayer) this.player).getHandle().playerConnection.networkManager.channel;
        this.channel.pipeline().addAfter("decoder", CHANNEL_NAME, new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) {
                readPacket(packet);
            }
        });
    }

    public void uninject() {
        if (this.channel.pipeline().get(CHANNEL_NAME) != null) {
            this.channel.pipeline().remove(CHANNEL_NAME);
        }
    }

    private void readPacket(Packet<?> packet) {
        System.out.println(packet.getClass());
    }

}
