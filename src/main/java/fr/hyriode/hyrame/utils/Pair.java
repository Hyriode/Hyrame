package fr.hyriode.hyrame.utils;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/12/2021 at 18:47
 */
public class Pair<K, V> {

    /** Key of this pair */
    private final K key;

    /** Value of this pair */
    private final V value;

    /**
     * Constructor of {@link Pair}
     *
     * @param key The key of this pair
     * @param value The value of this pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the key of this pair.
     *
     * @return Key of this pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the value of this pair.
     *
     * @return Value of this pair
     */
    public V getValue() {
        return value;
    }

}
