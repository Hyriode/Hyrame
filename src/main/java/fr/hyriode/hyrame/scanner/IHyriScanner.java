package fr.hyriode.hyrame.scanner;

import java.util.Set;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:38
 */
public interface IHyriScanner {

    /**
     * Get all {@link Class} matching a given type in a provided package
     *
     * @param classLoader The {@link ClassLoader} to use
     * @param packageName The package to scan
     * @param type The type of classes to find
     * @return All the classes found
     */
   Set<Class<?>> scan(ClassLoader classLoader, String packageName, Class<?> type);

}
