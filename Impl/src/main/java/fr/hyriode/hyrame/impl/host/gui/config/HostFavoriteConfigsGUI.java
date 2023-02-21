package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 08/08/2022 at 15:20
 */
public class HostFavoriteConfigsGUI extends HostConfigGUI {

    public HostFavoriteConfigsGUI(Player owner, HostCategory parentCategory) {
        super(owner, "favorite-configs", parentCategory);
    }

    private List<IHostConfig> getFavoriteConfigs() {
        final List<IHostConfig> configs = new ArrayList<>();
        final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());
        final List<String> invalidConfigs = new ArrayList<>();

        for (String configId : account.getHosts().getFavoriteConfigs()) {
            final IHostConfig config = HyriAPI.get().getHostConfigManager().getConfig(configId);

            if (config == null || (config.isPrivate() && !config.getOwner().equals(account.getUniqueId()))) {
                invalidConfigs.add(configId);
                continue;
            }

            configs.add(config);
        }

        for (String invalidConfig : invalidConfigs) {
            account.getHosts().removeFavoriteConfig(invalidConfig);
        }

        account.update();

        return configs;
    }

    @Override
    protected ItemStack createConfigItem(IHostConfig config) {
        final ItemStack original = super.createConfigItem(config);

        return original == null ? null : new ItemBuilder(original).appendLore(HyrameMessage.HOST_CLICK_TO_REMOVE_FROM_FAVORITES.asString(this.owner)).build();
    }

    @Override
    protected Runnable addItems() {
        return () -> this.addConfigItems(this.getFavoriteConfigs());
    }

    @Override
    protected void onConfigRightClick(IHostConfig config) {
        final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());

        account.getHosts().removeFavoriteConfig(config.getId());
        account.update();

        this.addItems().run();

        this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
        this.owner.sendMessage(HyrameMessage.HOST_CONFIG_FAVORITE_REMOVE_MESSAGE.asString(this.owner));
    }

}
