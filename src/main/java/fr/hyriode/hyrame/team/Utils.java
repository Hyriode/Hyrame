package fr.hyriode.hyrame.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Utils {

    public ArrayList<Team> createClassicTeams(int nbTeam, int teamSize) {
        ArrayList<Team> teams = new ArrayList<>();
        for (int i = -1; i < nbTeam; i++) {
            Team team = new Team(TeamColor.getValueByID(i), null, teamSize, false);
            teams.add(team);
        }
        return teams;
    }

    public ArrayList<Team> createClassicTeams(int nbTeam, int teamSize, ArrayList<Player> players) {
        if(players.size() == nbTeam * teamSize) {
            ArrayList<Team> teams = new ArrayList<>();
            for (int i = -1; i < nbTeam; i++) {
                ArrayList<Player> players1 = new ArrayList<Player>();
                for(int i1 = 0; i1 < teamSize + 1; i1++) {
                    players1.add(players.get(i1 * i));
                }
                Team team = new Team(TeamColor.getValueByID(i), players1, teamSize, false);
                teams.add(team);
            }
            return teams;
        }else {
            throw new IllegalArgumentException("The number of players must be equal to the places in the teams" + " || players : " + players.size() + " places : " + nbTeam * teamSize);
        }

    }
}
