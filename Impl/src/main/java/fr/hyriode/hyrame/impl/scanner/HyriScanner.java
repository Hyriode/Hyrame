package fr.hyriode.hyrame.impl.scanner;

import com.google.common.reflect.ClassPath;
import fr.hyriode.hyrame.scanner.IHyriScanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:42
 */
public class HyriScanner implements IHyriScanner {

    @Override
    public Set<Class<?>> scan(ClassLoader classLoader, String packageName) {
        try {
            final Set<Class<?>> classes = new HashSet<>();
            final ClassPath classPath = ClassPath.from(classLoader);

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
                classes.add(Class.forName(classInfo.getName()));
            }

            return classes;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
