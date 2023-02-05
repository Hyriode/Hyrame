package fr.hyriode.hyrame.impl.language;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.ILanguageLoader;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:38
 */
public class LanguageLoader implements ILanguageLoader {

    @Override
    public Collection<HyriLanguageMessage> loadLanguages(IPluginProvider pluginProvider) {
        final String formattedPluginProviderName = Hyrame.formatPluginProviderName(pluginProvider);
        final File langFolder = new File(pluginProvider.getPlugin().getDataFolder(), "lang");

        if (!langFolder.exists() && !langFolder.mkdirs()) {
            return null;
        }

        try {
            for (HyriLanguage language : HyriLanguage.values()) {
                final String fileName = language.getCode() + ".json";
                final String path = pluginProvider.getLanguagesPath() + fileName;
                final InputStream inputStream = pluginProvider.getClass().getResourceAsStream(path);

                if (inputStream == null) {
                    HyrameLogger.log(Level.INFO, "Cannot get resource from " + fileName + "!" + formattedPluginProviderName);
                    return null;
                }

                final File langFile = new File(langFolder, fileName);
                final boolean fileExists = langFile.exists();

                if (fileExists && HyriAPI.get().getConfig().isDevEnvironment()) {
                    langFile.delete();
                } else if (fileExists) {
                    return null;
                }

                FileUtils.copyInputStreamToFile(inputStream, langFile);
            }

            return HyriAPI.get().getLanguageManager().loadLanguagesMessages(langFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
