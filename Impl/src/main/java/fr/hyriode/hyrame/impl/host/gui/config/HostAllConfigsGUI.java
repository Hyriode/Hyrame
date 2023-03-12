package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.host.IHostConfigManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AstFaster
 * on 08/08/2022 at 15:20
 */
public class HostAllConfigsGUI extends HostConfigGUI {

    private IHostConfig searchedConfig;

    public HostAllConfigsGUI(Player owner, HostCategory parentCategory) {
        super(owner, "all-configs", parentCategory);

        this.setItem(26, new ItemBuilder(Material.SIGN)
                .withName(HyrameMessage.HOST_CONFIG_SEARCH_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_SEARCH_ITEM_LORE.asList(this.owner))
                .build(),
                event -> new AnvilGUI(IHyrame.get().getPlugin(), this.owner, HyrameMessage.HOST_CONFIG_SEARCH_INPUT.asString(this.owner), null, false, player -> Bukkit.getScheduler().runTaskLater(IHyrame.get().getPlugin(), this::open, 1L), null, null, (player, configId) -> {
                    configId = configId.replace("#", "");

                    final IHostConfig config = HyriAPI.get().getHostConfigManager().getConfig(configId);

                    if (config != null) {
                        this.searchedConfig = config;
                    } else {
                        this.owner.sendMessage(HyrameMessage.HOST_CONFIG_SEARCH_INVALID_ID_MESSAGE.asString(this.owner).replace("%id%", "#" + configId));
                    }

                    this.addItems().run();
                    this.open();

                    return null;
                }).open());
    }

    @Override
    protected ItemStack createConfigItem(IHostConfig config) {
        final ItemStack original = super.createConfigItem(config);
        final boolean alreadyFavorite = IHyriPlayer.get(this.owner.getUniqueId()).getHosts().hasFavoriteConfig(config.getId());

        if (original == null) {
            return null;
        }

        final ItemBuilder builder = new ItemBuilder(original);

        if (alreadyFavorite) {
            builder.withName(ChatColor.GOLD + Symbols.STAR + " " + builder.getName());
        } else {
            builder.appendLore(HyrameMessage.HOST_CLICK_TO_ADD_TO_FAVORITES.asString(this.owner));
        }
        return builder.build();
    }

    @Override
    protected Runnable addItems() {
        return () -> {
            if (this.searchedConfig != null) {
                super.addConfigItems(Collections.singletonList(this.searchedConfig));
            } else {
                final IHostConfigManager configManager = HyriAPI.get().getHostConfigManager();
                final List<IHostConfig> configs = new ArrayList<>();

                for (String configId : configManager.getConfigs()) {
                    final IHostConfig config = configManager.getConfig(configId);

                    if (config.isPrivate()) {
                        continue;
                    }

                    configs.add(config);
                }

                super.addConfigItems(configs);
            }
        };
    }

    @Override
    protected void onConfigRightClick(IHostConfig config) {
        if (IHyriPlayer.get(this.owner.getUniqueId()).getHosts().hasFavoriteConfig(config.getId())) {
            return;
        }

        final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());

        account.getHosts().addFavoriteConfig(config.getId());
        account.update();

        this.addItems().run();

        this.owner.sendMessage(HyrameMessage.HOST_CONFIG_FAVORITE_ADD_MESSAGE.asString(this.owner));
    }

}
