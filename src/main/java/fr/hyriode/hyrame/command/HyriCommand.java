package fr.hyriode.hyrame.command;

import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.BiomeForest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

        CommandUsage usage = null;
        int bestIndex = 0;
        for (CommandArgument argument : ctx.getArguments()) {
            ctx.setArgumentPosition(0);
            ctx.setResult(new CommandResult(CommandResult.Type.SUCCESS));

            final CommandOutput output = new CommandOutput();
            final String[] expectedArgs =  argument.getExpected().toLowerCase(Locale.ROOT).split(" ");

            if (argument.getExpected().isEmpty() && args.length == 0) {
                argument.getAction().accept(output);
                return;
            }

            for (int i = 0; i < expectedArgs.length; i++) {
                final String expectedArg = expectedArgs[i];

                if (i > bestIndex) {
                    usage = argument.getUsage();
                    bestIndex = i;
                }

                if (args.length <= i) {
                    ctx.setResult(new CommandResult(CommandResult.Type.ERROR, argument.getUsage()));
                    break;
                }

                final String arg = args[i];

                ctx.setArgumentPosition(i);

                if (!expectedArg.equalsIgnoreCase(arg)) { // It might be a check
                    final CommandCheck check = CommandCheck.fromSequence(expectedArg);

                    if (check == null) {
                        ctx.setResult(new CommandResult(CommandResult.Type.ERROR, argument.getUsage()));
                        break;
                    }

                    final boolean continueProcess = check.runAction(ctx, output, arg);

                    if (ctx.getResult().getType() == CommandResult.Type.CHECK_ERROR) { // An error occurred in checks
                        if (bestIndex == i) {
                            usage = ctx.getResult().getUsage();
                        }
                        break;
                    }

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

        ctx.setResult(new CommandResult(CommandResult.Type.ERROR, usage == null ? this.info.getUsage() : usage));

        // No argument was found
        this.commandError(player, ctx.getResult().getUsage());
    }

    private void commandError(Player player, CommandUsage usage) {
        String message = "";
        if (usage.isErrorPrefix()) {
            message += HyrameMessage.COMMAND_INVALID.asString(player);
        }

        final List<BaseComponent> components = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(message + ChatColor.RESET)));

        components.addAll(Arrays.asList(usage.getMessage().apply(player)));

        player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
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
