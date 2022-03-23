package fr.hyriode.hyrame.impl.command.model.debug;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.EHyriRank;
import fr.hyriode.api.rank.HyriPermission;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 09:45
 */
public class HyriDebugCommand extends HyriCommand<HyramePlugin> {

    private enum Permission implements HyriPermission {
        USE
    }

    public HyriDebugCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("hyridebug")
                .withDescription("Debug command for Hyriode (providers, items, server, game, etc)")
                .withUsage("/hyridebug providers|items|server|game")
                .withPermission(Permission.USE));

        Permission.USE.add(EHyriRank.ADMINISTRATOR);
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final CommandSender sender = ctx.getSender();

        this.handleArgument(ctx, "providers", output -> {
            sender.sendMessage("== Hyrame Providers ==");

            for (IPluginProvider provider : plugin.getHyrame().getPluginProviders()) {
                sender.sendMessage("* " + provider.getClass().getName());
            }
        });
        this.handleArgument(ctx, "items", output -> {
            sender.sendMessage("== Items ==");

            for (Map.Entry<String, HyriItem<?>> item : plugin.getHyrame().getItemManager().getItems().entrySet()) {
                sender.sendMessage("* " + item.getKey() + ": " + this.getFormattedItemDescription(sender, item.getValue().getDescription().get()));
            }
        });
        this.handleArgument(ctx, "server", output -> {
            final IHyriServer server = HyriAPI.get().getServer();

            sender.sendMessage("== Server ==");
            sender.sendMessage("* Name: " + server.getName());
            sender.sendMessage("* Type: " + server.getType());
            sender.sendMessage("* Started time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(server.getStartedTime())));
        });
        this.handleArgument(ctx, "game", output -> {
            final HyriGame<?> game = plugin.getHyrame().getGameManager().getCurrentGame();

            sender.sendMessage("== Game ==");

            if (game != null) {
                sender.sendMessage("* Name: " + game.getName());
                sender.sendMessage("* Display name: " + game.getDisplayName());
                sender.sendMessage("* State: " + game.getState());
            } else {
                sender.sendMessage("Warning: No game is registered on this server");
            }
        });
    }

    private String getFormattedItemDescription(CommandSender sender, List<HyriLanguageMessage> description) {
        if (description.size() > 0) {
            if (sender instanceof Player) {
                return description.stream().map(line -> " " + line.getForPlayer((Player) sender)).collect(Collectors.joining()).substring(1);
            } else {
                return description.stream().map(line -> " " + line.getValue(HyriLanguage.EN)).collect(Collectors.joining()).substring(1);
            }
        } else {
            return "None";
        }
    }

}
