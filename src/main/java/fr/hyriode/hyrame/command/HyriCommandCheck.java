package fr.hyriode.hyrame.command;

import fr.hyriode.hyrame.language.HyriLanguages;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.tools.PrimitiveType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 16:43
 */
public enum HyriCommandCheck {

    /** Handle all input, it can be a word, a number, a state etc */
    INPUT("%input%", (ctx, arg) -> true, input -> input),
    /** Handle a player name as an input, but it will check from players accounts */
    PLAYER("%player%", (ctx, arg) -> {
        final CommandSender sender = ctx.getSender();

        if (HyriAPI.get().getPlayerManager().getPlayerId(arg) != null) {
            return true;
        } else {
            ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, ChatColor.RED + HyriLanguages.PLAYER_NOT_FOUND.getForSender(sender) + arg + "."));
            return false;
        }
    }, input -> HyriAPI.get().getPlayerManager().getPlayer(input)),
    /** Handle a player name as an input, but it will check from players on current server */
    PLAYER_ON_SERVER("%player_server%", (ctx, arg) -> {
        final CommandSender sender = ctx.getSender();

        if (Bukkit.getPlayer(arg) != null) {
            return true;
        } else {
            ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, ChatColor.RED + HyriLanguages.PLAYER_NOT_FOUND.getForSender(sender) + arg + "."));
            return false;
        }
    }, Bukkit::getPlayer),
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
    /** Validation step */
    private final BiFunction<HyriCommandContext, String, Boolean> validation;
    /** Getting step */
    private final Function<String, Object> getter;

    /**
     * Constructor of {@link HyriCommandCheck}
     *
     * @param sequence Check sequence
     * @param validation Check validation step
     * @param getter Check getting step
     */
    HyriCommandCheck(String sequence, BiFunction<HyriCommandContext, String, Boolean> validation, Function<String, Object> getter) {
        this.sequence = sequence;
        this.validation = validation;
        this.getter = getter;
    }

    /**
     * Constructor of {@link HyriCommandCheck}<br>
     * This constructor is used for primitive type check only
     *
     * @param sequence Check sequence
     * @param type Primitive type of the check
     */
    HyriCommandCheck(String sequence, PrimitiveType<?> type) {
        this(sequence, (ctx, arg) -> {
            final CommandSender sender = ctx.getSender();

            if (type.isValid(arg)) {
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + (type == PrimitiveType.BOOLEAN ? HyriLanguages.INVALID_ARGUMENT : HyriLanguages.INVALID_NUMBER).getForSender(sender) + arg + ".");
                return false;
            }
        }, type::parse);
    }

    /**
     * Get check sequence. Example: %input%, %double% etc
     *
     * @return A sequence
     */
    public String getSequence() {
        return this.sequence;
    }

    /**
     * Execute validation step
     *
     * @param ctx Command context
     * @param arg Argument to validate
     * @return <code>true</code> if its valid
     */
    public boolean validate(HyriCommandContext ctx, String arg) {
        return this.validation.apply(ctx, arg);
    }

    /**
     * Execute getting step
     *
     * @param input Input to parse
     * @param <T> Type of the return
     * @return The parsed input
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String input) {
        return (T) this.getter.apply(input);
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
        for (HyriCommandCheck check : values()) {
            if (check.toString().equalsIgnoreCase(sequence)) {
                return check;
            }
        }
        return null;
    }

}
