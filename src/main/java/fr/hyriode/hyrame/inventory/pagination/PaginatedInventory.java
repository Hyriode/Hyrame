package fr.hyriode.hyrame.inventory.pagination;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 23:02
 *
 * The inventory that supports pagination system.<br>
 * Pagination can be used to display items dynamically by giving a list of items.
 */
public abstract class PaginatedInventory extends HyriInventory {

    /** The {@linkplain PaginationManager pagination manager} instance */
    protected final PaginationManager paginationManager;

    /**
     * Default constructor of {@linkplain PaginatedInventory paginated inventory} object.<br>
     * It initializes in the way as an {@link HyriInventory}
     *
     * @param owner The {@linkplain Player owner} of the inventory
     * @param name The name of the inventory (it will be displayed to the player)
     * @param size The size of the inventory (max: 54)
     */
    public PaginatedInventory(Player owner, String name, int size) {
        super(owner, name, size);
        this.paginationManager = new PaginationManager(this);
    }

    /**
     * Add some default pages items
     */
    protected void addDefaultPagesItems(int backSlot, int nextSlot) {
        if (this.paginationManager == null) {
            return;
        }

        final int currentPage = this.paginationManager.currentPage() + 1;
        int totalPages = this.paginationManager.getPagination().totalPages();

        if (totalPages == 0) {
            totalPages = 1;
        }

        this.setItem(backSlot, ItemBuilder.asHead(HyrameHead.MONITOR_BACKWARD)
                .withName(HyrameMessage.PAGINATION_PREVIOUS_PAGE_ITEM_NAME.asString(this.owner).replace("%current_page%", String.valueOf(currentPage)).replace("%total_pages%", String.valueOf(totalPages)))
                .withLore(HyrameMessage.PAGINATION_PREVIOUS_PAGE_ITEM_LORE.asList(this.owner))
                .build(), event -> this.paginationManager.previousPage());

        this.setItem(nextSlot, ItemBuilder.asHead(HyrameHead.MONITOR_FORWARD)
                .withName(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_NAME.asString(this.owner).replace("%current_page%", String.valueOf(currentPage)).replace("%total_pages%", String.valueOf(totalPages)))
                .withLore(HyrameMessage.PAGINATION_NEXT_PAGE_ITEM_LORE.asList(this.owner))
                .build(), event -> this.paginationManager.nextPage());
    }

    /**
     * Second constructor of {@linkplain PaginatedInventory paginated inventory} object.<br>
     *
     * @param owner The {@linkplain Player owner} of the inventory
     * @param size The size of the inventory (max: 54)
     */
    public PaginatedInventory(Player owner, int size) {
        this(owner, "", size);
    }

    /**
     * This method is called each the pagination needs to be updated.<br>
     * If you have an {@linkplain PaginationArea area} setup you don't need to reset items (it will be done automatically)
     *
     * @param page The current page of the inventory
     * @param items The list of that are on this page
     */
    public abstract void updatePagination(int page, List<PaginatedItem> items);

    /**
     * Get the {@linkplain PaginationManager pagination manager} related to the inventory
     *
     * @return A {@link PaginationManager} instance
     */
    public PaginationManager getPaginationManager() {
        return this.paginationManager;
    }

}
