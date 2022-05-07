package fr.hyriode.hyrame.reflection;

import java.lang.reflect.Field;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 19:14
 */
public class ObjectModifier<T> {

    private final Class<T> targetClass;
    private final Object object;

    private final Field[] fields;

    public ObjectModifier(Class<T> targetClass, Object object) {
        this.targetClass = targetClass;
        this.object = object;
        this.fields = Reflection.getFieldsOf(object.getClass(), targetClass);
    }

    public T read(int index) {
        if (fields.length == 0) {
            return null;
        }

        if (index < 0) {
            return null;
        }

        if (fields.length >= index) {
            return this.targetClass.cast(Reflection.invokeField(this.object, fields[index].getName()));
        }
        return null;
    }

    public ObjectModifier<T> write(int index, T value) {
        if (fields.length == 0) {
            return this;
        }

        if (index < 0) {
            return this;
        }

        if (fields.length >= index) {
            Reflection.setField(fields[index].getName(), this.object, value);
        }
        return this;
    }

}
