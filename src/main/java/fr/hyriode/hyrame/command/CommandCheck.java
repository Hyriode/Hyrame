package fr.hyriode.hyrame.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.PrimitiveType;
import fr.hyriode.hyrame.utils.triapi.TriPredicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 16:43
 */
public enum CommandCheck {

    /** Handle all input, it can be a word, a number, a state etc */
    INPUT("%input%", arg -> arg),
    /**  */
    SENTENCE("%sentence%", (ctx, output, arg) -> {
        final String[] args = ctx.getArgs();
        final StringBuilder builder = new StringBuilder();

        for (int i = ctx.getArgumentPosition(); i < ctx.getArgs().length; i++) {
            builder.append(args[i]).append(i + 1 != args.length ? " " : "");
        }

        output.add(String.class, builder.toString());
        return false;
    }),
    /** Handle a player name as an input, but it will check from players accounts */
    PLAYER("%player%", IHyriPlayer.class, arg -> HyriAPI.get().getPlayerManager().getPlayer(arg), (ctx, arg) -> HyrameMessage.PLAYER_NOT_FOUND_FULL.asString(ctx.getSender()).replace("%player%", arg)),
    /** Handle a player name as an input, but it will check from players accounts */
    PLAYER_ONLINE("%player_online%", IHyriPlayer.class, arg -> {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(arg);

        return account != null && HyriAPI.get().getPlayerManager().isOnline(account.getUniqueId()) ? account : null;
    }, (ctx, arg) -> HyrameMessage.PLAYER_NOT_FOUND_ONLINE.asString(ctx.getSender()).replace("%player%", arg)),
    /** Handle a player name as an input, but it will check from players on current server */
    PLAYER_ON_SERVER("%player_server%", Player.class, Bukkit::getPlayerExact, (ctx, arg) -> HyrameMessage.PLAYER_NOT_FOUND_SERVER.asString(ctx.getSender()).replace("%player%", arg)),
    /** Handle a short number */
    SHORT("%short%", PrimitiveType.SHORT),
    /** Handle an integer number */
    INTEGER("%integer%", PrimitiveType.INTEGER),
    /** Handle a long number */
    LONG("%long%", PrimitiveType.LONG),
    /** Handle a float number */
    FLOAT("%float%", PrimitiveType.FLOAT),
    /** Handle a double number */
    DOUBLE("%double%", PrimitiveType.DOUBLE),
    /** Handle a boolean input */
    BOOLEAN("%boolean%", PrimitiveType.BOOLEAN);

    /** Sequence used to detect if its present in a command */
    private final String sequence;
    /** The action to run when the check is detected. The action returns a boolean which means if the arguments checking process needs to continue or not. */
    private final TriPredicate<CommandContext, CommandOutput, String> action;

    /** Constant values list to increase performances */
    public static final CommandCheck[] VALUES = CommandCheck.values();

    /**
     * Constructor of {@link CommandCheck}
     *
     * @param sequence The sequence of the check
     * @param action The action linked to the check
     */
    CommandCheck(String sequence, TriPredicate<CommandContext, CommandOutput, String> action) {
        this.sequence = sequence;
        this.action = action;
    }

    /**
     * Alternative constructor of {@link CommandCheck}
     *
     * @param sequence The sequence of the check
     * @param clazz The class of the object to add in the output
     * @param getter Getter used to get the object to add in the output
     * @param error The error function to apply when the object is null
     */
    CommandCheck(String sequence, Class<?> clazz, Function<String, ?> getter, BiFunction<CommandContext, String, String> error) {
        this(sequence, (ctx, output, arg) -> {
            final Object object = getter.apply(arg);

            if (object != null) {
                output.add(clazz, object);
                return true;
            }

            if (error != null) {
                ctx.setResult(new CommandResult(CommandResult.Type.CHECK_ERROR, new CommandUsage()
                        .withStringMessage(player -> error.apply(ctx, arg))
                        .withErrorPrefix(false)));
            }
            return false;
        });
    }

    /**
     * Alternative constructor of {@link CommandCheck}
     *
     * @param sequence The sequence of the check
     * @param getter Getter used to get the object to add in the output
     * @param error The error function to apply when the object is null
     */
    CommandCheck(String sequence, Function<String, ?> getter, BiFunction<CommandContext, String, String> error) {
        this(sequence, (ctx, output, arg) -> {
            final Object object = getter.apply(arg);

            if (object != null) {
                output.add(object.getClass(), object);
                return true;
            }

            if (error != null) {
                ctx.setResult(new CommandResult(CommandResult.Type.CHECK_ERROR, new CommandUsage()
                        .withStringMessage(player -> error.apply(ctx, arg))
                        .withErrorPrefix(false)));
            }
            return false;
        });
    }

    /**
     * Alternative constructor of {@link CommandCheck}
     *
     * @param sequence The sequence of the check
     * @param getter Getter used to get the object to add in the output
     */
    CommandCheck(String sequence, Function<String, ?> getter) {
        this(sequence, getter, null);
    }

    /**
     * Constructor of {@link CommandCheck}<br>
     * This constructor is used for primitive type check only
     *
     * @param sequence Check sequence
     * @param type Primitive type of the check
     */
    CommandCheck(String sequence, PrimitiveType<?> type) {
        this(sequence, arg -> type.isValid(arg) ? type.parse(arg) : null, (ctx, arg) -> (type == PrimitiveType.BOOLEAN ? HyrameMessage.INVALID_ARGUMENT : HyrameMessage.INVALID_NUMBER).asString(ctx.getSender()).replace("%arg%", arg));
    }

    /**
     * Run check action
     *
     * @param ctx Command context
     * @param output Command output
     * @param arg Argument to validate
     * @return <code>true</code> if it's valid
     */
    public boolean runAction(CommandContext ctx, CommandOutput output, String arg) {
        return this.action.test(ctx, output, arg);
    }

    /**
     * Get check sequence. Example: %input%, %double% etc
     *
     * @return A sequence
     */
    public String getSequence() {
        return this.sequence;
    }

    @Override
    public String toString() {
        return this.sequence;
    }

    /**
     * Get a check from a provided sequence
     *
     * @param sequence Provided sequence
     * @return {@link CommandCheck} or <code>null</code>
     */
    public static CommandCheck fromSequence(String sequence) {
        for (CommandCheck check : VALUES) {
            if (check.toString().equalsIgnoreCase(sequence)) {
                return check;
            }
        }
        return null;
    }

}
