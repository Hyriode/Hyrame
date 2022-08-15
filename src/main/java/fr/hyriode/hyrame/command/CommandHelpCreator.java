package fr.hyriode.hyrame.command;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 04/08/2022 at 10:56
 */
public class CommandHelpCreator {

    private final String commandPrefix;
    private final String commandName;
    private final Player player;
    private final ComponentBuilder builder;

    public CommandHelpCreator(String commandPrefix, String commandName, Player player) {
        this.commandPrefix = commandPrefix;
        this.commandName = commandName;
        this.player = player;
        this.builder = this.initBuilder();
    }

    private ComponentBuilder initBuilder() {
        return new ComponentBuilder(Symbols.HYPHENS_LINE)
                .color(ChatColor.DARK_AQUA)
                .strikethrough(true)
                .append("\n")
                .strikethrough(false)
                .append(HyrameMessage.AVAILABLE_COMMANDS.asString(this.player))
                .append("\n");
    }

    public CommandHelpCreator addArgumentsLine(String name, String arguments) {
        this.builder.append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY)
                .append(" /" + this.commandPrefix + " ").color(ChatColor.DARK_AQUA).append(arguments).color(ChatColor.AQUA)
                .append(" - ").color(ChatColor.DARK_GRAY).event((ClickEvent) null)
                .append(HyriLanguageMessage.get("command." + this.commandName + "." + name + ".description").getValue(this.player)).color(ChatColor.GRAY)
                .append("\n");
        return this;
    }

    public BaseComponent[] create() {
        return this.builder
                .append(Symbols.HYPHENS_LINE)
                .color(ChatColor.DARK_AQUA)
                .strikethrough(true)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .create();
    }

}
