package com.jhandev.transpormap.alert.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
class CreateAlertRequestTest {
    @Autowired JsonMapper json;
    @Autowired Validator validator;

    private String validJson() {
        return """
            {"clientReportId":"4c5208fa-1cbd-4fa7-aec9-2c306c408f78",
             "title":"Cierre de vía", "description":"Cierre total por obras.",
             "category":"full_closure", "impact":"closed", "severity":"high",
             "location":{"latitude":10.99,"longitude":-74.8},
             "observedAt":"2026-01-02T09:30:00-05:00", "photoIntent":"none"}
            """;
    }

    private CreateAlertRequest read(String value) {
        return json.readValue(value, CreateAlertRequest.class);
    }

    private Set<String> invalidFields(CreateAlertRequest request) {
        return validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void deserializesValidRequestWithoutChangingObservedTime() {
        CreateAlertRequest request = read(validJson());
        assertTrue(invalidFields(request).isEmpty());
        assertEquals("2026-01-02T09:30:00-05:00", request.observedAt());
        assertEquals(10.99, request.location().latitude());
    }

    @Test
    void requiresFieldsAndValidatesNestedCoordinates() {
        Set<String> missing = invalidFields(read("{}"));
        assertTrue(missing.containsAll(Set.of("clientReportId", "title", "description", "category",
                "impact", "severity", "location", "observedAt", "photoIntent")));
        assertTrue(invalidFields(read(validJson().replace("\"latitude\":10.99,", "")))
                .contains("location.latitude"));
        assertTrue(invalidFields(read(validJson().replace("\"longitude\":-74.8", "\"longitude\":181")))
                .contains("location.longitude"));
    }

    @Test
    void rejectsUnknownEnumeratedValues() {
        assertTrue(invalidFields(read(validJson().replace("full_closure", "FULL_CLOSURE"))).contains("category"));
        assertTrue(invalidFields(read(validJson().replace("\"closed\"", "\"blocked\""))).contains("impact"));
        assertTrue(invalidFields(read(validJson().replace("\"high\"", "\"critical\""))).contains("severity"));
        assertTrue(invalidFields(read(validJson().replace("\"none\"", "\"uploaded\""))).contains("photoIntent"));
    }

    @Test
    void acceptsEveryCategoryExposedByDomainMapper() {
        for (var category : com.jhandev.transpormap.alert.AlertCategory.values()) {
            String wireValue = AlertCategoryMapper.toApi(category);
            assertFalse(invalidFields(read(validJson().replace("full_closure", wireValue))).contains("category"));
        }
    }

    @Test
    void textLimitsCountUnicodeCodePoints() {
        String maximum = "😀".repeat(100);
        String valid = validJson().replace("Cierre de vía", maximum);
        assertFalse(invalidFields(read(valid)).contains("title"));
        assertTrue(invalidFields(read(valid.replace(maximum, maximum + "😀"))).contains("title"));
        assertTrue(invalidFields(read(validJson().replace("Cierre de vía", "1234"))).contains("title"));
        assertTrue(invalidFields(read(validJson().replace("Cierre total por obras.", "😀".repeat(9))))
                .contains("description"));
        assertFalse(invalidFields(read(validJson().replace("Cierre total por obras.", "😀".repeat(500))))
                .contains("description"));
        assertTrue(invalidFields(read(validJson().replace("Cierre total por obras.", "😀".repeat(501))))
                .contains("description"));
    }

    @Test
    void optionalSegmentPointsAreValidatedWhenProvided() {
        String withSegment = validJson().replace("\"photoIntent\":\"none\"",
                "\"photoIntent\":\"pending\", \"segmentStart\":{\"latitude\":null,\"longitude\":0}");
        assertTrue(invalidFields(read(withSegment)).contains("segmentStart.latitude"));
    }

    @Test
    void validatesOptionalSegmentEndWhenProvided() {
        String missingLongitude = validJson().replace("\"photoIntent\":\"none\"",
                "\"photoIntent\":\"none\", \"segmentEnd\":{\"latitude\":10}");
        String invalidLatitude = validJson().replace("\"photoIntent\":\"none\"",
                "\"photoIntent\":\"none\", \"segmentEnd\":{\"latitude\":91,\"longitude\":-74}");
        assertTrue(invalidFields(read(missingLongitude)).contains("segmentEnd.longitude"));
        assertTrue(invalidFields(read(invalidLatitude)).contains("segmentEnd.latitude"));
    }
}
