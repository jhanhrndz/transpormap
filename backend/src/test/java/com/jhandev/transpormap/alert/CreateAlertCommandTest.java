package com.jhandev.transpormap.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateAlertCommandTest {
    private static final UUID ID = UUID.fromString("4c5208fa-1cbd-4fa7-aec9-2c306c408f78");
    private static final Instant OBSERVED = Instant.parse("2026-01-02T14:30:00Z");
    private static final GeoPoint POINT = new GeoPoint(10.99, -74.8);

    private CreateAlertCommand command(UUID id, String title, String description, AlertCategory category,
            RoadImpact impact, Severity severity, GeoPoint location, Instant observedAt, PhotoIntent photoIntent) {
        return new CreateAlertCommand(id, title, description, category, impact, severity, location,
                observedAt, photoIntent, null, null, null);
    }

    @Test
    void retainsSubmittedValuesWithoutChangingObservation() {
        var result = command(ID, " Cierre ", " Cierre total ", AlertCategory.FULL_CLOSURE,
                RoadImpact.CLOSED, Severity.HIGH, POINT, OBSERVED, PhotoIntent.NONE);
        assertEquals(" Cierre ", result.title());
        assertEquals(" Cierre total ", result.description());
        assertEquals(POINT, result.location());
        assertEquals(OBSERVED, result.observedAt());
    }

    @Test
    void rejectsEveryMissingRequiredValue() {
        assertThrows(IllegalArgumentException.class, () -> command(null, "abcde", "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, null, "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", null, AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", null, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", AlertCategory.ACCIDENT, null, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, null, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, null, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, null, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "abcde", "0123456789", AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, null));
    }

    @Test
    void enforcesTextLimitsInUnicodeCodePoints() {
        command(ID, "😀".repeat(5), "😀".repeat(10), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.PENDING);
        command(ID, "😀".repeat(100), "😀".repeat(500), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.PENDING);
        assertThrows(IllegalArgumentException.class, () -> command(ID, "😀".repeat(4), "😀".repeat(10), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "😀".repeat(101), "😀".repeat(10), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "😀".repeat(5), "😀".repeat(9), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
        assertThrows(IllegalArgumentException.class, () -> command(ID, "😀".repeat(5), "😀".repeat(501), AlertCategory.ACCIDENT, RoadImpact.OPEN, Severity.LOW, POINT, OBSERVED, PhotoIntent.NONE));
    }

    @Test
    void preservesOptionalRoadAndSegmentWithoutModification() {
        GeoPoint end = new GeoPoint(11.0, -74.7);
        var result = new CreateAlertCommand(ID, "Cierre", "Vía cerrada", AlertCategory.FULL_CLOSURE,
                RoadImpact.PARTIAL, Severity.MEDIUM, POINT, OBSERVED, PhotoIntent.PENDING,
                " Ruta 90 ", POINT, end);
        assertEquals(" Ruta 90 ", result.roadName());
        assertEquals(POINT, result.segmentStart());
        assertEquals(end, result.segmentEnd());
    }
}
