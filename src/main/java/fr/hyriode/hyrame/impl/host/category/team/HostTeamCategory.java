package fr.hyriode.hyrame.impl.host.category.team;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.impl.host.category.HostDefaultCategory;
import fr.hyriode.hyrame.impl.host.gui.team.HostTeamGUI;
import fr.hyriode.hyrame.impl.host.option.TeamColorOption;
import fr.hyriode.hyrame.impl.host.option.TeamNameOption;
import fr.hyriode.hyrame.impl.host.option.TeamNameTagVisibilityOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by AstFaster
 * on 02/08/2022 at 20:29
 */
public class HostTeamCategory extends HostDefaultCategory {

    private final BooleanOption friendlyFireOption;
    private final TeamNameTagVisibilityOption visibilityOption;

    private final HyriGameTeam team;

    public HostTeamCategory(int index, HyriGameTeam team) {
        super("team", Material.WOOL);
        this.team = team;
        this.guiProvider = player -> new HostTeamGUI(player, this, this.team);
        this.visibilityOption = new TeamNameTagVisibilityOption(this.createOptionDisplay("team-name-tag-visibility-" + index, "team-name-tag-visibility", Material.NAME_TAG), this.team.getNameTagVisibility());
        this.friendlyFireOption = new BooleanOption(this.createOptionDisplay("team-friendly-fire-" + index, "team-friendly-fire", Material.IRON_SWORD), this.team.isFriendlyFire());

        this.addOption(29, new TeamNameOption(this.createOptionDisplay("team-name-" + index, "team-name", Material.PAPER), this.team));
        this.addOption(30, this.friendlyFireOption.onChanged(this.team::setFriendlyFire));
        this.addOption(32, this.visibilityOption.onChanged(this.team::setNameTagVisibility));
        this.addOption(33, new TeamColorOption(this.createOptionDisplay("team-color-" + index, "team-color", ItemBuilder.asHead(HyrameHead.COLOR_PICKER).build()), this.team));
    }

    public void updateFromTeam(HyriGameTeam team) {
        this.friendlyFireOption.setValue(team.isFriendlyFire());
        this.visibilityOption.setValue(team.getNameTagVisibility());
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack createItem(Player player) {
        final HyriGameTeamColor color = this.team.getColor();
        final List<String> lore = ListReplacer.replace(HyrameMessage.HOST_CATEGORY_TEAM_DESCRIPTION.asList(player), "%color%", color.getChatColor() + color.getDisplay().getValue(player))
                .replace("%friendly_fire%", this.team.isFriendlyFire() ? ChatColor.GREEN + HyrameMessage.COMMON_YES.asString(player) : ChatColor.RED + HyrameMessage.COMMON_NO.asString(player))
                .replace("%name_tag_visibility%", this.team.getNameTagVisibility().display().get().getValue(player))
                .list();

        return new ItemBuilder(Material.WOOL, 1, this.team.getColor().getDyeColor().getWoolData())
                .withName(this.team.getFormattedDisplayName(player))
                .withLore(lore)
                .nbt().setString(HostDisplay.NBT_KEY, this.name).setBoolean(NBT_KEY, true).build();
    }

}
