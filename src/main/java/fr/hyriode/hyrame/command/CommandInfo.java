package fr.hyriode.hyrame.command;

import fr.hyriode.api.player.IHyriPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
public class CommandInfo {

    /** Command's name */
    private String name;
    /** Command's description */
    private String description = "";
    /** Command's usage */
    private CommandUsage usage = new CommandUsage();
    /** Command's aliases */
    private List<String> aliases = new ArrayList<>();
    /** Permission needed to execute the command */
    private Predicate<IHyriPlayer> permission;
    /** Is the command executed asynchronously or not */
    private boolean asynchronous;

    /**
     * Constructor of {@link CommandInfo}
     *
     * @param name Command's name
     */
    public CommandInfo(String name) {
        this.name = name;
    }

    /**
     * Set the name of the command
     *
     * @param name Command's name
     * @return {@link CommandInfo}
     */
    public CommandInfo withName(String name) {
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
     * @return {@link CommandInfo}
     */
    public CommandInfo withDescription(String description) {
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

    public CommandInfo withUsage(@NotNull CommandUsage usage) {
        this.usage = usage;
        return this;
    }

    public CommandUsage getUsage() {
        return this.usage;
    }

    /**
     * Set the aliases of the command
     *
     * @param aliases Command's aliases
     * @return {@link CommandInfo}
     */
    public CommandInfo withAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Set the aliases of the command
     *
     * @param aliases Command's aliases
     * @return {@link CommandInfo}
     */
    public CommandInfo withAliases(String... aliases) {
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
     * @return {@link CommandInfo}
     */
    public CommandInfo withPermission(Predicate<IHyriPlayer> permission) {
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
     * @return This {@link CommandInfo} instance
     */
    public CommandInfo asynchronous() {
        this.asynchronous = true;
        return this;
    }

}
