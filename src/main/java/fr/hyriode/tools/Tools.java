package fr.hyriode.tools;

import fr.hyriode.tools.bossbar.BossBarManager;
import fr.hyriode.tools.inventory.InventoryHandler;
import fr.hyriode.tools.npc.NPCManager;
import fr.hyriode.tools.signgui.SignGUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/11/2021 at 09:53
 */
public class Tools {

    public static void setup(JavaPlugin plugin, Supplier<Jedis> jedisSupplier) {
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();
        final ChatColor color = ChatColor.GREEN;

        sender.sendMessage(color +  "  _____         _    ");
        sender.sendMessage(color +  " |_   _|__  ___| |___");
        sender.sendMessage(color +  "   | |/ _ \\/ _ \\ (_-<");
        sender.sendMessage(color + "   |_|\\___/\\___/_/__/");

        log("Loading tools...");

        new NPCManager(plugin, jedisSupplier, "npcSkins:");
        new BossBarManager(plugin);
        new SignGUIManager(plugin);
        new InventoryHandler(plugin);
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.GREEN + "[Tools] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

}
