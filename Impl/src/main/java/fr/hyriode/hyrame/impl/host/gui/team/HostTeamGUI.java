package fr.hyriode.hyrame.impl.host.gui.team;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.gui.HostGUI;
import fr.hyriode.hyrame.host.option.HostOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by AstFaster
 * on 02/08/2022 at 21:27
 */
public class HostTeamGUI extends HostGUI {

    private final HyriGameTeam team;

    public HostTeamGUI(Player owner, HostCategory category, HyriGameTeam team) {
        super(owner, name(owner, "gui.host." + category.getName() + ".name"), category);
        this.team = team;

        this.addTeamItem();
    }

    @Override
    protected void addOption(int slot, HostOption<?> option) {
        super.addOption(slot, option);
        this.addTeamItem();
    }

    @SuppressWarnings("deprecation")
    private void addTeamItem() {
        if (this.team == null) {
            return;
        }

        final HyriGameTeamColor color = this.team.getColor();
        final List<String> lore = ListReplacer.replace(HyrameMessage.HOST_CATEGORY_TEAM_DESCRIPTION.asList(this.owner), "%color%", color.getChatColor() + color.getDisplay().getValue(this.owner))
                .replace("%friendly_fire%", this.team.isFriendlyFire() ? ChatColor.GREEN + HyrameMessage.COMMON_YES.asString(this.owner) : ChatColor.RED + HyrameMessage.COMMON_NO.asString(this.owner))
                .replace("%name_tag_visibility%", this.team.getNameTagVisibility().display().get().getValue(this.owner))
                .list();

        this.setItem(22, new ItemBuilder(Material.WOOL, 1, this.team.getColor().getDyeColor().getWoolData())
                .withName(this.team.getFormattedDisplayName(this.owner))
                .withLore(lore)
                .removeLoreLines(2)
                .build());
    }

}
