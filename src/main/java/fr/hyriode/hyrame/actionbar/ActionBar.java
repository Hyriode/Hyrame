package fr.hyriode.hyrame.actionbar;

import fr.hyriode.hyrame.packet.PacketUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ActionBar {

    /** Permanent message task */
    private final Map<UUID, BukkitTask> permanentMessageTasks;

    /** Players with the action bar */
    private final Set<UUID> players;

    /** Message to display */
    private String message;

    /**
     * Constructor of {@link ActionBar}
     *
     * @param message - Action bar message
     */
    public ActionBar(String message) {
        this.message = message;
        this.players = new ConcurrentSet<>();
        this.permanentMessageTasks = new HashMap<>();
    }

    public void send(Player player) {
        PacketUtil.sendPacket(player, this.getPacket());

        this.players.add(player.getUniqueId());
    }

    public void send() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.send(player);
        }
    }

    public void sendPermanent(JavaPlugin plugin, Player player) {
        this.permanentMessageTasks.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                send(player);
            }
        }.runTaskTimer(plugin, 0, 20));
    }

    public void sendPermanent(JavaPlugin plugin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.sendPermanent(plugin, player);
        }
    }

    public void remove(Player player) {
        final BukkitTask task = this.permanentMessageTasks.remove(player.getUniqueId());

        if (task != null) {
            task.cancel();
        }

        PacketUtil.sendPacket(player, new PacketPlayOutChat(new ChatComponentText(""), (byte) 2));

        this.players.remove(player.getUniqueId());
    }

    public void remove() {
        for (UUID uuid : this.players) {
            final Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                this.remove(player);
            }
        }
    }

    private Packet<?> getPacket() {
        return new PacketPlayOutChat(new ChatComponentText(this.message), (byte) 2);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
