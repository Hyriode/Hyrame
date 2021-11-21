package fr.hyriode.hyrame.scanner;

import java.util.Set;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:38
 */
public interface IHyriScanner {

    /**
     * Get all {@link Class} in a provided package
     *
     * @param classLoader - {@link ClassLoader} to use
     * @param packageName - Package to scan
     * @return - All classes found
     */
    Set<Class<?>> scan(ClassLoader classLoader, String packageName);

}
