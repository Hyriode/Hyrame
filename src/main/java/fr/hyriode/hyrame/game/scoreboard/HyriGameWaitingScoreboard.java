package fr.hyriode.hyrame.game.scoreboard;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.util.References;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.tools.scoreboard.Scoreboard;
import fr.hyriode.tools.scoreboard.ScoreboardLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 11:39
 */
public class HyriGameWaitingScoreboard extends Scoreboard {

    private static final HyriLanguageMessage GAME = new HyriLanguageMessage("scoreboard.game")
            .addValue(HyriLanguage.FR, "Jeu : ")
            .addValue(HyriLanguage.EN, "Game: ");

    private static final HyriLanguageMessage MAP = new HyriLanguageMessage("scoreboard.map")
            .addValue(HyriLanguage.FR, "Carte : ")
            .addValue(HyriLanguage.EN, "Map: ");

    private static final HyriLanguageMessage PLAYERS = new HyriLanguageMessage("scoreboard.players")
            .addValue(HyriLanguage.FR, "Joueurs : ")
            .addValue(HyriLanguage.EN, "Players: ");

    private static final HyriLanguageMessage WAITING = new HyriLanguageMessage("scoreboard.waiting")
            .addValue(HyriLanguage.FR, "En attente de joueurs")
            .addValue(HyriLanguage.EN, "Waiting for players");

    private static final HyriLanguageMessage STARTING = new HyriLanguageMessage("scoreboard.starting")
            .addValue(HyriLanguage.FR, "Lancement dans : ")
            .addValue(HyriLanguage.EN, "Starting in: ");

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private int time = -1;

    private final HyriGame<?> game;

    public HyriGameWaitingScoreboard(HyriGame<?> game, JavaPlugin plugin, Player player) {
        super(plugin, player, "lobby", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Hyriode");
        this.game = game;

        this.addLines();
    }

    private void addLines() {
        this.setLine(0, this.getCurrentDate(), scoreboardLine -> scoreboardLine.setValue(this.getCurrentDate()), 20);
        this.setLine(1, " ");
        this.setLine(2, DASH + GAME.getForPlayer(this.player) + ChatColor.AQUA + this.game.getDisplayName());
        this.setLine(3, "  ");
        this.setLine(4,  DASH + MAP.getForPlayer(this.player) + ChatColor.RED + "TODO with Hystia");
        this.setLine(5,  DASH + this.getPlayersLine(), this.getPlayersLineConsumer(), 10);
        this.setLine(6,  "   ");
        this.setLine(7, this.getWaitingLine(), this.getScoreboardLineConsumer(), 5);
        this.setLine(8,  "    ");
        this.setLine(9, ChatColor.DARK_AQUA + References.SERVER_IP, new HyriGameWaitingScoreboardIpConsumer(References.SERVER_IP), 2);
    }

    private Consumer<ScoreboardLine> getPlayersLineConsumer() {
        return scoreboardLine -> scoreboardLine.setValue(this.getPlayersLine());
    }

    private Consumer<ScoreboardLine> getScoreboardLineConsumer() {
        return scoreboardLine -> {
            if (this.time == -1) {
                scoreboardLine.setValue(this.getWaitingLine());
            } else {
                scoreboardLine.setValue(DASH + STARTING.getForPlayer(this.player) + ChatColor.AQUA + this.time + "s");
            }
        };
    }

    private String getWaitingLine() {
        return ChatColor.RED + WAITING.getForPlayer(this.player);
    }

    private String getPlayersLine() {
        return DASH + PLAYERS.getForPlayer(this.player) + ChatColor.AQUA + this.game.getPlayers().size() + "/" + this.game.getMaxPlayers();
    }

    private String getCurrentDate() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    public void setTime(int time) {
        this.time = time;
    }

}
