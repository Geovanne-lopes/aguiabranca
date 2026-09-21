package br.com.fiap.aguiabranca.dashboard.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SaoPauloCalendar {

    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private SaoPauloCalendar() {
    }

    public static ZonedDateTime now(Clock clock) {
        return ZonedDateTime.now(clock.withZone(ZONE));
    }

    public static Instant startOfMonth(Clock clock) {
        return startOfMonth(now(clock));
    }

    public static Instant startOfNextMonth(Clock clock) {
        return startOfMonth(now(clock).plusMonths(1));
    }

    public static Instant startOfPreviousMonth(Clock clock) {
        return startOfMonth(now(clock).minusMonths(1));
    }

    public static boolean inMonth(Instant instant, Instant startInclusive, Instant endExclusive) {
        return instant != null && !instant.isBefore(startInclusive) && instant.isBefore(endExclusive);
    }

    public static int countInMonth(List<Instant> instants, Instant startInclusive, Instant endExclusive) {
        int count = 0;
        if (instants == null) {
            return 0;
        }
        for (Instant instant : instants) {
            if (inMonth(instant, startInclusive, endExclusive)) {
                count++;
            }
        }
        return count;
    }

    public static List<MonthBar> lastMonths(List<Instant> createdAts, Clock clock, int months) {
        ZonedDateTime now = now(clock);
        List<MonthBar> bars = new ArrayList<>(months);
        for (int offset = months - 1; offset >= 0; offset--) {
            ZonedDateTime month = now.minusMonths(offset);
            Instant start = startOfMonth(month);
            Instant end = startOfMonth(month.plusMonths(1));
            bars.add(new MonthBar(label(YearMonth.from(month)), countInMonth(createdAts, start, end)));
        }
        return List.copyOf(bars);
    }

    public static String label(YearMonth month) {
        String raw = month.getMonth().getDisplayName(TextStyle.SHORT, PT_BR);
        String letters = raw.replaceAll("[^\\p{L}]", "");
        if (letters.isEmpty()) {
            return raw;
        }
        String lower = letters.toLowerCase(PT_BR);
        return lower.substring(0, 1).toUpperCase(PT_BR) + lower.substring(1);
    }

    private static Instant startOfMonth(ZonedDateTime month) {
        return month.withDayOfMonth(1).toLocalDate().atStartOfDay(ZONE).toInstant();
    }
}
