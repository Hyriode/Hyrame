package fr.hyriode.hyrame.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 09:01
 */
public class HyriCommandOutput {

    /** All the output objects */
    private final List<HyriCommandOutputObjects<?>> objectsList;

    /**
     * Constructor of {@link HyriCommandOutput}
     */
    public HyriCommandOutput() {
        this.objectsList = new ArrayList<>();
    }

    /**
     * Add an object to the output
     *
     * @param clazz Object's class
     * @param value The object
     * @param <T> A simple type
     */
    public <T> void add(Class<T> clazz, T value) {
        HyriCommandOutputObjects<T> object = this.getObjects(clazz);

        if (object == null) {
            object = new HyriCommandOutputObjects<>(clazz);

            this.objectsList.add(object);
        }

        object.add(value);
    }

    /**
     * Get a value of a class
     *
     * @param index An index
     * @param clazz Class of the object
     * @param <T> A simple type
     * @return The value
     */
    public <T> T get(int index, Class<T> clazz) {
        final HyriCommandOutputObjects<T> object = this.getObjects(clazz);

        if (object != null) {
            return this.getObjects(clazz).get(index);
        }
        throw new IllegalArgumentException("Couldn't find output objects with the provided class (" + clazz + ")!");
    }

    /**
     * Get a value of a class
     *
     * @param clazz Class of the object
     * @param <T> A simple type
     * @return The value
     */
    public <T> T get(Class<T> clazz) {
        return this.get(0, clazz);
    }

    /**
     * Get all output objects from a class
     *
     * @param clazz Class of the objects
     * @param <T> A simple type
     * @return {@link HyriCommandOutputObjects}
     */
    @SuppressWarnings("unchecked")
    public <T> HyriCommandOutputObjects<T> getObjects(Class<T> clazz) {
        for (HyriCommandOutputObjects<?> objects : this.objectsList) {
            if (clazz.isAssignableFrom(objects.getClazz())) {
                return (HyriCommandOutputObjects) objects;
            }
        }
        return null;
    }

    /**
     * This class is representing an output objects
     *
     * @param <T> The type of the objects
     */
    public static class HyriCommandOutputObjects<T> {

        /** Objects class */
        private final Class<T> clazz;
        /** All the values */
        private final List<T> values;

        /**
         * Constructor of {@link HyriCommandOutputObjects}
         *
         * @param clazz Objects class
         */
        public HyriCommandOutputObjects(Class<T> clazz) {
            this.clazz = clazz;
            this.values = new ArrayList<>();
        }

        /**
         * Add an object
         *
         * @param value Object to add
         */
        public void add(T value) {
            this.values.add(value);
        }

        /**
         * Get an object
         *
         * @param index Index of the object in the values
         * @return The object at the provided index
         */
        public T get(int index) {
            return this.values.get(index);
        }

        /**
         * Get objects class
         *
         * @return A {@link Class}
         */
        public Class<T> getClazz() {
            return this.clazz;
        }

        /**
         * Get all values
         *
         * @return A list of values
         */
        public List<T> getValues() {
            return this.values;
        }

    }

}
