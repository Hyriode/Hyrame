package fr.hyriode.hyrame.impl.host.category.team;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.impl.host.category.HostDefaultCategory;
import fr.hyriode.hyrame.impl.host.gui.team.HostTeamsGUI;
import fr.hyriode.hyrame.impl.host.option.TeamNameTagVisibilityOption;
import fr.hyriode.hyrame.impl.host.option.TeamsSizeOption;
import org.bukkit.Material;

import java.util.function.Consumer;

import static fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam.NameTagVisibility;

/**
 * Created by AstFaster
 * on 01/08/2022 at 00:50
 */
public class HostTeamsCategory extends HostDefaultCategory {

    public static final String TEAMS_SIZE_KEY = "teams-size";

    public HostTeamsCategory() {
        super("teams", Material.WOOL);
        this.guiProvider = player -> new HostTeamsGUI(player, this);

        NameTagVisibility defaultVisibility = NameTagVisibility.ALWAYS;
        boolean defaultFriendlyFire = false;
        int index = 0;
        for (HyriGameTeam team : HyrameLoader.getHyrame().getGameManager().getCurrentGame().getTeams()) {
            defaultVisibility = team.getNameTagVisibility();
            defaultFriendlyFire = team.isFriendlyFire();

            this.addSubCategory(index, new HostTeamCategory(index, team));

            index++;
        }

        this.addOption(1, new BooleanOption(this.createOptionDisplay("teams-friendly-fire", Material.IRON_SWORD), defaultFriendlyFire)
                .onChanged(friendlyFire -> this.runOnTeams(team -> {
                    team.setFriendlyFire(friendlyFire);

                    this.runOnCategories(category -> category.updateFromTeam(team));
                }))
                .notSavable());
        this.addOption(3, new TeamNameTagVisibilityOption(this.createOptionDisplay("teams-name-tag-visibility", Material.NAME_TAG), defaultVisibility)
                .onChanged(visibility -> this.runOnTeams(team -> {
                    team.setNameTagVisibility(visibility);

                    this.runOnCategories(category -> category.updateFromTeam(team));
                }))
                .notSavable());
        this.addOption(8, new TeamsSizeOption(this.createOptionDisplay(TEAMS_SIZE_KEY, Material.REDSTONE_COMPARATOR)));
    }

    private void runOnCategories(Consumer<HostTeamCategory> categoryConsumer) {
        for (HostCategory category : this.subCategories.values()) {
            if (!(category instanceof HostTeamCategory)) {
                continue;
            }

            categoryConsumer.accept((HostTeamCategory) category);
        }
    }

    private void runOnTeams(Consumer<HyriGameTeam> teamConsumer) {
        for (HyriGameTeam team : HyrameLoader.getHyrame().getGameManager().getCurrentGame().getTeams()) {
            teamConsumer.accept(team);
        }
    }

}
