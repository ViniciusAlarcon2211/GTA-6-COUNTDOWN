package com.vicountdown.app.countdown;

import java.time.*;

public final class CountdownEngine {
    private CountdownEngine() {}
    // Official launch date verified against Rockstar Games on project creation.
    public static final LocalDate RELEASE_DATE = LocalDate.of(2026, 11, 19);
    public static final LocalDate PROGRESS_START = LocalDate.of(2025, 11, 6);

    public static Snapshot now() {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime current = ZonedDateTime.now(zone);
        ZonedDateTime release = RELEASE_DATE.atStartOfDay(zone);
        long seconds = Math.max(0, Duration.between(current, release).getSeconds());
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return new Snapshot(days, hours, minutes, secs, seconds == 0);
    }

    public static int progress1000() {
        LocalDate today = LocalDate.now();
        long total = Math.max(1, Duration.between(PROGRESS_START.atStartOfDay(), RELEASE_DATE.atStartOfDay()).toDays());
        long done = Duration.between(PROGRESS_START.atStartOfDay(), today.atStartOfDay()).toDays();
        double value = Math.max(0, Math.min(1, done / (double) total));
        return (int)Math.round(value * 1000);
    }

    public record Snapshot(long days, long hours, long minutes, long seconds, boolean launched) {}
}
