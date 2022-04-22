package fr.hyriode.hyrame.utils;

import java.time.Duration;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 16:10
 */
public class DurationConverter {

    private long seconds;

    public DurationConverter(Duration duration) {
        this.seconds = duration.getSeconds();
    }

    public long toMonthsPart() {
        return this.seconds  / 86400 / 30;
    }

    public long toDaysPart() {
        return this.seconds / 86400;
    }

    public int toHoursPart() {
        return (int) ((this.seconds / (60 * 60)) % 24);
    }

    public int toMinutesPart() {
        return (int) ((this.seconds / 60) % 60);
    }

    public int toSecondsPart() {
        return (int) (this.seconds % 60);
    }

    public int toSeconds() {
        return (int) this.seconds;
    }

    public void substract(int seconds) {
        this.seconds -= seconds;
    }
}
