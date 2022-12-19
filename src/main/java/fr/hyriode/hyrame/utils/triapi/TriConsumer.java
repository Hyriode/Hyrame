package fr.hyriode.hyrame.utils.triapi;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/04/2022 at 21:30
 */
@FunctionalInterface
public interface TriConsumer<T, T1, T2> {

    void accept(T t, T1 t1, T2 t2);

    default TriConsumer<T, T1, T2> andThen(TriConsumer<? super T, ? super T1, ? super T2> after) {
        return (t1, t2, t3) -> {
            this.accept(t1, t2, t3);

            after.accept(t1, t2, t3);
        };
    }

}
