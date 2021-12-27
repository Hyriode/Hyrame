package fr.hyriode.hyrame.command;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 10:07
 */
public class HyriCommandResult {

    /** Result's type */
    private final Type type;
    /** Result's message */
    private final String message;

    /**
     * Constructor of {@link HyriCommandResult}
     *
     * @param type Result's type
     * @param message Result's message
     */
    public HyriCommandResult(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Get result's type
     *
     * @return {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get result's message
     *
     * @return A message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * A simple enum with all result types
     */
    public enum Type {
        /** The command was successfully executed */
        SUCCESS,
        /** An error occurred */
        ERROR
    }

}