package fr.hyriode.hyrame.commands;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.gameMethods.DeathMethods;
import fr.hyriode.hyrame.gameMethods.Game;
import fr.hyriode.hyrame.gameMethods.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TestCommand extends Command {

    private final Hyrame hyrame;

    public TestCommand(Hyrame hyrame) {
        super("test", "a", "/test", Arrays.asList("t"));
        this.hyrame = hyrame;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "the command has been executed");
        Game game = new Game(DeathMethods.KEEPINVENTORY, "testGame", false, true, true, 5);
        new GamePlayer(hyrame, (Player) sender, game, new Location(((Player) sender).getWorld() , 0, 100, 0));
        return true;
    }
}
