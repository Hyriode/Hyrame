package fr.hyriode.hyrame.impl.game.util.spectator;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class SpectatorSettingsItem extends HyriItem<HyramePlugin> {

    public SpectatorSettingsItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.SPECTATOR_SETTINGS_NAME, () -> HyriLanguageMessage.get("item.spectator.settings"), null, Material.REDSTONE_COMPARATOR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new Gui(event.getPlayer()).open();
    }

    private static class Gui extends HyriInventory {

        public Gui(Player owner) {
            super(owner, name(owner, "gui.spectator.settings.title"), 27);

            // Speed items part
            this.setItem(11, new ItemBuilder(Material.LEATHER_BOOTS).withName(ChatColor.GREEN + "Speed 0").build(),
                    e -> this.getOwner().removePotionEffect(PotionEffectType.SPEED));
            this.setItem(12, new ItemBuilder(Material.CHAINMAIL_BOOTS).withName(ChatColor.BLUE + "Speed I").build(),
                    e -> this.addEffect(0));
            this.setItem(13, new ItemBuilder(Material.IRON_BOOTS).withName(ChatColor.YELLOW + "Speed II").build(),
                    e -> this.addEffect(1));
            this.setItem(14, new ItemBuilder(Material.GOLD_BOOTS).withName(ChatColor.GOLD + "Speed III").build(),
                    e -> this.addEffect(2));
            this.setItem(15, new ItemBuilder(Material.DIAMOND_BOOTS).withName(ChatColor.RED + "Speed IV").build(),
                    e -> this.addEffect(3));
        }

        private void addEffect(int amplifier) {
            this.owner.setFlySpeed(amplifier + 1.0f);
            this.owner.removePotionEffect(PotionEffectType.SPEED);
            this.owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, false, false));
        }
    }


}
