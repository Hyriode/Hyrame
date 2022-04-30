package fr.hyriode.hyrame.command;

import fr.hyriode.api.player.IHyriPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/12/2021 at 09:21
 */
public class HyriCommandInfo {

    /** Command's name */
    private String name;
    /** Command's description */
    private String description = "";
    /** Command's usage */
    private Function<CommandSender, BaseComponent[]> usage;
    /** The boolean that means whether the invalid command message will be sent or not */
    private boolean invalidMessage;
    /** Command's aliases */
    private List<String> aliases = new ArrayList<>();
    /** Permission needed to execute the command */
    private Predicate<IHyriPlayer> permission;
    /** Command's type */
    private HyriCommandType type = HyriCommandType.ALL;
    /** Is the command executed asynchronously or not */
    private boolean asynchronous;

    /**
     * Constructor of {@link HyriCommandInfo}
     *
     * @param name Command's name
     */
    public HyriCommandInfo(String name) {
        this.name = name;
    }

    /**
     * Set the name of the command
     *
     * @param name Command's name
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the command's name
     *
     * @return The command's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the description of the command
     *
     * @param description Command's description
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get the command's description
     *
     * @return The command's description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the usage of the command
     *
     * @param usage Command's usage
     * @param invalidMessage Invalid message will be sent or not
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withUsage(Function<CommandSender, BaseComponent[]> usage, boolean invalidMessage) {
        this.usage = usage;
        this.invalidMessage = invalidMessage;
        return this;
    }

    /**
     * Set the usage of the command
     *
     * @param usage Command's usage
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withUsage(Function<CommandSender, BaseComponent[]> usage) {
        return this.withUsage(usage, true);
    }

    /**
     * Set the usage of the command
     *
     * @param usage Command's usage
     * @param invalidMessage Invalid message will be sent or not
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withUsage(String usage, boolean invalidMessage) {
        this.usage = sender -> TextComponent.fromLegacyText(usage);
        this.invalidMessage = invalidMessage;
        return this;
    }

    /**
     * Set the usage of the command
     *
     * @param usage Command's usage
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withUsage(String usage) {
        return this.withUsage(usage, true);
    }

    /**
     * Get the command's usage
     *
     * @return The command's usage
     */
    public Function<CommandSender, BaseComponent[]> getUsage() {
        return this.usage;
    }

    /**
     * Check if invalid message needs to be sent or not
     *
     * @return <code>true</code> if the message needs to be sent
     */
    public boolean isInvalidMessage() {
        return this.invalidMessage;
    }

    /**
     * Set the aliases of the command
     *
     * @param aliases Command's aliases
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Set the aliases of the command
     *
     * @param aliases Command's aliases
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withAliases(String... aliases) {
        this.aliases = Arrays.asList(aliases);
        return this;
    }

    /**
     * Get the command's aliases
     *
     * @return The command's aliases
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Set the permission of the command
     *
     * @param permission Command's permission predicate
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withPermission(Predicate<IHyriPlayer> permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Get the permission needed to execute the command
     *
     * @return The permission
     */
    public Predicate<IHyriPlayer> getPermission() {
        return this.permission;
    }

    /**
     * Set the type of the command
     *
     * @param type Command's type
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withType(HyriCommandType type) {
        this.type = type;
        return this;
    }

    /**
     * Get the command's type
     *
     * @return The command's type
     */
    public HyriCommandType getType() {
        return this.type;
    }

    /**
     * Check if the command will be executed asynchronously
     *
     * @return <code>true</code> if yes
     */
    public boolean isAsynchronous() {
        return this.asynchronous;
    }

    /**
     * Set the command execution asynchronous
     *
     * @return This {@link HyriCommandInfo} instance
     */
    public HyriCommandInfo asynchronous() {
        this.asynchronous = true;
        return this;
    }

}
