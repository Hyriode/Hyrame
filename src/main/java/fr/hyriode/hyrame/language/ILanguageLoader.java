package fr.hyriode.hyrame.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.plugin.IPluginProvider;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:27
 */
public interface ILanguageLoader {

    /**
     * Load all languages messages from a {@link IPluginProvider}.<br>
     * It will copy files from resources to server data folder.
     *
     * @param pluginProvider The {@link IPluginProvider} object
     * @return The list of loaded {@link HyriLanguageMessage}
     */
    List<HyriLanguageMessage> loadLanguages(IPluginProvider pluginProvider);

}
