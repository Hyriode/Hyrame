package fr.hyriode.hyrame.game.scoreboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.HyriConstants;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 11:39
 */
public class HyriWaitingScoreboard extends HyriScoreboard {

    private static final HyriLanguageMessage MAP = new HyriLanguageMessage("scoreboard.map")
            .addValue(HyriLanguage.FR, "Carte: ")
            .addValue(HyriLanguage.EN, "Map: ");

    private static final HyriLanguageMessage PLAYERS = new HyriLanguageMessage("scoreboard.players")
            .addValue(HyriLanguage.FR, "Joueurs: ")
            .addValue(HyriLanguage.EN, "Players: ");

    private static final HyriLanguageMessage WAITING = new HyriLanguageMessage("scoreboard.waiting")
            .addValue(HyriLanguage.FR, "En attente de joueurs")
            .addValue(HyriLanguage.EN, "Waiting for players");

    private static final HyriLanguageMessage STARTING = new HyriLanguageMessage("scoreboard.starting")
            .addValue(HyriLanguage.FR, "Lancement dans: ")
            .addValue(HyriLanguage.EN, "Starting in: ");

    private static final String DASH = ChatColor.WHITE + " ⁃ ";

    private int time = -1;

    public HyriWaitingScoreboard(HyriGame<?> game, JavaPlugin plugin, Player player) {
        super(plugin, player, "waiting", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + game.getDisplayName());

        this.addLines();

        String map = HyriAPI.get().getServer().getMap();

        if (map != null) {
            map = "Unknown";
        }

        this.setLine(0, ChatColor.GRAY + TimeUtil.getCurrentFormattedDate(), line -> line.setValue(ChatColor.GRAY + TimeUtil.getCurrentFormattedDate()), 20);
        this.addBlankLine(1);
        this.setLine(2,  DASH + MAP.getForPlayer(this.player) + ChatColor.AQUA + map);
        this.addBlankLine(4);
        this.addBlankLine(6);
        this.setLine(7, ChatColor.DARK_AQUA + HyriConstants.SERVER_IP, new HyriScoreboardIpConsumer(HyriConstants.SERVER_IP), 2);
    }

    private void addLines() {
        this.setLine(3, this.getPlayersLine());
        this.setLine(5, this.getStartingLine());
    }

    public void update() {
        this.addLines();
        this.updateLines();
    }

    private String getStartingLine() {
        if (this.time == -1) {
           return this.getWaitingLine();
        } else {
            return DASH + STARTING.getForPlayer(this.player) + ChatColor.AQUA + this.time + "s";
        }
    }

    private String getWaitingLine() {
        return ChatColor.RED + WAITING.getForPlayer(this.player);
    }

    private String getPlayersLine() {
        return PlaceholderAPI.setPlaceholders(null, DASH + PLAYERS.getForPlayer(this.player) + ChatColor.AQUA + "%game_players%/%game_max_players%");
    }

    public void setTime(int time) {
        this.time = time;
    }

}
