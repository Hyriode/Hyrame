package fr.hyriode.hyrame.team;

import fr.hyriode.hyrame.gamemethods.Game;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<Team> createClassicTeams(Plugin plugin, int nbTeam, int teamSize, Game game) {
        ArrayList<Team> teams = new ArrayList<>();
        for (int i = 0; i < nbTeam; i++) {
            final Team team = new Team(plugin ,game ,TeamColor.getValueByID(i), null, teamSize, false);
            teams.add(team);
        }
        return teams;
    }

    public static ArrayList<Team> createClassicTeams(Plugin plugin, int nbTeam, int teamSize, ArrayList<Player> players, Game game) {
        if(players.size() == nbTeam * teamSize) {
            ArrayList<Team> teams = new ArrayList<>();
            for (int i = -1; i < nbTeam; i++) {
                ArrayList<Player> players1 = new ArrayList<Player>();
                for(int i1 = 0; i1 < teamSize + 1; i1++) {
                    players1.add(players.get(i1 * i));
                }
                final Team team = new Team(plugin ,game, TeamColor.getValueByID(i), players1, teamSize, false);
                teams.add(team);
            }
            return teams;
        }else {
            throw new IllegalArgumentException("The number of players must be equal to the places in the teams" + " || players : " + players.size() + " places : " + nbTeam * teamSize);
        }

    }
}
