package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.host.IHostConfigManager;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.impl.host.config.HostConfigIcon;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by AstFaster
 * on 09/08/2022 at 18:56
 */
public class HostCreateConfigGUI extends HyriInventory {

    private boolean saving;

    private String currentName = "Default";
    private HostConfigIcon currentIcon = HostConfigIcon.PAPER;
    private boolean private_ = false;

    public HostCreateConfigGUI(Player owner, Runnable goBack) {
        super(owner, name(owner, "gui.host.config.create.name"), 6 * 9);

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(0, new ItemBuilder(Material.ARROW)
                .withName(HyrameMessage.GO_BACK.asString(this.owner))
                .build(), event -> goBack.run());

        this.setItem(49, new ItemBuilder(Material.STAINED_GLASS, 1, 5)
                .withName(HyrameMessage.HOST_CONFIG_CREATION_SAVE_ITEM_NAME.asString(this.owner))
                .build(),
                event -> {
                    if (this.saving) {
                        return;
                    }

                    this.saving = true;

                    final IHostConfigManager configManager = HyriAPI.get().getHostConfigManager();
                    final IHyriServer server = HyriAPI.get().getServer();
                    final IHostConfig config = configManager.createConfig(this.owner.getUniqueId(), server.getType(), server.getSubType(), this.currentName, this.currentIcon.getMaterial().name());

                    config.setPrivate(this.private_);

                    for (HostOption<?> option : HyrameLoader.getHyrame().getHostController().getOptions()) {
                        if (!option.isSavable()) {
                            continue;
                        }

                        config.addValue(option.getName(), option.getValue());
                    }

                    config.save();

                    this.owner.sendMessage(HyrameMessage.HOST_CONFIG_SAVED_MESSAGE.asString(this.owner).replace("%name%", this.currentName));

                    goBack.run();
                });

        this.addNameItem();
        this.addIconItem();
        this.addPrivateItem();
    }

    private void addNameItem() {
        this.setItem(21, new ItemBuilder(Material.NAME_TAG)
                .withName(HyrameMessage.HOST_CONFIG_CREATION_NAME_ITEM_NAME.asString(this.owner))
                .withLore(ListReplacer.replace(HyrameMessage.HOST_CONFIG_CREATION_NAME_ITEM_LORE.asList(this.owner), "%current_name%", this.currentName).list())
                .build(),
                event -> new AnvilGUI(HyrameLoader.getHyrame().getPlugin(), this.owner, this.currentName, null, false, player -> this.open(), null, null, (player, name) -> {
                    this.currentName = name;

                    this.addNameItem();
                    this.open();

                    return null;
                }).open());
    }

    private void addIconItem() {
        this.setItem(23, new ItemBuilder(this.currentIcon.asItem())
                .withName(HyrameMessage.HOST_CONFIG_CREATION_ICON_ITEM_NAME.asString(this.owner))
                .withLore(HyrameMessage.HOST_CONFIG_CREATION_ICON_ITEM_LORE.asList(this.owner))
                .withAllItemFlags()
                .build(),
                event -> new HostConfigIconGUI(this.owner, this.currentIcon, icon -> {
                    this.currentIcon = icon;

                    this.addIconItem();
                    this.open();
                }).open());
    }

    private void addPrivateItem() {
        final List<String> lore = ListReplacer.replace(HyrameMessage.HOST_CONFIG_CREATION_PRIVATE_ITEM_LORE.asList(this.owner), "%no_color%", String.valueOf(this.private_ ? ChatColor.GRAY : ChatColor.AQUA))
                .replace("%yes_color%", String.valueOf(this.private_ ? ChatColor.AQUA : ChatColor.GRAY))
                .list();
        this.setItem(31, new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .withName(HyrameMessage.HOST_CONFIG_CREATION_PRIVATE_ITEM_NAME.asString(this.owner))
                .withLore(lore)
                .build(),
                event -> {
                    this.private_ = !this.private_;

                    this.addPrivateItem();
                });
    }

}
