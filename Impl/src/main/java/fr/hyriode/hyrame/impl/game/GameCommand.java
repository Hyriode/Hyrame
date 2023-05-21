package fr.hyriode.hyrame.impl.game;

import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/01/2022 at 21:04
 */
public class GameCommand extends HyriCommand<HyramePlugin> {

    public GameCommand(HyramePlugin plugin) {
        super(plugin, new CommandInfo("game")
                .withUsage(new CommandUsage().withStringMessage(player -> "/game start|end"))
                .withPermission(player -> player.getRank().isSuperior(StaffRank.DEVELOPER))
                .withDescription("Command used to start or end a running game."));
    }

    @Override
    public void handle(CommandContext ctx) {
        final CommandSender sender = ctx.getSender();
        final HyriGame<?> game = HyrameLoader.getHyrame().getGameManager().getCurrentGame();

        if (game != null) {
            ctx.registerArgument("start", output -> {
                if (game.getState() == HyriGameState.WAITING || game.getState() == HyriGameState.READY) {
                    sender.sendMessage(ChatColor.RED + "Game start forced! Warning: it can cause many issues!");
                    game.start();
                } else {
                    sender.sendMessage(ChatColor.RED + "This game is already playing!");
                }
            });

            ctx.registerArgument("end", output -> {
                if (game.getState() == HyriGameState.PLAYING) {
                    sender.sendMessage(ChatColor.RED + "Game end forced! Warning: it can cause many issues!");
                    game.end();
                } else {
                    sender.sendMessage(ChatColor.RED + "This game is not playing!");
                }
            });

            super.handle(ctx);
        } else {
            sender.sendMessage(ChatColor.RED + "No game is currently registered on this server!");
        }
    }

}
