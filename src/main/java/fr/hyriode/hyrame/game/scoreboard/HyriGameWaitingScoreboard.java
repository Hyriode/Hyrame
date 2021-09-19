package fr.hyriode.hyrame.game.scoreboard;

import fr.hyriode.common.board.Scoreboard;
import fr.hyriode.common.board.ScoreboardLine;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.language.Language;
import fr.hyriode.hyrame.language.LanguageMessage;
import fr.hyriode.hyrame.util.References;
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

    private static final LanguageMessage GAME = new LanguageMessage("scoreboard.game")
            .addValue(Language.FR, "Jeu : ")
            .addValue(Language.EN, "Game: ");

    private static final LanguageMessage MAP = new LanguageMessage("scoreboard.map")
            .addValue(Language.FR, "Carte : ")
            .addValue(Language.EN, "Map: ");

    private static final LanguageMessage PLAYERS = new LanguageMessage("scoreboard.players")
            .addValue(Language.FR, "Joueurs : ")
            .addValue(Language.EN, "Players: ");

    private static final LanguageMessage WAITING = new LanguageMessage("scoreboard.waiting")
            .addValue(Language.FR, "En attente")
            .addValue(Language.EN, "Waiting");

    private static final LanguageMessage STARTING = new LanguageMessage("scoreboard.starting")
            .addValue(Language.FR, "Lancement dans : ")
            .addValue(Language.EN, "Starting in:");

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
        this.setLine(4,  DASH + MAP.getForPlayer(this.player) + ChatColor.AQUA + "Aucune");
        this.setLine(5,  DASH + this.getPlayersLine(), this.getPlayersLineConsumer(), 10);
        this.setLine(6,  "   ");
        this.setLine(7, this.getWaitingLine(), this.getScoreboardLineConsumer(), 10);
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
                scoreboardLine.setValue(DASH + "Lancement dans : " + ChatColor.AQUA + this.time + "s");
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
