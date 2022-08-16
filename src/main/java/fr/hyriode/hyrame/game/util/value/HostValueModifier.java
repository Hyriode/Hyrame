package fr.hyriode.hyrame.game.util.value;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLoader;

/**
 * Created by AstFaster
 * on 16/08/2022 at 11:00
 */
public class HostValueModifier<T> extends ValueModifier<T> {

    public HostValueModifier(int priority, Class<T> valueClass, String optionName) {
        super(priority, () -> HyriAPI.get().getServer().isHost(), () -> HyrameLoader.getHyrame().getHostController().findOption(optionName).castValue(valueClass));
    }

}
