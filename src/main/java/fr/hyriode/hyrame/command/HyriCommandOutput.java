package fr.hyriode.hyrame.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 09:01
 */
public class HyriCommandOutput {

    /** The map of objects stored in the output */
    private final Map<Class<?>, List<Object>> objectsMap = new HashMap<>();

    /**
     * Add a given object in the output
     *
     * @param clazz The {@link Class} of the object to add
     * @param object The object to add
     */
    void add(Class<?> clazz, Object object) {
        List<Object> objects = this.objectsMap.get(clazz);

        if (objects == null) {
            objects = new ArrayList<>();
        }

        objects.add(object);

        this.objectsMap.put(clazz, objects);
    }

    /**
     * Get an object stored in the output by giving its {@link Class} and index
     *
     * @param index This index represents the position of the object to get in the list of objects with the same class
     * @param clazz The class of the object to get
     * @param <T> The type of the object to return cast
     * @return An object of the type of the class provided
     */
    public <T> T get(int index, Class<T> clazz) {
        final List<Object> objects = this.objectsMap.get(clazz);

        if (objects == null) {
            return null;
        }
        return clazz.cast(objects.get(index));
    }

    public int size(Class<?> clazz) {
        final List<Object> objects = this.objectsMap.get(clazz);

        if (objects == null) {
            return 0;
        }
        return objects.size();
    }

    /**
     * Get an object stored in the output by giving its {@link Class} and index
     *
     * @param clazz The class of the object to get
     * @param <T> The type of the object to return cast
     * @return An object of the type of the class provided
     */
    public <T> T get(Class<T> clazz) {
        return this.get(0, clazz);
    }

}
