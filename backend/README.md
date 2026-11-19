# Backend de TransporMap

El backend contiene un esqueleto Spring Boot y reglas y DTO parciales; aún no existe un endpoint de creación de alertas.

Antes de conectar `CreateAlertRequest` a un endpoint, se debe acordar e implementar la validación completa de `observedAt` (incluido el perfil temporal compartido con el cliente y el límite de cinco minutos), comprobar el rechazo de propiedades adicionales y coerciones JSON frente a OpenAPI, y aplicar `@Valid` en la entrada HTTP con respuestas de error probadas. El DTO actual solo exige que `observedAt` no esté vacío; no acredita conformidad completa con el contrato.

Los límites de longitud de título y descripción se comprueban temporalmente tanto en el DTO como en `CreateAlertCommand`: protegen la frontera HTTP y las invariantes de dominio, pero duplican sus valores. Al integrar ambos, centralizar la regla compartida sin eliminar ninguna de esas protecciones ni cambiar el cómputo por puntos de código Unicode. El comando recibe un `Instant` ya interpretado; la validación de hora futura con reloj inyectado corresponde al servicio de aplicación.

La comprobación preliminar de PostGIS usa Testcontainers con el perfil `postgis-it`: `./mvnw verify -Ppostgis-it` o, en PowerShell, `.\mvnw.cmd verify -Ppostgis-it`. Requiere un runtime Docker compatible y descarga de la imagen fijada; falla si Docker no está disponible. En este equipo el test se descubrió pero falló por ausencia de Docker: todavía no hay ejecución exitosa, ni evidencia de migraciones, repositorios o distancias. El workflow de GitHub activa el perfil, pero aún no se ha ejecutado remotamente.
