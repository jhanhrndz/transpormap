package com.jhandev.transpormap.alert.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import org.hibernate.validator.constraints.CodePointLength;

public record CreateAlertRequest(
        @NotNull UUID clientReportId,
        @NotNull @CodePointLength(min = 5, max = 100) String title,
        @NotNull @CodePointLength(min = 10, max = 500) String description,
        @NotNull @Pattern(regexp = "poor_road|full_closure|partial_closure|accident|landslide_flood|roadworks|freight_restriction") String category,
        @NotNull @Pattern(regexp = "open|partial|closed|unknown") String impact,
        @NotNull @Pattern(regexp = "low|medium|high|unknown") String severity,
        @NotNull @Valid PointRequest location,
        @NotBlank String observedAt,
        @NotNull @Pattern(regexp = "none|pending") String photoIntent,
        String roadName,
        @Valid PointRequest segmentStart,
        @Valid PointRequest segmentEnd) {
}
