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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AstFaster
 * on 04/08/2022 at 10:56
 */
public class CommandHelpCreator {

    private ChatColor mainColor = ChatColor.DARK_AQUA;
    private ChatColor secondaryColor = ChatColor.AQUA;

    private final Map<String, String> data = new HashMap<>();

    private final String commandPrefix;
    private final String commandName;
    private final Player player;
    private final ComponentBuilder builder;

    public CommandHelpCreator(String commandPrefix, String commandName, Player player) {
        this.commandPrefix = commandPrefix;
        this.commandName = commandName;
        this.player = player;
        this.builder = new ComponentBuilder("");
    }

    public CommandHelpCreator addArgumentsLine(String name, String arguments) {
        this.data.put(name, arguments);
        return this;
    }

    public BaseComponent[] create() {
        this.builder.append(Symbols.HYPHENS_LINE)
                .color(this.mainColor)
                .strikethrough(true)
                .append("\n")
                .strikethrough(false)
                .append(HyrameMessage.AVAILABLE_COMMANDS.asString(this.player)).color(this.mainColor)
                .append("\n");

        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            this.builder.append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY)
                    .append(" /" + this.commandPrefix + " ").color(this.mainColor).append(entry.getValue()).color(this.secondaryColor)
                    .append(" - ").color(ChatColor.DARK_GRAY).event((ClickEvent) null)
                    .append(HyriLanguageMessage.get("command." + this.commandName + "." + entry.getKey() + ".description").getValue(this.player)).color(ChatColor.GRAY)
                    .append("\n");
        }

        return this.builder
                .append(Symbols.HYPHENS_LINE)
                .color(this.mainColor)
                .strikethrough(true)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .create();
    }

    public CommandHelpCreator withMainColor(ChatColor mainColor) {
        this.mainColor = mainColor;
        return this;
    }

    public CommandHelpCreator withSecondaryColor(ChatColor secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

}
