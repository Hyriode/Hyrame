package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.hyrame.placeholder.PlaceholderHandler;
import fr.hyriode.hyrame.utils.TimeUtil;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 10:25
 */
public class PUtilHandler extends PlaceholderHandler {

    @Override
    public String handle(Player player, String placeholder) {
        switch (placeholder) {
            case "date":
                return TimeUtil.getCurrentFormattedDate();
        }
        return null;
    }
}
