package fr.hyriode.hyrame.game.scoreboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.HyriConstants;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/03/2022 at 19:11
 */
public class HyriGameScoreboard<G extends HyriGame<?>> extends HyriScoreboard {

    /** The {@link HyriGame} instance linked to the scoreboard */
    protected final G game;

    /**
     * Constructor of {@link HyriScoreboard}
     *
     * @param plugin Spigot plugin
     * @param game The game linked to the scoreboard
     * @param player Player
     * @param name Scoreboard's name
     */
    public HyriGameScoreboard(JavaPlugin plugin, G game, Player player, String name) {
        super(plugin, player, name, ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + (HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST ? HyriAPI.get().getServer().getHostData().getName() : game.getDisplayName()) + "     ");
        this.game = game;
    }

    /**
     * Add the hostname line at the bottom of the scoreboard
     */
    protected void addHostnameLine() {
        this.setLine(this.lines.size(), ChatColor.DARK_AQUA + HyriConstants.SERVER_IP, new IPLine(HyriConstants.SERVER_IP), 2);
    }

    /**
     * Add a line with the current date.<br>
     * This line will be auto updated every 20 ticks.
     *
     * @param line The number of the line
     */
    protected void addCurrentDateLine(int line) {
        final Supplier<String> date = () -> PlaceholderAPI.setPlaceholders(null, ChatColor.GRAY + "%date%");

        this.setLine(line, date.get(), sbLine -> sbLine.setValue(date.get()), 20);
    }

    /**
     * Add a line with the count of players
     *
     * @param line The number of the line
     * @param prefix The prefix to add before the counter
     * @param total If <code>true</code> it will add the maximum of players next to the current amount of players
     */
    protected void addPlayersLine(int line, String prefix, boolean total) {
        this.setLine(line, ChatColor.WHITE + prefix + ChatColor.AQUA + (this.game.getPlayers().size() - this.game.getDeadPlayers().size()) + (total ? ChatColor.AQUA + "/" + HyriAPI.get().getServer().getSlots() : ""));
    }

    /**
     * Add a line with the count of players but without the total display
     *
     * @param line The number of the line
     * @param prefix The prefix to add before the counter
     */
    protected void addPlayersLine(int line, String prefix) {
        this.addPlayersLine(line, prefix, false);
    }

    /**
     * Add the line with the game time
     *
     * @param line The number of the line
     * @param prefix The prefix to add before the line
     */
    protected void addGameTimeLine(int line, String prefix) {
        this.setLine(line, this.getFormattedGameTime(prefix));

        this.game.getTimer().setOnTimeChanged(newTime -> {
            this.setLine(line, this.getFormattedGameTime(prefix, newTime));

            this.updateLines();
        });
    }

    /**
     * Get the current game time
     *
     * @param prefix The prefix to add before game time
     * @return A formatted game time
     */
    protected String getFormattedGameTime(String prefix) {
        return this.getFormattedGameTime(prefix, this.game.getTimer().getCurrentTime());
    }

    /**
     * Get the current game time
     *
     * @param prefix The prefix of to add before the time
     * @param time The time to format
     * @return A formatted game time
     */
    protected String getFormattedGameTime(String prefix, long time) {
        return ChatColor.WHITE + prefix + ChatColor.AQUA + TimeUtil.formatTime(time);
    }

}
