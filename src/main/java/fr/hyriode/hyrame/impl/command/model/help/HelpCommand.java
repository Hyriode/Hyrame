package fr.hyriode.hyrame.impl.command.model.help;

import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HelpCommand extends HyriCommand<HyramePlugin> {

    public HelpCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("help")
                .withAliases("?", "aide")
                .withType(HyriCommandType.PLAYER)
                .withDescription("Command used to help you")
                .withUsage("/help"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n\n").strikethrough(false)
                .append(this.getCommandLine(player, "discord")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord"))
                .append(this.getCommandLine(player, "website")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/website"))
                .append(this.getCommandLine(player, "store")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/store"))
                .append(this.getCommandLine(player, "friend")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend"))
                .append(this.getCommandLine(player, "party")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party"))
                .append(this.getCommandLine(player, "lobby")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lobby"))
                .append(this.getCommandLine(player, "report")).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/report "))
                .append("\n")
                .event((ClickEvent) null)
                .append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

        player.spigot().sendMessage(builder.create());
    }

    private String getCommandLine(Player player, String command) {
        return ChatColor.AQUA + " /" + command + " - " + ChatColor.WHITE + HyriLanguageMessage.get("message.help." + command).getForPlayer(player) + "\n";
    }

}
