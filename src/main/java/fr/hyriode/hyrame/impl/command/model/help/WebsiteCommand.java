package fr.hyriode.hyrame.impl.command.model.help;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class WebsiteCommand extends HyriCommand<HyramePlugin> {

    public WebsiteCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("website")
                .withAliases("web", "site", "internet")
                .withType(HyriCommandType.PLAYER)
                .withDescription("Command used to get the website link")
                .withUsage("/discord"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        player.spigot().sendMessage(new ComponentBuilder(HyriLanguageMessage.get("message.website").getForPlayer(player).replace("%website%", HyriConstants.WEBSITE_URL))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, HyriConstants.WEBSITE_URL))
                .create());
    }

}
