package com.jhandev.transpormap.alert.api;

import com.jhandev.transpormap.alert.GeoPoint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PointRequest(
        @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude) {

    public GeoPoint toDomain() {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("La latitud y la longitud son obligatorias.");
        }
        return new GeoPoint(latitude, longitude);
    }
}
