package com.jhandev.transpormap.alert.api;

import com.jhandev.transpormap.alert.PhotoIntent;
import com.jhandev.transpormap.alert.RoadImpact;
import com.jhandev.transpormap.alert.Severity;

public final class AlertFieldMapper {
    private AlertFieldMapper() {}

    public static RoadImpact roadImpact(String value) {
        if (value == null) throw new IllegalArgumentException("El impacto es obligatorio.");
        return switch (value) {
            case "open" -> RoadImpact.OPEN;
            case "partial" -> RoadImpact.PARTIAL;
            case "closed" -> RoadImpact.CLOSED;
            case "unknown" -> RoadImpact.UNKNOWN;
            default -> throw new IllegalArgumentException("El impacto no es válido.");
        };
    }

    public static Severity severity(String value) {
        if (value == null) throw new IllegalArgumentException("La severidad es obligatoria.");
        return switch (value) {
            case "low" -> Severity.LOW;
            case "medium" -> Severity.MEDIUM;
            case "high" -> Severity.HIGH;
            case "unknown" -> Severity.UNKNOWN;
            default -> throw new IllegalArgumentException("La severidad no es válida.");
        };
    }

    public static PhotoIntent photoIntent(String value) {
        if (value == null) throw new IllegalArgumentException("La intención de fotografía es obligatoria.");
        return switch (value) {
            case "none" -> PhotoIntent.NONE;
            case "pending" -> PhotoIntent.PENDING;
            default -> throw new IllegalArgumentException("La intención de fotografía no es válida.");
        };
    }
}
