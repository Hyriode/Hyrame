package fr.hyriode.hyrame.impl.command.model.game;

import fr.hyriode.api.rank.type.HyriStaffRankType;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
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
        super(plugin, new HyriCommandInfo("game")
                .withType(HyriCommandType.ALL)
                .withUsage("/game start|end")
                .withPermission(player -> player.getRank().isSuperior(HyriStaffRankType.DEVELOPER))
                .withDescription("Command used to start or end a running game.")
                .withType(HyriCommandType.PLAYER));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final CommandSender sender = ctx.getSender();
        final HyriGame<?> game = this.plugin.getHyrame().getGameManager().getCurrentGame();

        if (game != null) {
            this.handleArgument(ctx, "start", output -> {
                if (game.getState() == HyriGameState.WAITING || game.getState() == HyriGameState.READY) {
                    sender.sendMessage(ChatColor.RED + "Game start forced! Warning: it can cause many issues!");
                    game.start();
                } else {
                    sender.sendMessage(ChatColor.RED + "This game is already playing!");
                }
            });

            this.handleArgument(ctx, "end", output -> {
                if (game.getState() == HyriGameState.PLAYING) {
                    sender.sendMessage(ChatColor.RED + "Game end forced! Warning: it can cause many issues!");
                    game.end();
                } else {
                    sender.sendMessage(ChatColor.RED + "This game is not playing!");
                }
            });
        } else {
            sender.sendMessage(ChatColor.RED + "No game is currently registered on this server!");
        }
    }

}
