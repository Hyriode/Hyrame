package fr.hyriode.tools;

import fr.hyriode.tools.bossbar.BossBarManager;
import fr.hyriode.tools.inventory.InventoryHandler;
import fr.hyriode.tools.npc.NPCManager;
import fr.hyriode.tools.signgui.SignGUIManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/11/2021 at 09:53
 */
public class Tools {

    public static void setup(JavaPlugin plugin, Logger logger, Supplier<Jedis> jedisSupplier) {
        logger.log(Level.INFO, "Registering tools...");

        new NPCManager(plugin, jedisSupplier, "npcSkins:");
        new BossBarManager(plugin);
        new SignGUIManager(plugin);
        new InventoryHandler(plugin);
    }

}
