package com.jhandev.transpormap.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class PostgisSmokeIT {
    // Docker Hub postgis/postgis:17-3.7 multi-platform digest, verified 2026-11-19.
    private static final DockerImageName IMAGE = DockerImageName.parse(
            "postgis/postgis@sha256:b2379049e1d7623127ec61ba802a44706109bf91c23ec76b8f323888fd037ed9")
            .asCompatibleSubstituteFor("postgres");

    @Container
    private static final PostgreSQLContainer DATABASE = new PostgreSQLContainer(IMAGE);

    @Test
    void providesPostgisAndWgs84Geometry() throws Exception {
        try (var connection = DriverManager.getConnection(
                    DATABASE.getJdbcUrl(), DATABASE.getUsername(), DATABASE.getPassword());
             var query = connection.createStatement();
             var result = query.executeQuery("""
                     SELECT PostGIS_Full_Version(), ST_X(point), ST_Y(point), ST_SRID(point)
                     FROM (SELECT ST_SetSRID(ST_MakePoint(-74.80, 10.99), 4326) AS point) p
                     """)) {
            assertTrue(result.next());
            assertTrue(result.getString(1).contains("POSTGIS"));
            assertEquals(-74.80, result.getDouble(2), 0.0000001);
            assertEquals(10.99, result.getDouble(3), 0.0000001);
            assertEquals(4326, result.getInt(4));
        }
    }
}
