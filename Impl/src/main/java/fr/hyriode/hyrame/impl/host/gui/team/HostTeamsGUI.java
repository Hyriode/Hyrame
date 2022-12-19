package fr.hyriode.hyrame.impl.host.gui.team;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.impl.host.category.team.HostTeamCategory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:59
 */
public class HostTeamsGUI extends HostGUI {

    public HostTeamsGUI(Player owner, HostCategory category) {
        super(owner, name(owner, "gui.host." + category.getName() + ".name"), category);
        this.paginationManager.setArea(new PaginationArea(19, 34));
        this.usingPages = true;

        this.addCategories();
    }

    @Override
    protected void addCategories() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (HostCategory category : this.category.getSubCategories().values()) {
            if (!(category instanceof HostTeamCategory)) {
                continue;
            }

            pagination.add(PaginatedItem.from(category.createItem(this.owner), event -> category.openGUI(this.owner)));
        }

        this.paginationManager.updateGUI();
    }

}
