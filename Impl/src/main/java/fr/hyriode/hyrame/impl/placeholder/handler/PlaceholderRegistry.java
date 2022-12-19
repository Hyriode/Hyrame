package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:27
 */
public class PlaceholderRegistry {

    public static void registerPlaceholders(IHyrame hyrame) {
        HyrameLogger.log("Registering default placeholders...");

        PlaceholderAPI.registerHandler(new PLanguageHandler());
        PlaceholderAPI.registerHandler(new PNetworkHandler());
        PlaceholderAPI.registerHandler(new PServerHandler());
        PlaceholderAPI.registerHandler(new PPlayerHandler());
        PlaceholderAPI.registerHandler(new PURLHandler());
        PlaceholderAPI.registerHandler(new PUtilHandler());
        PlaceholderAPI.registerHandler(new PGameHandler(hyrame));
    }

}
