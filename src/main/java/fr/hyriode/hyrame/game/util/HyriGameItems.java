package fr.hyriode.hyrame.game.util;

import fr.hyriode.hyrame.IHyrame;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/09/2021 at 12:47
 */
public class HyriGameItems {

    public static final String TEAM_CHOOSER_NAME = "team_chooser";
    public static final String LEAVE_NAME = "leave_game";

    public static final GiveConsumer<IHyrame, Player, Integer> TEAM_CHOOSER = (hyrame, player, slot) -> hyrame.getItemManager().giveItem(player, slot, TEAM_CHOOSER_NAME);
    public static final GiveConsumer<IHyrame, Player, Integer> LEAVE_ITEM = (hyrame, player, slot) -> hyrame.getItemManager().giveItem(player, slot, LEAVE_NAME);

    @FunctionalInterface
    public interface GiveConsumer<T, T1, T2> {

        void give(T t, T1 t1, T2 t2);

    }

}
