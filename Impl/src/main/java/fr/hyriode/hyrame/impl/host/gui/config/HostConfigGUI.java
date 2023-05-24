package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.impl.host.config.HostConfigFormatter;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * Created by AstFaster
 * on 08/08/2022 at 14:54
 */
public abstract class HostConfigGUI extends PaginatedInventory {

    public HostConfigGUI(Player owner, String name, HostCategory parentCategory) {
        super(owner, name(owner, "gui.host.config." + name + ".name"), 6 * 9);
        this.paginationManager.setArea(new PaginationArea(20, 33));

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(3, ItemBuilder.asHead(HyrameHead.GOLD_CRATE)
                .withName(HyrameMessage.HOST_CONFIG_FAVORITE_CONFIGS_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_FAVORITE_CONFIGS_ITEM_LORE.asList(this.owner))
                .build(),
                event -> {
                    this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                    new HostFavoriteConfigsGUI(this.owner, parentCategory).open();
                });

        this.setItem(4, ItemBuilder.asHead(HyrameHead.IRON_CRATE)
                .withName(HyrameMessage.HOST_CONFIG_OWN_CONFIGS_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_OWN_CONFIGS_ITEM_LORE.asList(this.owner))
                .build(),
                event -> {
                    this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);
                    new HostOwnConfigsGUI(this.owner, parentCategory).open();
                });

        this.setItem(5, ItemBuilder.asHead(HyrameHead.JUNGLE_CRATE)
                .withName(HyrameMessage.HOST_CONFIG_ALL_CONFIGS_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_ALL_CONFIGS_ITEM_LORE.asList(this.owner))
                .build(),
                event -> {
                    this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);
                    new HostAllConfigsGUI(this.owner, parentCategory).open();
                });

        this.setItem(18, ItemBuilder.asHead(HyrameHead.MONITOR_PLUS)
                .withName(HyrameMessage.HOST_CONFIG_CREATE_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_CREATE_ITEM_LORE.asList(this.owner))
                .build(),
                event -> new HostCreateConfigGUI(this.owner, () -> {
                    this.addItems().run();
                    this.open();
                }).open());

        this.setItem(27, ItemBuilder.asHead(HyrameHead.GARBAGE_CAN)
                .withName(HyrameMessage.HOST_CONFIG_RESET_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_RESET_ITEM_LORE.asList(this.owner))
                .build(),
                event -> {
                    HyrameLoader.getHyrame().getHostController().resetOptions();

                    this.addItems().run();

                    this.owner.sendMessage(HyrameMessage.HOST_CONFIG_RESET_MESSAGE.asString(this.owner));
                    this.owner.playSound(this.owner.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
                });

        this.setItem(49, new ItemBuilder(Material.ARROW)
                .withName(HyrameMessage.GO_BACK.asString(this.owner))
                .build(), event -> parentCategory.getGUIProvider().apply(this.owner).open());

        this.addItems().run();
    }

    protected abstract Runnable addItems();

    protected void onConfigRightClick(IHostConfig config) {}

    protected void addConfigItems(List<IHostConfig> configs) {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (IHostConfig config : configs) {
            pagination.add(PaginatedItem.from(this.createConfigItem(config), event -> {
                if (event.isLeftClick()) {
                    final IHostController controller = HyrameLoader.getHyrame().getHostController();
                    final IHostConfig currentConfig = controller.getCurrentConfig();

                    if (!this.isCompatible(config)) {
                        return;
                    }

                    if (currentConfig == null || !currentConfig.getId().equals(config.getId())) {
                        controller.applyConfig(config);

                        this.addItems().run();

                        this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        this.owner.sendMessage(HyrameMessage.HOST_CONFIG_LOADED_MESSAGE.asString(this.owner).replace("%name%", config.getId()));
                    }
                } else if (event.isRightClick()) {
                    this.onConfigRightClick(config);
                }
            }));
        }

        this.paginationManager.updateGUI();

        if (pagination.size() == 0) {
            this.setItem(31, new ItemBuilder(Material.BARRIER)
                    .withName(HyrameMessage.HOST_CONFIG_EMPTY_ITEM_NAME.asString(this.owner))
                    .build());
        }
    }

    protected ItemStack createConfigItem(IHostConfig config) {
        final IHostConfig currentConfig = HyrameLoader.getHyrame().getHostController().getCurrentConfig();

        return HostConfigFormatter.createItem(this.owner, config, currentConfig != null && currentConfig.getId().equals(config.getId()), this.isCompatible(config));
    }

    protected boolean isCompatible(IHostConfig config) {
        final IHyriServer server = HyriAPI.get().getServer();

        return server.getType().equals(config.getGame()) && Objects.equals(server.getGameType(), config.getGameType());
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addDefaultPagesItems(45, 53);
    }

}
