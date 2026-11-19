package com.jhandev.transpormap.alert.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jhandev.transpormap.alert.GeoPoint;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
class PointRequestTest {
    @Autowired JsonMapper json;
    @Autowired Validator validator;

    private PointRequest read(String source) {
        return json.readValue(source, PointRequest.class);
    }

    private Set<String> invalidFields(PointRequest point) {
        return validator.validate(point).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void absentAndNullCoordinatesAreInvalidAndNeverConvertedToZero() {
        for (String source : new String[] {
                "{}", "{\"latitude\":null,\"longitude\":0}",
                "{\"latitude\":0,\"longitude\":null}", "{\"latitude\":0}", "{\"longitude\":0}"
        }) {
            PointRequest point = read(source);
            assertFalse(invalidFields(point).isEmpty(), source);
            assertThrows(IllegalArgumentException.class, point::toDomain, source);
        }
    }

    @Test
    void correctCoordinatesPreserveOrderAndAllowActualZero() {
        PointRequest colombia = read("{\"latitude\":10.99,\"longitude\":-74.8}");
        assertTrue(invalidFields(colombia).isEmpty());
        assertEquals(new GeoPoint(10.99, -74.8), colombia.toDomain());
        PointRequest zero = read("{\"latitude\":0,\"longitude\":0}");
        assertTrue(invalidFields(zero).isEmpty());
        assertEquals(new GeoPoint(0, 0), zero.toDomain());
    }

    @Test
    void rejectsCoordinatesOutsideWgs84Limits() {
        assertEquals(Set.of("latitude"), invalidFields(read("{\"latitude\":90.1,\"longitude\":0}")));
        assertEquals(Set.of("longitude"), invalidFields(read("{\"latitude\":0,\"longitude\":-180.1}")));
        assertThrows(IllegalArgumentException.class,
                () -> read("{\"latitude\":0,\"longitude\":181}").toDomain());
    }

    @Test
    void validatorAndDomainRejectAllNonFiniteCoordinates() {
        for (double value : new double[] {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            for (PointRequest point : new PointRequest[] {new PointRequest(value, 0d), new PointRequest(0d, value)}) {
                assertFalse(invalidFields(point).isEmpty(), "validator must reject " + point);
                assertThrows(IllegalArgumentException.class, point::toDomain);
            }
        }
    }
}
