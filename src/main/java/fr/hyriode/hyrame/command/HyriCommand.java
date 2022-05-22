package fr.hyriode.hyrame.command;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/12/2021 at 10:34
 */
public abstract class HyriCommand<T extends JavaPlugin> {

    /** The plugin provided in the {@link fr.hyriode.hyrame.plugin.IPluginProvider} */
    protected final T plugin;
    /** The information of the command */
    protected final HyriCommandInfo info;

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin The plugin
     * @param info The command's information
     */
    public HyriCommand(T plugin, HyriCommandInfo info) {
        this.plugin = plugin;
        this.info = info;
    }

    /**
     * Method fired when the command is executed
     *
     * @param ctx Context of the execution
     */
    public abstract void handle(HyriCommandContext ctx);

    /**
     * Handle an argument
     *
     * @param ctx Command's execution context
     * @param expected Expected input
     * @param usage Usage message if the input is not correct
     * @param callback Callback to fire after
     */
    protected void handleArgument(HyriCommandContext ctx, String expected, Function<CommandSender, BaseComponent[]> usage, Consumer<HyriCommandOutput> callback) {
        if (ctx.getResult() == null || ctx.getResult().getType() == HyriCommandResult.Type.ERROR) {
            final String[] expectedArgs = expected.toLowerCase(Locale.ROOT).split(" ");
            final String[] args = ctx.getArgs();

            if (args.length == 0 && expectedArgs.length > 0) {
                this.invalidCommandMessage(ctx, usage);
                return;
            }

            final HyriCommandOutput output = new HyriCommandOutput();

            ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.SUCCESS));

            if (args.length < expectedArgs.length) {
                this.invalidCommandMessage(ctx, usage);
                return;
            }

            for (int i = 0; i < args.length; i++) {
                if (expectedArgs.length <= i) {
                    this.invalidCommandMessage(ctx, usage);
                    return;
                }

                final String arg = args[i];
                final String expectedArg = expectedArgs[i];

                ctx.setArgumentPosition(i);

                if (!arg.equalsIgnoreCase(expectedArg)) {
                    final HyriCommandCheck check = HyriCommandCheck.fromSequence(expectedArg);

                    if (check == null) {
                        this.invalidCommandMessage(ctx, usage);
                        return;
                    }

                    final boolean continueProcess = check.runAction(ctx, output, arg);
                    final HyriCommandResult result = ctx.getResult();

                    if (result.getType() == HyriCommandResult.Type.ERROR) {
                        return;
                    }

                    if (!continueProcess) {
                        break;
                    }
                }

            }

            callback.accept(output);
        }
    }

    protected void handleArgument(HyriCommandContext ctx, String expected, Consumer<HyriCommandOutput> callback) {
        this.handleArgument(ctx, expected, this.info.getUsage(), callback);
    }

    /**
     * Used to set the error message as an invalid command in the context
     *
     * @param ctx Command context
     */
    private void invalidCommandMessage(HyriCommandContext ctx, Function<CommandSender, BaseComponent[]> usage) {
        final CommandSender sender = ctx.getSender();

        String message = ChatColor.RED + "";

        if (this.info.isInvalidMessage()) {
            if (sender instanceof Player) {
                message += HyriCommonMessages.INVALID_COMMAND.getForPlayer((Player) sender);
            } else {
                message += HyriCommonMessages.INVALID_COMMAND.getValue(HyriLanguage.EN);
            }
        }

        final List<BaseComponent> components = new ArrayList<>(Arrays.asList(TextComponent.fromLegacyText(message + ChatColor.RESET)));

        components.addAll(Arrays.asList(usage.apply(sender)));

        ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, components.toArray(new BaseComponent[0])));
    }

    /**
     * Get the information of the command
     *
     * @return A {@link HyriCommandInfo} object
     */
    public HyriCommandInfo getInfo() {
        return this.info;
    }

}
