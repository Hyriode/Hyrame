package fr.hyriode.hyrame.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 27/02/2022 at 11:05
 */
public class PacketChannelHandler extends ChannelDuplexHandler {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg.getClass().getSimpleName().equals("PacketPlayOutEntityEquipment")) {
            System.out.println("Sending " + msg.getClass().getName());
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // System.out.println("Receiving " + msg.getClass().getName());
        super.channelRead(ctx, msg);
    }

}
