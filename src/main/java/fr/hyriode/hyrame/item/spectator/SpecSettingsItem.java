package fr.hyriode.hyrame.item.spectator;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpecSettingsItem extends HyriSpectatorItem {

    public SpecSettingsItem(HyramePlugin plugin) {
        super(plugin, "spec_settings_item", "item.spectator.settings.name", "item.spectator.settings.lore", Material.REDSTONE_COMPARATOR_ON);
    }

    @Override
    protected void onClick(IHyrame hyrame, PlayerInteractEvent event) {
        new Gui(hyrame, event.getPlayer()).open();
    }

    public static class Gui extends HyriInventory {

        public Gui(IHyrame hyrame, Player owner) {
            super(owner, HyriInventory.name(hyrame, owner, "gui.spectator.settings.title"), 27);

            // Speed items part
            this.setItem(11, new ItemBuilder(Material.LEATHER_BOOTS).withName("Speed 0").build(),
                    e -> this.getOwner().removePotionEffect(PotionEffectType.SPEED));
            this.setItem(12, new ItemBuilder(Material.CHAINMAIL_BOOTS).withName("Speed I").build(),
                    e -> this.addEffect(1));
            this.setItem(13, new ItemBuilder(Material.IRON_BOOTS).withName("Speed II").build(),
                    e -> this.addEffect(2));
            this.setItem(14, new ItemBuilder(Material.GOLD_BOOTS).withName("Speed III").build(),
                    e -> this.addEffect(3));
            this.setItem(15, new ItemBuilder(Material.DIAMOND_BOOTS).withName("Speed IV").build(),
                    e -> this.addEffect(4));
        }

        private void addEffect(int amplifier) {
            this.getOwner().removePotionEffect(PotionEffectType.SPEED);
            this.getOwner().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, false, false));
        }
    }
}
