package fr.hyriode.hyrame.utils;

import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 09:42
 */
public class PrimitiveType<T> {

    /** Short type */
    public static final PrimitiveType<Short> SHORT = new PrimitiveType<>(Short::parseShort);
    /** Integer type */
    public static final PrimitiveType<Integer> INTEGER = new PrimitiveType<>(Integer::parseInt);
    /** Long type */
    public static final PrimitiveType<Long> LONG = new PrimitiveType<>(Long::parseLong);
    /** Float type */
    public static final PrimitiveType<Float> FLOAT = new PrimitiveType<>(Float::parseFloat);
    /** Double type */
    public static final PrimitiveType<Double> DOUBLE = new PrimitiveType<>(Double::parseDouble);
    /** Boolean type */
    public static final PrimitiveType<Boolean> BOOLEAN = new PrimitiveType<>(Boolean::parseBoolean);

    /** Action used to parse the type */
    private final Function<String, T> action;

    /**
     * Constructor of {@link PrimitiveType}
     *
     * @param action Parsing action
     */
    public PrimitiveType(Function<String, T> action) {
        this.action = action;
    }

    /**
     * Check if an input as the same current type
     *
     * @param input Input to check
     * @return <code>true</code> if yes
     */
    public boolean isValid(String input) {
        try {
            this.action.apply(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse an input to the type
     *
     * @param input Input to parse
     * @return Parsed input
     */
    public T parse(String input) {
        return this.action.apply(input);
    }

    /**
     * Check if an input as the same current type
     *
     * @param input Input to check
     * @param type The type that input will be checked with
     * @return <code>true</code> if yes
     */
    public static boolean isValid(String input, PrimitiveType<?> type) {
        return type.isValid(input);
    }

}
