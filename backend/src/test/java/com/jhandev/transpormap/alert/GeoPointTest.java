package com.jhandev.transpormap.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GeoPointTest {
    @Test
    void preservesColombianLatitudeAndLongitudeWithoutSwappingAxes() {
        var point = new GeoPoint(10.99, -74.80);
        assertEquals(10.99, point.latitude());
        assertEquals(-74.80, point.longitude());
    }

    @Test
    void acceptsInclusiveWgs84Bounds() {
        new GeoPoint(-90, -180);
        new GeoPoint(90, 180);
    }

    @Test
    void rejectsOutOfRangeLatitudeAndLongitude() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(-90.0001, 0));
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(90.0001, 0));
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0, -180.0001));
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0, 180.0001));
    }

    @Test
    void rejectsNonFiniteValuesForEitherCoordinate() {
        for (double value : new double[] {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new GeoPoint(value, 0));
            assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0, value));
        }
    }
}
