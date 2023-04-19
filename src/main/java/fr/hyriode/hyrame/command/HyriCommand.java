package fr.hyriode.hyrame.command;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/12/2021 at 10:34
 */
public abstract class HyriCommand<T extends JavaPlugin> {

    /** The plugin provided in the {@link IPluginProvider} */
    protected final T plugin;
    /** The information of the command */
    protected final CommandInfo info;

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin The plugin
     * @param info The command's information
     */
    public HyriCommand(T plugin, CommandInfo info) {
        this.plugin = plugin;
        this.info = info;
    }

    /**
     * Method fired when the command is executed
     *
     * @param ctx Context of the execution
     */
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final String[] args = ctx.getArgs();

        for (CommandArgument argument : ctx.getArguments()) {
            ctx.setResult(new CommandResult(CommandResult.Type.SUCCESS));

            final CommandOutput output = new CommandOutput();
            final String[] expectedArgs = argument.getExpected().toLowerCase(Locale.ROOT).split(" ");

            for (int i = 0; i < args.length; i++) {
                final String arg = args[i];

                if (expectedArgs.length <= i) {
                    ctx.setResult(new CommandResult(CommandResult.Type.ERROR, argument.getUsage().apply(player)));
                    break;
                }

                final String expectedArg = expectedArgs[i];

                ctx.setArgumentPosition(i);

                if (!arg.equalsIgnoreCase(expectedArg)) {
                    final CommandCheck check = CommandCheck.fromSequence(expectedArg);

                    if (check == null) {
                        ctx.setResult(new CommandResult(CommandResult.Type.ERROR, argument.getUsage().apply(player)));
                        break;
                    }

                    final boolean continueProcess = check.runAction(ctx, output, arg);

                    if (!continueProcess) {
                        break;
                    }
                }
            }

            if (ctx.getResult().getType() == CommandResult.Type.SUCCESS) {
                argument.getAction().accept(output);
                return;
            }
        }

        // No argument was found
        player.spigot().sendMessage(ctx.getResult().getMessage());
    }

    /**
     * Get the information of the command
     *
     * @return A {@link CommandInfo} object
     */
    public CommandInfo getInfo() {
        return this.info;
    }

}
