package com.jhandev.transpormap.alert.api;

import com.jhandev.transpormap.alert.AlertCategory;

public final class AlertCategoryMapper {
    private AlertCategoryMapper() {}

    public static AlertCategory fromApi(String value) {
        if (value == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        return switch (value) {
            case "poor_road" -> AlertCategory.POOR_ROAD;
            case "full_closure" -> AlertCategory.FULL_CLOSURE;
            case "partial_closure" -> AlertCategory.PARTIAL_CLOSURE;
            case "accident" -> AlertCategory.ACCIDENT;
            case "landslide_flood" -> AlertCategory.LANDSLIDE_FLOOD;
            case "roadworks" -> AlertCategory.ROADWORKS;
            case "freight_restriction" -> AlertCategory.FREIGHT_RESTRICTION;
            default -> throw new IllegalArgumentException("La categoría no es válida.");
        };
    }

    public static String toApi(AlertCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        return switch (category) {
            case POOR_ROAD -> "poor_road";
            case FULL_CLOSURE -> "full_closure";
            case PARTIAL_CLOSURE -> "partial_closure";
            case ACCIDENT -> "accident";
            case LANDSLIDE_FLOOD -> "landslide_flood";
            case ROADWORKS -> "roadworks";
            case FREIGHT_RESTRICTION -> "freight_restriction";
        };
    }
}
