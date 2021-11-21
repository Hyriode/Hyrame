package fr.hyriode.hyrame.impl.command.model.debug;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandArgument;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriPermission;
import fr.hyriode.hyriapi.server.IHyriServer;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/11/2021 at 22:20
 */
public class HyriDebugCommand extends HyriCommand<HyramePlugin> {

    private enum Permission implements HyriPermission {
        DEBUG_COMMAND
    }

    public HyriDebugCommand(HyramePlugin plugin) {
        super(plugin, "hyridebug", "Debug command for Hyriode (providers, items, server, game, etc)", Permission.DEBUG_COMMAND);

        this.addArgument(new ProvidersArgument(this));
        this.addArgument(new ItemsArgument(this));
        this.addArgument(new ServerArgument(this));
        this.addArgument(new GameArgument(this));

       HyriAPI.get().getRankManager().addPermission(EHyriRank.ADMINISTRATOR, Permission.DEBUG_COMMAND);
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {}

    class ProvidersArgument extends HyriCommandArgument {

        public ProvidersArgument(HyriCommand<?> command) {
            super(command, "providers");
        }

        @Override
        public void handle(CommandSender sender, String label, String[] args) {
            sender.sendMessage("== Hyrame Providers ==");

            for (IPluginProvider provider : plugin.getHyrame().getPluginProviders()) {
                sender.sendMessage("* " + provider.getClass().getName());
            }
        }

    }

    private class ItemsArgument extends HyriCommandArgument {

        public ItemsArgument(HyriCommand<?> command) {
            super(command, "items");
        }

        @Override
        public void handle(CommandSender sender, String label, String[] args) {
            sender.sendMessage("== Items ==");

            for (Map.Entry<String, HyriItem<?>> item : plugin.getHyrame().getItemManager().getItems().entrySet()) {
                sender.sendMessage("* " + item.getKey() + ": " + this.getFormattedDescription(item.getValue().getDescription()));
            }
        }

        private String getFormattedDescription(List<String> description) {
            if (description.size() > 0) {
                return description.stream().map(line -> " " + line).collect(Collectors.joining()).substring(1);
            } else {
                return "None";
            }
        }

    }

    private static class ServerArgument extends HyriCommandArgument {

        public ServerArgument(HyriCommand<?> command) {
            super(command, "server");
        }

        @Override
        public void handle(CommandSender sender, String label, String[] args) {
            final IHyriServer server = HyriAPI.get().getServer();

            sender.sendMessage("== Server ==");
            sender.sendMessage("* Name: " + server.getName());
            sender.sendMessage("* Type: " + server.getType());
            sender.sendMessage("* Started time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(server.getStartedTime())));
        }

    }

    private class GameArgument extends HyriCommandArgument {

        public GameArgument(HyriCommand<?> command) {
            super(command, "game");
        }

        @Override
        public void handle(CommandSender sender, String label, String[] args) {
            final HyriGame<?> game = plugin.getHyrame().getGameManager().getCurrentGame();

            sender.sendMessage("== Game ==");

            if (game != null) {
                sender.sendMessage("* Name: " + game.getName());
                sender.sendMessage("* Display name: " + game.getDisplayName());
                sender.sendMessage("* State: " + game.getState());
            } else {
                sender.sendMessage("Warning: No game is registered on this server");
            }
        }

    }

}