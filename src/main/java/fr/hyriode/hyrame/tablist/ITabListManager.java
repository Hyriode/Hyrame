package fr.hyriode.hyrame.tablist;

import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by AstFaster
 * on 30/05/2022 at 19:44
 */
public interface ITabListManager {

    void enable();

    void disable();

    void registerTeam(HyriScoreboardTeam team);

    void unregisterTeam(String teamName);

    void updateTeam(String teamName);

    HyriScoreboardTeam getTeam(String teamName);

    boolean containsTeam(String teamName);

    void addPlayerInTeam(Player player, String teamName);

    void removePlayerFromTeam(Player player);

    HyriScoreboardTeam getPlayerTeam(Player player);

    Set<HyriScoreboardTeam> getTeams();

    void hideNameTag(Player player);

    void showNameTag(Player player);

    boolean isEnabled();

}