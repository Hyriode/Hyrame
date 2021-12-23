package fr.hyriode.hyrame.command;

import fr.hyriode.hyriapi.rank.HyriPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/12/2021 at 09:21
 */
public class HyriCommandInfo {

    /** Command's name */
    private String name;
    /** Command's description */
    private String description;
    /** Command's usage */
    private String usage;
    /** Command's aliases */
    private List<String> aliases = new ArrayList<>();
    /** Permission needed to execute the command */
    private HyriPermission permission;
    /** Command's type */
    private HyriCommandType type = HyriCommandType.ALL;

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
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withUsage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Get the command's usage
     *
     * @return The command's usage
     */
    public String getUsage() {
        return this.usage;
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
        this.aliases = List.of(aliases);
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
     * @param permission Command's permission
     * @return {@link HyriCommandInfo}
     */
    public HyriCommandInfo withPermission(HyriPermission permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Get the permission needed to execute the command
     *
     * @return The permission
     */
    public HyriPermission getPermission() {
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

}
