package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.language.IHyriLanguageManager;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class PluginCommand extends HyriCommand<HyramePlugin> {

    private static final String SERVER_INFORMATION_KEY = "command.plugin.info";
    private static final String CURRENT_KEY = "command.plugin.current";

    public PluginCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("plugins")
                .withDescription("Show all plugins running on the server")
                .withAliases("pl", "ver", "version", "about", "icanhasbukkit")
                .withUsage("/plugins"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final CommandSender sender = ctx.getSender();
        final String serverInformation = HyriLanguageMessage.get(SERVER_INFORMATION_KEY).getValue(sender);
        final String current = HyriLanguageMessage.get(CURRENT_KEY).getValue(sender);
        final String barLine = ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "-------------------------------------------";

        sender.sendMessage(barLine);
        sender.sendMessage(ChatColor.DARK_AQUA + serverInformation + ": ");
        sender.sendMessage(this.getCategory(current, HyriAPI.get().getServer().getName()));
        sender.sendMessage(this.getCategory("Version", Bukkit.getVersion()));
        sender.sendMessage(this.getCategory("Plugins", this.getPlugins()));
        sender.sendMessage(barLine);
    }

    private String getCategory(String category, String value) {
        return ChatColor.AQUA + category + ": " + ChatColor.WHITE + value;
    }

    private String getPlugins() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            stringBuilder.append(ChatColor.WHITE)
                    .append(plugin.getName())
                    .append(", ");
        }

        return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
    }

}
