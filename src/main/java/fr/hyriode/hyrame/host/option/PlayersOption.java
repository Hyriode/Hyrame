package fr.hyriode.hyrame.host.option;

import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.language.HyrameMessage;

/**
 * Created by AstFaster
 * on 02/08/2022 at 21:07
 */
public class PlayersOption extends IntegerOption {

    public PlayersOption(HostDisplay display, int defaultValue, int minimum, int maximum) {
        super(display, defaultValue, minimum, maximum);
        this.valueFormatter = player -> HyrameMessage.HOST_OPTION_PLAYERS_FORMATTER.asString(player).replace("%value%", String.valueOf(this.value));
    }

}
