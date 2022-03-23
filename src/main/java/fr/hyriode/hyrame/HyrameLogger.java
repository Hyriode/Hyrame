package fr.hyriode.hyrame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 10:08
 */
public class HyrameLogger {

    /**
     * Log a message as Hyrame
     *
     * @param level The level of the message
     * @param message The message to log
     */
    public static void log(Level level, String message) {
        String prefix = ChatColor.DARK_PURPLE + "[" + IHyrame.NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    /**
     * Log a message as Hyrame
     *
     * @param message The message to log
     */
    public static void log(String message) {
        log(Level.INFO, message);
    }

}
