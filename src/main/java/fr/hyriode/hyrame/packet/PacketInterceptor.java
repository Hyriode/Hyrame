package fr.hyriode.hyrame.packet;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
        this.channel.pipeline().addBefore("packet_handler", CHANNEL_NAME, new PacketChannelHandler());
    }

    public void uninject() {
        if (this.channel.pipeline().get(CHANNEL_NAME) != null) {
            this.channel.pipeline().remove(CHANNEL_NAME);
        }
    }

}
