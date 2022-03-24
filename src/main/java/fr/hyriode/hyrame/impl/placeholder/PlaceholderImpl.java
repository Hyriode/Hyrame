package fr.hyriode.hyrame.impl.placeholder;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.placeholder.handler.PlaceholderRegistry;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.placeholder.PlaceholderHandler;
import org.bukkit.OfflinePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 18:14
 */
public class PlaceholderImpl extends PlaceholderAPI {

    private static final char CHARACTER = '%';

    @Override
    public String replacePlaceholders(OfflinePlayer player, String text) {
        final char[] characters = text.toCharArray();
        final StringBuilder placeholderBuilder = new StringBuilder();

        String result = text;
        boolean startFound = false;
        for (char character : characters) {
            if (character == CHARACTER && !startFound) {
                startFound = true;
                continue;
            }
            if (startFound && character == ' ') {
                placeholderBuilder.setLength(0);
                startFound = false;
                continue;
            }
            if (startFound && character == CHARACTER && placeholderBuilder.length() != 0) {
                final String placeholder = placeholderBuilder.toString();
                final String replacement = this.getReplacement(player, placeholder);

                if (replacement != null) {
                    result = result.replace(CHARACTER + placeholder + CHARACTER, replacement);
                }

                placeholderBuilder.setLength(0);
                startFound = false;
                continue;
            }
            if (startFound) {
                placeholderBuilder.append(character);
            }
        }
        return result;
    }

    private String getReplacement(OfflinePlayer player, String placeholder) {
        for (PlaceholderHandler handler : this.handlers) {
            final String replacement = handler.preHandle(player, placeholder);

            if (replacement == null || replacement.isEmpty()) {
                continue;
            }

            return replacement;
        }
        return null;
    }

}
