package fr.hyriode.hyrame.utils;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class ScoreboardHelp {


    public static Scoreboard createScoreboard(String name, ArrayList<String> strings) {

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("general", "dummy");

        objective.setDisplayName(name);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(String s : strings) {
            Score score = objective.getScore(s);
            score.setScore(strings.size() - s.indexOf(s));
        }

        return scoreboard;
    }
}
