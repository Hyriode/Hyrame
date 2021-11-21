package fr.hyriode.hyrame.impl.command.model.bukkit;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class PluginCommand extends HyriCommand<HyramePlugin> {

    private static final String SERVER_INFORMATION_KEY = "command.plugin.info";
    private static final String CURRENT_KEY = "command.plugin.current";

    public PluginCommand(HyramePlugin plugin) {
        super(plugin, "plugins", "Show all plugins", Arrays.asList("pl", "ver", "version", "about", "icanhasbukkit"));
    }

    @Override
    public void handle(CommandSender sender, String label, String[] args) {
        final IHyriLanguageManager languageManager = this.plugin.getHyrame().getLanguageManager();
        final boolean player = sender instanceof Player;
        final String colon = player ? (HyriAPI.get().getPlayerManager().getPlayer(((Player) sender).getUniqueId()).getSettings().getLanguage() == HyriLanguage.FR ? " : " : ": ") : ": ";
        final String serverInformation = player ? languageManager.getMessageValueForPlayer((Player) sender, SERVER_INFORMATION_KEY) : languageManager.getMessageValue(HyriLanguage.EN, SERVER_INFORMATION_KEY);
        final String current = player ? languageManager.getMessageValueForPlayer((Player) sender, CURRENT_KEY) : languageManager.getMessageValue(HyriLanguage.EN, CURRENT_KEY);
        final String barLine = ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + "-------------------------------------------";

        sender.sendMessage(barLine);
        sender.sendMessage(ChatColor.DARK_AQUA + serverInformation + colon);
        sender.sendMessage(this.getCategory(current, colon, HyriAPI.get().getServer().getName()));
        sender.sendMessage(this.getCategory("Version", colon, Bukkit.getVersion()));
        sender.sendMessage(this.getCategory("Plugins", colon, this.getPlugins()));
        sender.sendMessage(barLine);
    }

    private String getCategory(String category, String colon, String value) {
        return ChatColor.AQUA + category + colon + ChatColor.WHITE + value;
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
