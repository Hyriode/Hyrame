package fr.hyriode.hyrame.game.util;

import fr.hyriode.hyrame.IHyrame;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/09/2021 at 12:47
 */
public class HyriGameItems {

    public static final String TEAM_SELECTOR_NAME = "team_selector";
    public static final String LEAVE_NAME = "leave_game";
    public static final String SPECTATOR_SETTINGS_NAME = "spectator_settings";
    public static final String SPECTATOR_TELEPORTER_NAME = "spectator_teleporter";
    public static final String RESTART_GAME_NAME = "restart_game";

    public static final GiveAction TEAM_SELECTOR = new GiveAction(TEAM_SELECTOR_NAME);
    public static final GiveAction LEAVE = new GiveAction(LEAVE_NAME);
    public static final GiveAction SPECTATOR_SETTINGS = new GiveAction(SPECTATOR_SETTINGS_NAME);
    public static final GiveAction SPECTATOR_TELEPORTER = new GiveAction(SPECTATOR_TELEPORTER_NAME);
    public static final GiveAction RESTART_GAME = new GiveAction(RESTART_GAME_NAME);

    public static class GiveAction {

        private final String itemName;

        public GiveAction(String itemName) {
            this.itemName = itemName;
        }

        public void give(IHyrame hyrame, Player player, int slot) {
            hyrame.getItemManager().giveItem(player, slot, this.itemName);
        }

    }

}
