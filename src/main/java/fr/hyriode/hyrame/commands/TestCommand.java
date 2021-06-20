package fr.hyriode.hyrame.commands;

import fr.hyriode.hyrame.utils.ScoreboardHelp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("test")) {
            String[] list = new  String[] {
                    ChatColor.BLUE + "test blue",
                    ChatColor.RED + "test red",
                    "test r",
                    ChatColor.BLUE + "" + ChatColor.BOLD + "Oui" + ChatColor.RESET + "" + ChatColor.MAGIC + "oui"
            };

            final Scoreboard scoreboard = ScoreboardHelp.createScoreboard( ChatColor.GOLD + "test", new ArrayList<>(Arrays.asList(list)));

            for(Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(scoreboard);
            }
            return true;
        }
        return false;
    }
}
