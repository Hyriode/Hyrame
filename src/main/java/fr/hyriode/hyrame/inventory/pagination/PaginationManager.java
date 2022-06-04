package fr.hyriode.hyrame.inventory.pagination;

import fr.hyriode.hyrame.utils.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 23:03
 *
 * This manager is used to switch easily from a page to another.<br>
 * It's also useful to handle {@linkplain PaginationArea pagination area} automatically.
 */
public class PaginationManager {

    /** The area of the pagination */
    private PaginationArea area;

    /** The current page */
    private int currentPage;

    /** The {@linkplain PaginatedItem paginated items} handler */
    private final Pagination<PaginatedItem> pagination;
    /** The GUI using the manager */
    private final PaginatedInventory gui;

    /**
     * Default constructor of a {@linkplain PaginationManager pagination manager}
     *
     * @param gui The GUI related to the manager
     */
    public PaginationManager(PaginatedInventory gui) {
        this.pagination = new Pagination<>(0, new ArrayList<>());
        this.gui = gui;
        this.currentPage = 0;

       this.updateGUI();
    }

    /**
     * Update the GUI.<br
     * If a {@linkplain PaginationArea pagination area} is used, it will refresh it, else it will call {@link PaginatedInventory#updatePagination(int, List)}
     */
    public void updateGUI() {
        final List<PaginatedItem> items = this.pagination.getPageContent(this.currentPage);

        if (this.area != null) {
            final int start = this.area.getStart();
            final int end = this.area.getEnd();
            final boolean normal = start < end;

            int index = 0;
            for (int slot = (normal ? start : end); slot < (normal ? end : start); slot++) {
                if (items.size() > index) {
                    final PaginatedItem item = items.get(index);

                    this.gui.setItem(slot, item.asBukkit(), item.getEventConsumer());
                } else {
                    this.gui.setItem(slot, null);
                }
                index++;
            }
        }

        this.gui.updatePagination(this.currentPage, items);
    }

    /**
     * Switch to the next page.<br>
     *
     * @return <code>true</code> if a next page is available
     */
    public boolean nextPage() {
        this.currentPage++;

        if (!this.pagination.existsPage(this.currentPage)) {
            this.currentPage--;
            return false;
        }

        this.updateGUI();
        return true;
    }

    /**
     * Switch to the previous page.<br>
     *
     * @return <code>true</code> if a previous page is available
     */
    public boolean previousPage() {
        this.currentPage--;

        if (!this.pagination.existsPage(this.currentPage)) {
            this.currentPage++;
            return false;
        }

        this.updateGUI();
        return true;
    }

    /**
     * Get the current page
     *
     * @return A page number
     */
    public int currentPage() {
        return this.currentPage;
    }

    /**
     * Set the pagination area to refresh during updates
     *
     * @param area The {@linkplain PaginationArea area} object
     */
    public void setArea(PaginationArea area) {
        this.area = area;

        this.pagination.setPageSize(area.size());
    }

    /**
     * Get the area object; it can be <code>null</code>
     *
     * @return A {@link PaginationArea} object or <code>null</code>
     */
    public PaginationArea area() {
        return this.area;
    }

    /**
     * Get the pagination handler (for items) instance
     *
     * @return The {@link Pagination pagination handler}
     */
    public Pagination<PaginatedItem> getPagination() {
        return this.pagination;
    }

}
