package fr.hyriode.hyrame.utils;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by AstFaster
 * on 28/05/2022 at 13:41
 */
public class DurationFormatter {

    protected boolean days = false;
    protected boolean hours = true;
    protected boolean minutes = true;
    protected boolean seconds = true;

    public DurationFormatter() {}

    public DurationFormatter(boolean days, boolean hours, boolean minutes, boolean seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public boolean isDays() {
        return this.days;
    }

    public DurationFormatter withDays(boolean days) {
        this.days = days;
        return this;
    }

    public boolean isHours() {
        return this.hours;
    }

    public DurationFormatter withHours(boolean hours) {
        this.hours = hours;
        return this;
    }

    public boolean isMinutes() {
        return this.minutes;
    }

    public DurationFormatter withMinutes(boolean minutes) {
        this.minutes = minutes;
        return this;
    }

    public boolean isSeconds() {
        return this.seconds;
    }

    public DurationFormatter withSeconds(boolean seconds) {
        this.seconds = seconds;
        return this;
    }

    public String format(HyriLanguage language, long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = -1;
        long hours = -1;
        long minutes = -1;
        long seconds = -1;

        if (this.days) {
            days = TimeUnit.MILLISECONDS.toDays(millis);

            millis -= TimeUnit.DAYS.toMillis(days);
        }
        
        if (this.hours) {
            hours = TimeUnit.MILLISECONDS.toHours(millis);

            millis -= TimeUnit.HOURS.toMillis(hours);
        }

        if (this.minutes) {
            minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

            millis -= TimeUnit.MINUTES.toMillis(minutes);
        }

        if (this.seconds) {
            seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        }
        return this.format(language, days, hours, minutes, seconds);
    }

    public String format(Player player, long millis) {
        return this.format(IHyriPlayer.get(player.getUniqueId()).getSettings().getLanguage(), millis);
    }

    public String format(HyriLanguage language, long days, long hours, long minutes, long seconds) {
        final StringBuilder result = new StringBuilder();

        if (this.days && days > 0) {
            result.append(HyriLanguageMessage.get("format.duration.days").getValue(language).replace("%days%", String.valueOf(days))).append(" ");
        }

        if (this.hours && hours > 0) {
            result.append(HyriLanguageMessage.get("format.duration.hours").getValue(language).replace("%hours%", String.valueOf(hours))).append(" ");
        }

        if (this.minutes && minutes > 0) {
            result.append(HyriLanguageMessage.get("format.duration.minutes").getValue(language).replace("%minutes%", String.valueOf(minutes))).append(" ");
        }

        if (this.seconds && seconds > 0) {
            result.append(HyriLanguageMessage.get("format.duration.seconds").getValue(language).replace("%seconds%", String.valueOf(seconds)));
        }
        return result.toString();
    }

    public String format(Player player, long days, long hours, long minutes, long seconds) {
        return this.format(IHyriPlayer.get(player.getUniqueId()).getSettings().getLanguage(), days, hours, minutes, seconds);
    }

}
