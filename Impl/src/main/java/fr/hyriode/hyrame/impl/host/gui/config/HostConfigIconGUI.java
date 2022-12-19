package fr.hyriode.hyrame.impl.host.gui.config;

import fr.hyriode.hyrame.impl.host.config.HostConfigIcon;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 09/08/2022 at 19:20
 */
public class HostConfigIconGUI extends PaginatedInventory {

    private final HostConfigIcon initialIcon;
    private final Consumer<HostConfigIcon> onComplete;

    public HostConfigIconGUI(Player owner, HostConfigIcon initialIcon, Consumer<HostConfigIcon> onComplete) {
        super(owner, name(owner, "gui.host.config.icon.name"), 6 * 9);
        this.initialIcon = initialIcon;
        this.onComplete = onComplete;
        this.paginationManager.setArea(new PaginationArea(10, 43));

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(49, new ItemBuilder(Material.ARROW)
                .withName(HyrameMessage.GO_BACK.asString(this.owner))
                .build(), event -> this.onComplete.accept(this.initialIcon));

        this.addIcons();
    }

    private void addIcons() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (HostConfigIcon icon : HostConfigIcon.values()) {
            final ItemStack itemStack = new ItemBuilder(icon.asItem())
                    .withName(HyrameMessage.HOST_CONFIG_SELECT_ICON_ITEM_NAME.asString(this.owner))
                    .withAllItemFlags()
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> this.onComplete.accept(icon)));
        }

        this.paginationManager.updateGUI();
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addDefaultPagesItems(45, 53);
    }

}
