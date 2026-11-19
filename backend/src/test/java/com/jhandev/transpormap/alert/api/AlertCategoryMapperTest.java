package com.jhandev.transpormap.alert.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhandev.transpormap.alert.AlertCategory;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AlertCategoryMapperTest {
    private static final Map<String, AlertCategory> CONTRACT_CATEGORIES = Map.of(
            "poor_road", AlertCategory.POOR_ROAD,
            "full_closure", AlertCategory.FULL_CLOSURE,
            "partial_closure", AlertCategory.PARTIAL_CLOSURE,
            "accident", AlertCategory.ACCIDENT,
            "landslide_flood", AlertCategory.LANDSLIDE_FLOOD,
            "roadworks", AlertCategory.ROADWORKS,
            "freight_restriction", AlertCategory.FREIGHT_RESTRICTION);

    @Test
    void mapsEveryContractCategoryInBothDirections() {
        assertEquals(AlertCategory.values().length, CONTRACT_CATEGORIES.size());
        CONTRACT_CATEGORIES.forEach((wire, category) -> {
            assertEquals(category, AlertCategoryMapper.fromApi(wire));
            assertEquals(wire, AlertCategoryMapper.toApi(category));
        });
    }

    @Test
    void rejectsUnknownOrMissingContractCategoriesWithoutNormalizing() {
        for (String invalid : new String[] {null, "", "accidents", "ACCIDENT", " accident"}) {
            assertThrows(IllegalArgumentException.class, () -> AlertCategoryMapper.fromApi(invalid));
        }
    }

    @Test
    void rejectsMissingDomainCategory() {
        assertThrows(IllegalArgumentException.class, () -> AlertCategoryMapper.toApi(null));
    }
}
