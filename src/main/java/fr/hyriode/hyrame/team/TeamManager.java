package fr.hyriode.hyrame.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamManager{

    public static ArrayList<Team> teamManager = new ArrayList<Team>();

    public static Team getTeamByPlayer(Player player) {
        Team playerTeam = null;
        for(Team team : teamManager) {
            if(team.getMembers().contains(player)) {
                playerTeam = team;
                break;
            }
        }
        return playerTeam;
    }

    public static void deleteEmptyTeams() {
        teamManager.removeIf(team -> team == null || team.getMembers().isEmpty());
    }
}
