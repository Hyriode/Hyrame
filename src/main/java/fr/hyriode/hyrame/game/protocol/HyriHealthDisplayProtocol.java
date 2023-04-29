package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.hyrame.IHyrame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by AstFaster
 * on 29/05/2022 at 17:14
 */
public class HyriHealthDisplayProtocol extends HyriGameProtocol {

    private static final String HEALTH_HEAD = "health-head";
    private static final String HEALTH_TAB = "health-tab";

    private final Scoreboard scoreboard;
    private final Options options;

    public HyriHealthDisplayProtocol(IHyrame hyrame, Options options) {
        super(hyrame, "health_display");
        this.options = options;
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    @Override
    void enable() {
        if (this.options.isHead()) {
            final Objective headObjective = this.scoreboard.registerNewObjective(HEALTH_HEAD, "health");

            headObjective.setDisplayName(ChatColor.RED + "❤");
            headObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

            for (Player player : Bukkit.getOnlinePlayers()) {
                headObjective.getScore(player.getName()).setScore(20);
            }
        }

        if (this.options.isTabList()) {
            final Objective tabObjective = this.scoreboard.registerNewObjective(HEALTH_TAB, "health");

            tabObjective.setDisplayName(ChatColor.RED + "❤");
            tabObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            for (Player player : Bukkit.getOnlinePlayers()) {
                tabObjective.getScore(player.getName()).setScore(20);
            }
        }
    }

    @Override
    void disable() {
        final Objective headObjective = this.scoreboard.getObjective(HEALTH_HEAD);
        final Objective tabObjective = this.scoreboard.getObjective(HEALTH_TAB);

        if (headObjective != null) {
            headObjective.unregister();
        }

        if (tabObjective != null) {
            tabObjective.unregister();
        }
    }

    public Options getOptions() {
        return this.options;
    }

    public static class Options {

        /** Is health showing above head */
        private boolean head;
        /** Is head showing in the tab list */
        private boolean tabList;

        public Options() {}

        public Options(boolean head, boolean tabList) {
            this.head = head;
            this.tabList = tabList;
        }

        public boolean isHead() {
            return this.head;
        }

        public Options withHead(boolean head) {
            this.head = head;
            return this;
        }

        public boolean isTabList() {
            return this.tabList;
        }

        public Options withTabList(boolean tabList) {
            this.tabList = tabList;
            return this;
        }

    }

}
