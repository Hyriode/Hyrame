package fr.hyriode.hyrame.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/03/2022 at 18:43
 */
public class TimeUtil {

    /** The default pattern to use to format dates */
    public static final String DEFAULT_PATTERN = "dd/MM/yyyy HH:mm";

    /**
     * Format a time to string
     *
     * @param time A time
     * @return A {@link String}
     */
    public static String formatTime(long time) {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(time * 1000);

        return (line.startsWith("00:") ? line.substring(3) : line);
    }

    /**
     * Format the current date to a given pattern in a {@link String}
     *
     * @param pattern The pattern to use when formatting
     * @return A formatted date
     */
    public static String getCurrentFormattedDate(String pattern) {
        final SimpleDateFormat format = new SimpleDateFormat(pattern);

        format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        return format.format(new Date());
    }

    /**
     * Format the current date to a given pattern in a {@link String}
     *
     * @return A formatted date
     */
    public static String getCurrentFormattedDate() {
        return getCurrentFormattedDate(DEFAULT_PATTERN);
    }

    /**
     * Format a given date to the default pattern
     *
     * @param date The date to format
     * @return A formatted date
     */
    public static String formatDate(Date date) {
        final SimpleDateFormat format = new SimpleDateFormat(DEFAULT_PATTERN);

        format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        return format.format(date);
    }


}
