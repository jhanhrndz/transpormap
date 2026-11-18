package com.jhandev.transpormap.alert;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class AlertTimeRules {
    private static final Duration MAX_CLOCK_SKEW = Duration.ofMinutes(5);

    private AlertTimeRules() {}

    public static Instant validateObservedAt(Instant observedAt, Clock clock) {
        Objects.requireNonNull(observedAt, "observedAt");
        Objects.requireNonNull(clock, "clock");
        if (observedAt.isAfter(clock.instant().plus(MAX_CLOCK_SKEW))) {
            throw new IllegalArgumentException("La hora observada supera el límite permitido en el futuro.");
        }
        return observedAt;
    }

    public static Instant expiresAt(AlertCategory category, Instant observedAt) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(observedAt, "observedAt");
        Duration validity = switch (category) {
            case ACCIDENT, LANDSLIDE_FLOOD, FULL_CLOSURE, PARTIAL_CLOSURE -> Duration.ofHours(24);
            case ROADWORKS, FREIGHT_RESTRICTION, POOR_ROAD -> Duration.ofHours(72);
        };
        return observedAt.plus(validity);
    }

    public static boolean isExpired(Instant expiresAt, Instant currentTime) {
        Objects.requireNonNull(expiresAt, "expiresAt");
        Objects.requireNonNull(currentTime, "currentTime");
        return !currentTime.isBefore(expiresAt);
    }
}
