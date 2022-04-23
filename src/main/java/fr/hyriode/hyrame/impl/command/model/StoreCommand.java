package fr.hyriode.hyrame.impl.command.model;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class StoreCommand extends HyriCommand<HyramePlugin> {

    public StoreCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("store")
                .withAliases("shop", "boutique")
                .withType(HyriCommandType.PLAYER)
                .withDescription("Command used to get the store website link")
                .withUsage("/discord"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        player.sendMessage(HyriLanguageMessage.get("message.store-website").getForPlayer(player) + ChatColor.RESET + HyriConstants.STORE_WEBSITE_URL);
    }

}
