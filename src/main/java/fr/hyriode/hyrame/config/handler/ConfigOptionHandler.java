package fr.hyriode.hyrame.config.handler;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigProcess;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

/**
 * Created by AstFaster
 * on 01/06/2022 at 07:16
 *
 * An option handler is like a protocol to follow.<br>
 * This handler is used to set all objects with the same type during configuration process
 */
public abstract class ConfigOptionHandler<T> {

    /** The Hyrame instance */
    protected final IHyrame hyrame;
    /** The current process that ran the handler */
    protected final ConfigProcess<?> process;
    /** The player running the process */
    protected final Player player;
    /** The completion to call when the handler has finished its job to set a config's value */
    protected final CompletableFuture<T> completion;

    /**
     * Constructor of {@link ConfigOptionHandler}
     *
     * @param hyrame The Hyrame instance
     * @param process The process that ran the handler
     */
    public ConfigOptionHandler(IHyrame hyrame, ConfigProcess<?> process) {
        this.hyrame = hyrame;
        this.process = process;
        this.player = this.process.getPlayer();
        this.completion = new CompletableFuture<>();
    }

    /**
     * This method is called just after the handler being initialized.<br>
     * It's after this method has been called, that an object needs to have a value.
     */
    public abstract void handle();

    /**
     * Complete the handler by giving the object to set.<br>
     * The process will never continue if this method is not fired.
     *
     * @param value The value to set
     */
    protected void complete(T value) {
        this.completion.complete(value);
    }

    /**
     * Get the completion of the handler
     *
     * @return A {@link CompletableFuture}
     */
    public CompletableFuture<T> getCompletion() {
        return this.completion;
    }

}
