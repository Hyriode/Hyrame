package fr.hyriode.hyrame.utils;

/**
 * Created by AstFaster
 * on 11/12/2022 at 12:37
 */
public interface Cast<T> {

    @SuppressWarnings("unchecked")
    default <C extends T> C cast() {
        return (C) this;
    }

}
