package fr.hyriode.hyrame.utils.triapi;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/04/2022 at 21:30
 */
@FunctionalInterface
public interface TriPredicate<T, T1, T2> {

    boolean test(T t, T1 t1, T2 t2);

}
