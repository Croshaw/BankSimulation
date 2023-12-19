package me.vlad.banksimulation.util;


import java.time.Duration;

public class DurationUtils {
    public static String toStringFormat(String format, Duration duration) {
        boolean needDay = format.contains("D");
        return format
                .replace("DD", "%02d".formatted(duration.toDays()))
                .replace("D", "%s".formatted(duration.toDays()))
                .replace("HH", "%02d".formatted(needDay ? duration.toHoursPart() : duration.toHours()))
                .replace("H", "%s".formatted(needDay ? duration.toHoursPart() : duration.toHours()))
                .replace("mm", "%02d".formatted(duration.toMinutesPart()))
                .replace("m", "%s".formatted(duration.toMinutesPart()))
                .replace("ss", "%02d".formatted(duration.toSecondsPart()))
                .replace("s", "%s".formatted(duration.toSecondsPart()));

    }
    public static String toString(Duration duration) {
        return toStringFormat("HH:mm:ss", duration);
    }
}
