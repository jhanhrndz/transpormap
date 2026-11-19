package com.jhandev.transpormap.alert;

import java.time.Instant;
import java.util.UUID;

public record CreateAlertCommand(
        UUID clientReportId,
        String title,
        String description,
        AlertCategory category,
        RoadImpact impact,
        Severity severity,
        GeoPoint location,
        Instant observedAt,
        PhotoIntent photoIntent,
        String roadName,
        GeoPoint segmentStart,
        GeoPoint segmentEnd) {

    public CreateAlertCommand {
        if (clientReportId == null || title == null || description == null || category == null
                || impact == null || severity == null || location == null || observedAt == null
                || photoIntent == null) {
            throw new IllegalArgumentException("Faltan campos obligatorios de la alerta.");
        }
        int titleLength = title.codePointCount(0, title.length());
        int descriptionLength = description.codePointCount(0, description.length());
        if (titleLength < 5 || titleLength > 100 || descriptionLength < 10 || descriptionLength > 500) {
            throw new IllegalArgumentException("El título o la descripción están fuera de los límites permitidos.");
        }
    }
}
