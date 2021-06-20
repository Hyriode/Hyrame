package fr.hyriode.hyrame.commands;

import fr.hyriode.hyrame.team.Team;
import fr.hyriode.hyrame.team.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestCommand extends Command {

    public TestCommand() {
        super("test", "a", "/test", Arrays.asList("teste"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "La commande a bien été executée");
        Team team = new Team(TeamColor.BLUE, new ArrayList(Collections.singletonList((Player) sender)), 5);
        team.giveAll(new ItemStack(Material.BED));
        return true;
    }
}
