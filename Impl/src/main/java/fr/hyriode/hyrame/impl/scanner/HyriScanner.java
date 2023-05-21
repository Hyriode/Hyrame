package fr.hyriode.hyrame.impl.scanner;

import fr.hyriode.hyrame.scanner.IHyriScanner;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 17:42
 */
public class HyriScanner implements IHyriScanner {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Class<?>> scan(ClassLoader classLoader, String packageName, Class<?> type) {
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packageName)
                .setUrls(ClasspathHelper.forPackage(packageName, classLoader))
                .filterInputsBy(new FilterBuilder().includePackage(packageName)));

        return (Set<Class<?>>) reflections.getSubTypesOf(type);
    }

}
