package fr.hyriode.hyrame.impl.game.util;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.game.gui.TeamChooserGUI;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemNBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 19:20
 */
public class TeamChooserItem extends HyriItem<HyramePlugin> {

    private static final HyriLanguageMessage DISPLAY_NAME = new HyriLanguageMessage("gui.choose.team.name")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Equipes")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "Teams");

    private static final String SLOT_NBT_KET = TeamChooserItem.class.getSimpleName() + "Slot";

    public TeamChooserItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.TEAM_SELECTOR_NAME, () -> DISPLAY_NAME, Material.WOOL);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new TeamChooserGUI(hyrame, hyrame.getGameManager().getCurrentGame(), event.getPlayer(), new ItemNBT(event.getItem()).getInt(SLOT_NBT_KET)).open();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            final HyriGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

            itemStack.setDurability(gamePlayer.hasTeam() ? gamePlayer.getTeam().getColor().getDyeColor().getWoolData() : (byte) 0);
        } else {
            throw new IllegalStateException("Cannot use team chooser if no game exists!");
        }
        return itemStack;
    }

}
