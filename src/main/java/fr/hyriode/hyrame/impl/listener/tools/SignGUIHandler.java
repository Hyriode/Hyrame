package fr.hyriode.hyrame.impl.listener.tools;

import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.signgui.SignGUI;
import fr.hyriode.hyrame.signgui.SignGUIManager;
import fr.hyriode.hyrame.utils.PacketUtil;
import fr.hyriode.hyrame.utils.ThreadUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class SignGUIHandler extends HyriListener<HyramePlugin> {

    public SignGUIHandler(HyramePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.inject(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.uninject(player);
    }

    private void inject(Player player) {
        final ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (packet instanceof PacketPlayInUpdateSign) {
                    final PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;
                    final Map<UUID, SignGUI> signs = SignGUIManager.get().getSigns();

                    if (signs.containsKey(player.getUniqueId())) {
                        final SignGUI sign = signs.get(player.getUniqueId());
                        final BlockPosition blockPosition = (BlockPosition) Reflection.invokeField(updateSign, "a");
                        final IChatBaseComponent[] components = updateSign.b();
                        final List<String> lines = new ArrayList<>();

                        for (IChatBaseComponent component : components) {
                            lines.add(component.getText());
                        }

                        final PacketPlayOutBlockChange blockChangePacket = new PacketPlayOutBlockChange(((CraftWorld) player.getWorld()).getHandle(), blockPosition);

                        blockChangePacket.block = Blocks.AIR.getBlockData();

                        PacketUtil.sendPacket(player, blockChangePacket);

                        ThreadUtil.backOnMainThread(plugin, () -> sign.getCompleteCallback().call(player, lines.toArray(new String[4])));

                        signs.remove(player.getUniqueId());
                    }
                }
                super.channelRead(ctx, packet);
            }
        };

        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", "SignPacketInjector", channelDuplexHandler);
    }

    private void uninject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

        if (pipeline.get("SignPacketInjector") != null) {
            pipeline.remove("SignPacketInjector");
        }
    }

}
