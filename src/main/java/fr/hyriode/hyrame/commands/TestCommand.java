package fr.hyriode.hyrame.commands;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.gamemethods.DeathMethods;
import fr.hyriode.hyrame.gamemethods.Game;
import fr.hyriode.hyrame.gamemethods.GameManager;
import fr.hyriode.hyrame.gamemethods.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TestCommand extends HyriCommand {

    public TestCommand() {
        super("test", "a", "/test", Arrays.asList("t"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "the command has been executed");
        Game game = new Game(false, DeathMethods.KEEP_INVENTORY, "testGame", false, true, true, 5);
        new GamePlayer((Player) sender, GameManager.getGameByName("testGame"));
        return true;
    }
}
