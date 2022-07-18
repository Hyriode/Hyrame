package fr.hyriode.hyrame.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.PrimitiveType;
import fr.hyriode.hyrame.utils.TriPredicate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 16:43
 */
public enum HyriCommandCheck {

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
    PLAYER("%player%", IHyriPlayer.class, arg -> HyriAPI.get().getPlayerManager().getPlayer(arg), (ctx, arg) -> ChatColor.RED + HyriCommonMessages.PLAYER_NOT_FOUND.getValue((Player) ctx.getSender()) + arg + "."),
    /** Handle a player name as an input, but it will check from players accounts */
    PLAYER_ONLINE("%player_online%", IHyriPlayer.class, arg -> {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getCachedPlayer(arg);

        return account!= null && account.isOnline() ? account : null;
    }, (ctx, arg) -> ChatColor.RED + HyriCommonMessages.PLAYER_NOT_FOUND.getValue((Player) ctx.getSender()) + arg + "."),
    /** Handle a player name as an input, but it will check from players on current server */
    PLAYER_ON_SERVER("%player_server%", Player.class, PlayerUtil::getPlayer, (ctx, arg) -> ChatColor.RED + HyriCommonMessages.PLAYER_NOT_FOUND.getValue((Player) ctx.getSender()) + arg + "."),
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
    private final TriPredicate<HyriCommandContext, HyriCommandOutput, String> action;

    /** Constant values list to increase performances */
    public static final HyriCommandCheck[] VALUES = HyriCommandCheck.values();

    /**
     * Constructor of {@link HyriCommandCheck}
     *
     * @param sequence The sequence of the check
     * @param action The action linked to the check
     */
    HyriCommandCheck(String sequence, TriPredicate<HyriCommandContext, HyriCommandOutput, String> action) {
        this.sequence = sequence;
        this.action = action;
    }

    /**
     * Alternative constructor of {@link HyriCommandCheck}
     *
     * @param sequence The sequence of the check
     * @param clazz The class of the object to add in the output
     * @param getter Getter used to get the object to add in the output
     * @param error The error function to apply when the object is null
     */
    HyriCommandCheck(String sequence, Class<?> clazz, Function<String, ?> getter, BiFunction<HyriCommandContext, String, String> error) {
        this(sequence, (ctx, output, arg) -> {
            final Object object = getter.apply(arg);

            if (object != null) {
                output.add(clazz, object);
                return true;
            }

            if (error != null) {
                ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, error.apply(ctx, arg)));
            }
            return false;
        });
    }

    /**
     * Alternative constructor of {@link HyriCommandCheck}
     *
     * @param sequence The sequence of the check
     * @param getter Getter used to get the object to add in the output
     * @param error The error function to apply when the object is null
     */
    HyriCommandCheck(String sequence, Function<String, ?> getter, BiFunction<HyriCommandContext, String, String> error) {
        this(sequence, (ctx, output, arg) -> {
            final Object object = getter.apply(arg);

            if (object != null) {
                output.add(object.getClass(), object);
                return true;
            }

            if (error != null) {
                ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, error.apply(ctx, arg)));
            }
            return false;
        });
    }

    /**
     * Alternative constructor of {@link HyriCommandCheck}
     *
     * @param sequence The sequence of the check
     * @param getter Getter used to get the object to add in the output
     */
    HyriCommandCheck(String sequence, Function<String, ?> getter) {
        this(sequence, getter, null);
    }

    /**
     * Constructor of {@link HyriCommandCheck}<br>
     * This constructor is used for primitive type check only
     *
     * @param sequence Check sequence
     * @param type Primitive type of the check
     */
    HyriCommandCheck(String sequence, PrimitiveType<?> type) {
        this(sequence, arg -> type.isValid(arg) ? type.parse(arg) : null, (ctx, arg) -> ChatColor.RED + (type == PrimitiveType.BOOLEAN ? HyriCommonMessages.INVALID_ARGUMENT : HyriCommonMessages.INVALID_NUMBER).getValue((Player) ctx.getSender()) + arg + ".");
    }

    /**
     * Run check action
     *
     * @param ctx Command context
     * @param output Command output
     * @param arg Argument to validate
     * @return <code>true</code> if it's valid
     */
    public boolean runAction(HyriCommandContext ctx, HyriCommandOutput output, String arg) {
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
     * @return {@link HyriCommandCheck} or <code>null</code>
     */
    public static HyriCommandCheck fromSequence(String sequence) {
        for (HyriCommandCheck check : VALUES) {
            if (check.toString().equalsIgnoreCase(sequence)) {
                return check;
            }
        }
        return null;
    }

}
