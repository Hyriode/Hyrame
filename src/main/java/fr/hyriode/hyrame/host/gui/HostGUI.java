package fr.hyriode.hyrame.host.gui;

import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 01/08/2022 at 10:44
 */
public class HostGUI extends PaginatedInventory {

    protected boolean usingPages;

    protected final HostCategory category;

    public HostGUI(Player owner, String name, HostCategory category) {
        super(owner, name, 6 * 9);
        this.category = category;

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        final Function<Player, HostGUI> parentGUIProvider = category.getParentGUIProvider();

        if (parentGUIProvider != null) {
            this.setItem(49, new ItemBuilder(Material.ARROW)
                            .withName(HyrameMessage.GO_BACK.asString(this.owner))
                            .build(),
                    event -> parentGUIProvider.apply(this.owner).open());
        }

        this.addCategories();
        this.addOptions();
    }

    protected void addCategories() {
        for (Map.Entry<Integer, HostCategory> entry : this.category.getSubCategories().entrySet()) {
            final int slot = entry.getKey();
            final HostCategory category = entry.getValue();

            this.setItem(slot, category.createItem(this.owner), event -> category.openGUI(this.owner));
        }
    }

    protected void addOptions() {
        for (Map.Entry<Integer, HostOption<?>> entry : this.category.getOptions().entrySet()) {
            final int slot = entry.getKey();
            final HostOption<?> option = entry.getValue();

            this.addOption(slot, option);
        }
    }

    protected void addOption(int slot, HostOption<?> option) {
        this.setItem(slot, option.createItem(this.owner), event -> {
            if (!option.isAcceptingSecondaryHost() && this.getHostController().getHostData().getSecondaryHosts().contains(this.owner.getUniqueId())) {
                this.owner.sendMessage(HyrameMessage.HOST_NOT_HOST_MESSAGE.asString(this.owner));
                return;
            }

            this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

            option.onClick(this.owner, event);

            updateAll();
        });
    }

    protected IHostController getHostController() {
        return HyrameLoader.getHyrame().getHostController();
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        if (this.usingPages) {
            this.addDefaultPagesItems(45, 53);
        }
    }

    public static void updateAll() {
        for (HostGUI hostGUI : HyrameLoader.getHyrame().getInventoryManager().getInventories(HostGUI.class)) {
            hostGUI.addCategories();
            hostGUI.addOptions();
        }
    }

}
