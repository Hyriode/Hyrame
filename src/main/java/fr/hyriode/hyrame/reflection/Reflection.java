package fr.hyriode.hyrame.reflection;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class Reflection {

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server" + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle").invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> objectClass, String fieldName) {
        try {
            Field field = objectClass.getDeclaredField(fieldName);

            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setField(String fieldName, Object fieldObject, Object value) {
        try {
            final Field field = getField(fieldObject.getClass(), fieldName);

            if (field != null) {
                field.set(fieldObject, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setStaticField(String fieldName, Class<?> clazz, Object value) {
        try {
            final Field field = getField(clazz, fieldName);

            if (field != null) {
                field.set(clazz, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setFinalStaticField(String fieldName, Class<?> clazz, Object newValue) {
        try {
            final Field field = getField(clazz, fieldName);
            final Field modifiersField = getField(Field.class, "modifiers");

            if (field != null && modifiersField != null) {
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, newValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object invokeField(Object fieldObject, String fieldName) {
        try {
            final Field field = getField(fieldObject.getClass(), fieldName);

            if (field != null) {
                return field.get(fieldObject);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> objectClass, String methodName, Class<?>... argumentTypes) {
        final Class<?>[] primitiveTypes = DataType.getPrimitive(argumentTypes);

        for (Method method : objectClass.getMethods()) {
            if (!method.getName().equals(methodName) || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
                continue;
            }
            return method;
        }
        return null;
    }

    public static Object invokeStaticMethod(Class<?> objectClass, String methodName, Object... methodArguments) {
        try {
            return getMethod(objectClass, methodName, DataType.getPrimitive(methodArguments)).invoke(null, methodArguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean inheritOf(Class<?> objectClass, Class<?> parent) {
        Class<?> superClass = objectClass.getSuperclass();

        while (superClass != null) {
            if(superClass.equals(parent)) {
                return true;
            }
            objectClass = superClass;
            superClass = objectClass.getSuperclass();
        }
        return false;
    }

    public static boolean hasConstructorWithParameters(Class<?> clazz, Class<?>... parametersType) {
        boolean result = false;
        for(Constructor<?> constructor : clazz.getConstructors()) {
            if(constructor.getParameterCount() == parametersType.length) {
                for (int i = 0; i < parametersType.length; i++) {
                    result = constructor.getParameterTypes()[i].equals(parametersType[i]);
                }
            }
        }
        return result;
    }

    public static Field[] getFieldsOf(Class<?> clazz, Class<?> targetClass) {
        final List<Field> result = new ArrayList<>();
        final Consumer<Field[]> action = fields -> {
            for (Field field : fields) {
                final Class<?> fieldClass = DataType.getReference(field.getType());

                if (fieldClass == targetClass) {
                    result.add(field);
                }
            }
        };

        action.accept(clazz.getFields());
        action.accept(clazz.getDeclaredFields());

        return result.toArray(new Field[0]);
    }

    public enum DataType {

        BYTE(byte.class, Byte.class),
        SHORT(short.class, Short.class),
        INTEGER(int.class, Integer.class),
        LONG(long.class, Long.class),
        CHARACTER(char.class, Character.class),
        FLOAT(float.class, Float.class),
        DOUBLE(double.class, Double.class),
        BOOLEAN(boolean.class, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>();
        private final Class<?> primitive;
        private final Class<?> reference;

        // Initialize map for quick class lookup
        static {
            for (DataType type : values()) {
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
            }
        }


        DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        public static DataType fromClass(Class<?> clazz) {
            return CLASS_MAP.get(clazz);
        }

        public static Class<?> getPrimitive(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getPrimitive();
        }

        public static Class<?> getReference(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getReference();
        }

        public static Class<?>[] getPrimitive(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getReference(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getPrimitive(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(objects[index].getClass());
            }
            return types;
        }

        public static Class<?>[] getReference(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(objects[index].getClass());
            }
            return types;
        }

        public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
            if (primary == null || secondary == null || primary.length != secondary.length) {
                return false;
            }
            for (int index = 0; index < primary.length; index++) {
                Class<?> primaryClass = primary[index];
                Class<?> secondaryClass = secondary[index];
                if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
                    continue;
                }
                return false;
            }
            return true;
        }

        public Class<?> getPrimitive() {
            return primitive;
        }

        public Class<?> getReference() {
            return reference;
        }
    }

}
