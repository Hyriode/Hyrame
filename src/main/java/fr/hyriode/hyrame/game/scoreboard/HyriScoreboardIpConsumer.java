package fr.hyriode.hyrame.game.scoreboard;

import fr.hyriode.hyrame.utils.References;
import fr.hyriode.hyrame.scoreboard.HyriScoreboardLine;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 17/09/2021 at 11:39
 */
public class HyriScoreboardIpConsumer implements Consumer<HyriScoreboardLine> {

    private int count = 0;

    private int charIndex = 0;

    private final String ip;

    public HyriScoreboardIpConsumer(String ip) {
        this.ip = ip;
    }

    @Override
    public void accept(HyriScoreboardLine scoreboardLine) {
        if (this.count >= 20) {
            if (this.charIndex > this.ip.length()) {
                this.count = 0;
                this.charIndex = 0;
                return;
            }

            if (this.charIndex == 0) {
                scoreboardLine.setValue(ChatColor.AQUA + this.ip.substring(0, 1) + ChatColor.DARK_AQUA + this.ip.substring(1));
            } else if (this.charIndex == this.ip.length()) {
                scoreboardLine.setValue(ChatColor.DARK_AQUA + References.SERVER_IP);
            } else {
                final String start = this.ip.substring(0, this.charIndex);
                final String character = this.ip.substring(this.charIndex, this.charIndex + 1);
                final String end = this.ip.substring(this.charIndex + 1);

                scoreboardLine.setValue(ChatColor.DARK_AQUA + start + ChatColor.AQUA + character + ChatColor.DARK_AQUA + end);
            }
            this.charIndex++;
        }
        this.count++;
    }

}
