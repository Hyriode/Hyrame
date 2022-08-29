package fr.hyriode.hyrame.game.util.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 16/08/2022 at 10:26
 */
public class ValueProvider<T> {

    private final List<ValueModifier<T>> modifiers;

    private final T defaultValue;

    public ValueProvider(T defaultValue) {
        this.defaultValue = defaultValue;
        this.modifiers = new ArrayList<>();
    }

    @SafeVarargs
    public final ValueProvider<T> addModifiers(ValueModifier<T>... modifiers) {
        this.modifiers.addAll(Arrays.asList(modifiers));
        return this;
    }

    public List<ValueModifier<T>> getModifiers() {
        return this.modifiers;
    }

    public T get() {
        for (ValueModifier<T> modifier : this.modifiers.stream().sorted((o1, o2) -> o2.getPriority() - o1.getPriority()).collect(Collectors.toList())) {
            if (!modifier.isExecutable()) {
                continue;
            }

            final T value = modifier.execute();

            if (value == null) {
                continue;
            }

            return value;
        }
        return this.defaultValue;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

}
