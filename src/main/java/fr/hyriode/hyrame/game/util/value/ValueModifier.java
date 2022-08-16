package fr.hyriode.hyrame.game.util.value;

import java.util.function.Supplier;

/**
 * Created by AstFaster
 * on 16/08/2022 at 10:45
 */
public class ValueModifier<T> {

    protected int priority;
    protected final Supplier<Boolean> executable;
    protected Supplier<T> supplier;

    public ValueModifier(int priority, Supplier<Boolean> executable, Supplier<T> supplier) {
        this.priority = priority;
        this.executable = executable;
        this.supplier = supplier;
    }

    public T execute() {
        return this.supplier.get();
    }

    public boolean isExecutable() {
        return this.executable.get();
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

}
