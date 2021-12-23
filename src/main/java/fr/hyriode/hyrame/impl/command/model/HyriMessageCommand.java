package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandOutput;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/12/2021 at 20:10
 */
public class HyriMessageCommand extends HyriCommand<HyramePlugin> {

    public HyriMessageCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("msgtest")
                .withUsage("/msgtest [message]")
                .withDescription("Command used to test messages"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final String[] args = ctx.getArgs();

        if (args.length >= 1) {
            final String input = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

            ctx.getSender().sendMessage(input);
        }
    }
}
