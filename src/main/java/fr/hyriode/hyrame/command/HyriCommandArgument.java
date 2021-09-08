package fr.hyriode.hyrame.command;

import org.bukkit.command.CommandSender;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 18:15
 */
public abstract class HyriCommandArgument {

    protected final String name;
    protected final String usageMessage;

    public HyriCommandArgument(String name) {
        this(name, "");
    }

    public HyriCommandArgument(String name, String usageMessage) {
        this.name = name;
        this.usageMessage = usageMessage;
    }

    public abstract void handle(CommandSender sender, String[] args);

    public String getName() {
        return this.name;
    }

    public String getUsageMessage() {
        return this.usageMessage;
    }

}
