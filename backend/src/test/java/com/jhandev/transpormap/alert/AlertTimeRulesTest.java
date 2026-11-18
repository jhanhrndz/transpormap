package com.jhandev.transpormap.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class AlertTimeRulesTest {
    private static final Instant OBSERVED = Instant.parse("2026-01-31T23:30:00Z");
    private static final Clock CLOCK = Clock.fixed(OBSERVED, ZoneOffset.UTC);

    @Test
    void acuteEventsExpire24HoursAfterObservation() {
        for (var category : new AlertCategory[] {AlertCategory.ACCIDENT, AlertCategory.LANDSLIDE_FLOOD,
                AlertCategory.FULL_CLOSURE, AlertCategory.PARTIAL_CLOSURE}) {
            assertEquals(OBSERVED.plus(Duration.ofHours(24)), AlertTimeRules.expiresAt(category, OBSERVED));
        }
    }

    @Test
    void persistentConditionsExpire72HoursAfterObservation() {
        for (var category : new AlertCategory[] {AlertCategory.ROADWORKS, AlertCategory.FREIGHT_RESTRICTION,
                AlertCategory.POOR_ROAD}) {
            assertEquals(OBSERVED.plus(Duration.ofHours(72)), AlertTimeRules.expiresAt(category, OBSERVED));
        }
    }

    @Test
    void acceptsObservationExactlyFiveMinutesAheadWithoutChangingIt() {
        Instant observedAt = OBSERVED.plus(Duration.ofMinutes(5));
        assertEquals(observedAt, AlertTimeRules.validateObservedAt(observedAt, CLOCK));
    }

    @Test
    void rejectsObservationMoreThanFiveMinutesAhead() {
        assertThrows(IllegalArgumentException.class,
                () -> AlertTimeRules.validateObservedAt(OBSERVED.plus(Duration.ofMinutes(5)).plusNanos(1), CLOCK));
    }

    @Test
    void lateReportRetainsObservationAndIsExpiredAtDeadline() {
        Instant receivedAt = OBSERVED.plus(Duration.ofHours(24));
        assertEquals(OBSERVED, AlertTimeRules.validateObservedAt(OBSERVED, Clock.fixed(receivedAt, ZoneOffset.UTC)));
        Instant expiresAt = AlertTimeRules.expiresAt(AlertCategory.ACCIDENT, OBSERVED);
        assertFalse(AlertTimeRules.isExpired(expiresAt, receivedAt.minusNanos(1)));
        assertTrue(AlertTimeRules.isExpired(expiresAt, receivedAt));
    }
}
