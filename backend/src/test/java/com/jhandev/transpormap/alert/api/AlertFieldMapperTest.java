package com.jhandev.transpormap.alert.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhandev.transpormap.alert.PhotoIntent;
import com.jhandev.transpormap.alert.RoadImpact;
import com.jhandev.transpormap.alert.Severity;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AlertFieldMapperTest {
    @Test
    void mapsAllContractRoadImpacts() {
        Map<String, RoadImpact> expected = Map.of("open", RoadImpact.OPEN, "partial", RoadImpact.PARTIAL,
                "closed", RoadImpact.CLOSED, "unknown", RoadImpact.UNKNOWN);
        assertEquals(RoadImpact.values().length, expected.size());
        expected.forEach((wire, value) -> assertEquals(value, AlertFieldMapper.roadImpact(wire)));
    }

    @Test
    void mapsAllContractSeverities() {
        Map<String, Severity> expected = Map.of("low", Severity.LOW, "medium", Severity.MEDIUM,
                "high", Severity.HIGH, "unknown", Severity.UNKNOWN);
        assertEquals(Severity.values().length, expected.size());
        expected.forEach((wire, value) -> assertEquals(value, AlertFieldMapper.severity(wire)));
    }

    @Test
    void mapsAllContractPhotoIntents() {
        Map<String, PhotoIntent> expected = Map.of("none", PhotoIntent.NONE, "pending", PhotoIntent.PENDING);
        assertEquals(PhotoIntent.values().length, expected.size());
        expected.forEach((wire, value) -> assertEquals(value, AlertFieldMapper.photoIntent(wire)));
    }

    @Test
    void rejectsMissingAndUnknownValuesRatherThanNormalizing() {
        for (String value : new String[] {null, "", "UNKNOWN", " unknown", "other"}) {
            assertThrows(IllegalArgumentException.class, () -> AlertFieldMapper.roadImpact(value));
            assertThrows(IllegalArgumentException.class, () -> AlertFieldMapper.severity(value));
            assertThrows(IllegalArgumentException.class, () -> AlertFieldMapper.photoIntent(value));
        }
    }
}
