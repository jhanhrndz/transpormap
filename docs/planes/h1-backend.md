# TransporMap — Plan H1: backend del esqueleto andante

> Para quien ejecute: usar revisión independiente por tarea y antes del merge. Este archivo es un plan: existe un esqueleto backend y pruebas locales, pero H1 no está terminado. La aprobación de Fase 4 autoriza el trabajo técnico, pero no elimina las puertas de H0, no acredita proveedores ni autoriza gastos. Los detalles propuestos para revisión están en [arquitectura backend](../06-arquitectura-detallada-backend.md).

**Objetivo:** demostrar en el backend el flujo autenticado mínimo de publicar una alerta sintética y leer la misma alerta para mapa y lista, preservando las garantías de privacidad acordadas.

**Arquitectura:** monolito modular Spring Boot, API `/v1`, PostgreSQL/PostGIS y un proveedor de identidad delegado. Spring verifica el token y el estado local de la cuenta en cada petición protegida; el cliente nunca obtiene acceso directo a tablas. La identidad externa autentica, pero la autorización de cuenta activa y rol pertenece al dominio de TransporMap.

**Tecnologías:** Java y Spring Boot; Maven wrapper; PostgreSQL/PostGIS; migraciones versionadas; pruebas con BD PostGIS real en Testcontainers; OpenAPI 3.1 validado estructuralmente. El esqueleto existente fija Java 25 y Spring Boot 4.1.2 tras consultar Spring Initializr; resta verificar compatibilidad con hosting y gates H0. PostgreSQL/PostGIS y plugins siguen por fijar. JWT Bearer representa el candidato de contrato; no está aprobado ni demostrado un proveedor concreto. No escribir `latest` como versión.

**Especificaciones:** [requisitos aprobados](../01-requisitos.md), [arquitectura aprobada condicionalmente](../02-stack-y-arquitectura.md), [roadmap H0/H1](../03-plan-y-roadmap.md), [OpenAPI propuesto](../api/openapi-propuesta.yaml), [notas de sus límites](../api/notas-contrato-h0.md), [trazabilidad del legado](../descubrimiento/04-trazabilidad-legado.md), [preflight de proveedores](../operacion/00-preflight-demo.md) y [plan de pruebas](../04-plan-pruebas.md).

## Restricciones y puertas globales

- Este plan detalla **solo backend H1**. H1 completo exige además PWA, mapa/lista, CI/CD, revisión independiente y demo desplegada; un backend local o un proveedor simulado no son H1 terminado.
- Mantener intactos `src/`, `nbproject/`, JAR e historial del legado durante el arranque. No trasladar credenciales, contraseñas, licencias embebidas o assets sin verificar su procedencia. No existen filas Oracle que migrar.
- No exponer contenido de alertas a visitantes: `GET /v1/health` es la única operación pública del subconjunto. `GET /v1/me`, `GET /v1/alerts`, `GET /v1/alerts/{alertId}` y `POST /v1/alerts` requieren cuenta activa autenticada. Las rutas de registro, recuperación y login del proveedor externo no se inventan en esta API.
- Mantener `clientReportId` estable, ligado al autor, e `Idempotency-Key` del contrato. El reintento idéntico debe devolver la misma identidad de alerta mientras exista evidencia y autorización; contenido distinto con la misma identidad produce conflicto. El propietario decidió que, si tras purgar datos no puede saberse si se publicó, se conserva borrador y foto, no se reenvía automáticamente y se pide revisión manual. No es necesario mantener una marca perpetua; identificar de forma segura cuándo existe incertidumbre sigue siendo una puerta H0/H5.
- Registrar hora observada y recibida por separado; rechazar una observación más de cinco minutos futura con error corregible. La vigencia 24/72 h está aprobada; H1 debe guardar y devolver `expiresAt` y `status` coherentes con ese cómputo. No corregir la hora silenciosamente.
- No añadir un endpoint público alternativo, usuario demo privilegiado, token embebido, contraseña estática o JWT con firma desactivada para superar una prueba o una limitación del proveedor. Los datos sintéticos deben identificarse como tales en documentación/demo.
- No activar proveedor, correo, cuenta, tarjeta, despliegue remoto o servicio con posibles cargos hasta que H0 verifique su plan y control de gasto; si falla, avanzar con la parte local independiente y mantener el hito remoto abierto.
- En el momento de esta planificación no se ha detectado Docker ni WSL instalado. Las pruebas Testcontainers son obligatorias y se consideran **bloqueadas**, no aprobadas ni reemplazadas por H2, hasta contar con un runtime compatible. No instalar componentes de sistema o reiniciar la máquina sin los permisos correspondientes.
- El contrato es un diseño estructuralmente comprobado con Redocly `minimal`; identidad concreta, semántica de deduplicación tardía y varias operaciones aún precisan decisiones o pruebas reales. Cada PR incluirá sus límites explícitos.

## Límites del incremento H1

Incluido: salud mínima, verificación de identidad y perfil, creación y lectura de alerta, persistencia de punto, atributos necesarios del contrato, autorización por servidor, categorías acordadas, idempotencia dentro del ciclo demostrado y caso completo autenticado. El código debe permitir que el frontend use la misma respuesta para lista y marcador.

Posterior en H2–H7: envío/caché offline y snapshots, confirmaciones, carga o lectura de foto, moderación, métricas detalladas, rutas A–B, proximidad geográfica, expiración/retención completas y tratamiento de reintentos con datos ya purgados. En H1 se calculan los campos `expiresAt` y `status` del contrato, pero no se presume que la limpieza, los cambios de visibilidad, la programación o el histórico estén terminados. `photoIntent` se acepta como `none` o `pending`; no se afirma que la carga de fotos exista.

No se cierra la historia de identidad completa hasta demostrar registro, verificación de correo y recuperación para usuarios externos en H4. Para la demo H1, autenticación real significa un token emitido por el proveedor candidato **probado con la cuenta y el coste autorizados**, no un JWT creado en el navegador con secreto compartido. Si el SMTP del proveedor no permite registro de reclutadores, no se presenta registro general como funcional.

## Estructura y convenciones propuestas

Raíz existente `backend/`, paquete `com.jhandev.transpormap`. La tabla muestra la estructura objetivo: algunos archivos ya existen y otros son propuestas para coordinar implementadores. El recurso REST mantiene los nombres del OpenAPI; los nombres de paquetes y clases no cambian el contrato HTTP.

| Ruta futura bajo `backend/` | Responsabilidad |
|---|---|
| `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/` | Build y wrapper reproducibles, versiones explícitas aprobadas y dependencias gestionadas por Spring Boot. |
| `src/main/java/com/jhandev/transpormap/TransporMapApplication.java` | Entrada Boot, sin lógica de dominio. |
| `src/main/java/com/jhandev/transpormap/identity/` | Identidad autenticada, cuenta activa, perfil y adaptación de verificación de tokens. |
| `src/main/java/com/jhandev/transpormap/alert/` | Dominio, casos de uso, DTO, HTTP y puertos de persistencia. |
| `src/main/java/com/jhandev/transpormap/alert/persistence/` | Adaptador PostgreSQL/PostGIS, mapeo de punto y transacción. |
| `src/main/java/com/jhandev/transpormap/shared/` | Errores HTTP `application/problem+json`, reloj inyectable y configuración compartida mínima. |
| `src/main/resources/application.yml` | Configuración por entorno, `issuer`/audiencia y conexión por variables; sin secretos ni credenciales por defecto para perfiles remotos. |
| `src/main/resources/db/migration/V1__h1_foundation.sql` | Migración inicial del esquema H1 y extensión espacial cuando el proveedor lo permita; no es una exportación Oracle. |
| `src/test/java/com/jhandev/transpormap/` | Pruebas unitarias, HTTP y PostgreSQL/PostGIS reales. Los certificados y sujetos de pruebas son sintéticos. |

No duplicar el esquema OpenAPI dentro de DTO sin comprobar correspondencia; las clases H1 cubren los campos de `CreateAlert` y `Alert` presentes en el contrato. Usar `Instant` para momentos, `UUID` para identidades y `BigDecimal` o `double` validado para coordenadas. El significado geográfico es WGS84: latitud de −90 a 90 y longitud de −180 a 180; GeoJSON usa orden longitud/latitud. Las distancias se implementarán en H3 con `geography` en metros, no convirtiendo grados manualmente.

## Contratos internos propuestos

Son firmas candidatas para eliminar ambigüedad entre tareas, no clases ya existentes. Sincronizar cualquier cambio aprobado con el contrato OpenAPI.

```java
record AuthenticatedUser(UUID id, String alias, AccountRole role) {}
enum AccountRole { USER, ADMIN }

record GeoPoint(double latitude, double longitude) {}
enum PhotoIntent { NONE, PENDING }
record CreateAlertCommand(UUID clientReportId, String title, String description,
    AlertCategory category, RoadImpact impact, Severity severity,
    GeoPoint location, Instant observedAt, PhotoIntent photoIntent,
    String roadName, GeoPoint segmentStart, GeoPoint segmentEnd) {}

record AlertView(UUID id, String title, String description, AlertCategory category,
    RoadImpact impact, Severity severity, GeoPoint location, Instant observedAt,
    String roadName, GeoPoint segmentStart, GeoPoint segmentEnd,
    String authorAlias, Instant receivedAt, Instant expiresAt, AlertStatus status,
    PhotoStatus photoStatus, long stillActiveCount, long noLongerActiveCount,
    Instant latestConfirmationAt, String closureReason) {}

interface CurrentUserResolver {
    AuthenticatedUser requireActiveUser(Authentication authentication);
}
interface AccountRepository {
    Optional<Account> findByIssuerAndSubject(String issuer, String subject);
}
interface AccountProvisioningService {
    ProfileView provisionVerifiedUser(VerifiedExternalIdentity identity, String requestedAlias);
}
interface AlertRepository {
    Optional<StoredAlert> findByAuthorAndClientReportId(UUID authorId, UUID clientReportId);
    StoredAlert saveNew(StoredAlert alert);
    Optional<StoredAlert> findVisibleById(UUID id);
    StoredAlertPage findVisiblePage(AlertQuery query);
}
interface AlertApplicationService {
    CreateAlertResult create(AuthenticatedUser user, String idempotencyKey,
        CreateAlertCommand command);
    Optional<AlertView> findVisible(AuthenticatedUser user, UUID alertId);
    AlertPage listVisible(AuthenticatedUser user, AlertQuery query);
}
```

`StoredAlert`, `StoredAlertPage`, `AlertCategory`, `RoadImpact`, `Severity`, `AlertStatus`, `PhotoStatus`, `AlertQuery`, `AlertPage` y `CreateAlertResult` serán tipos explícitos en el módulo `alert`; deben definir todos los valores del esquema OpenAPI y distinguir creación nueva de reintento. `StoredAlertPage` incluye elementos y cursor siguiente; `AlertQuery` representa los filtros, límite y cursor aceptados por OpenAPI. H1 debe implementar los filtros declarados o revisar el contrato del incremento y definir el error de filtro no disponible antes del merge; jamás ignorarlos y devolver un conjunto engañoso. `roadName`, extremos, última confirmación y causa de cierre pueden ser nulos. `clientReportId` e identidad externa nunca se incluyen en `AlertView`. `Idempotency-Key` y contenido normalizado se vinculan al mismo usuario; se generan y guardan de forma transaccional con la alerta. La interfaz concreta del proveedor/decoder no se decide hasta resolver H0; `CurrentUserResolver` no debe aceptar identidades no verificadas.

## Enfoque de revisión y pruebas que lo cubren

1. **JWT válido pero cuenta suspendida:** ninguna ruta protegida devuelve alertas ni acepta publicación; pruebas con rutas fixture `SecurityFixtureIT.suspendedAccountIsForbidden` (tarea 2) y endpoints reales `AlertAuthorizationIT.suspendedAccountCannotReadOrCreate` (tarea 4).
2. **Sin sesión o token incorrecto:** rutas fixture responden 401 sin datos; luego `GET /v1/alerts` y `POST /v1/alerts` reales responden 401 sin contenido de alertas; pruebas `SecurityFixtureIT.unauthenticatedRequestsAreRejected` (tarea 2) y `AlertAuthorizationIT.unauthenticatedRequestsAreRejected` (tarea 4).
3. **Pérdida de respuesta tras commit:** reenviar mismo autor, ID, clave y cuerpo devuelve el ID original sin crear otra fila; prueba `CreateAlertPostgisIT.identicalRetryDoesNotDuplicate` (tarea 4).
4. **Latitud/longitud invertidas:** un punto de prueba colombiano vuelve con los mismos valores y se almacena en WGS84; prueba `CreateAlertPostgisIT.roundTripsColombianPoint` (tarea 3).
5. **Hora de dispositivo adelantada:** `observedAt` mayor por 5 min + 1 s a un reloj fijo genera 400, sin fila; exactamente +5 min no se rechaza por esta regla; pruebas `AlertTimeRulesTest` y `CreateAlertPostgisIT` (tareas 3–4). El cliente conserva su borrador: ese comportamiento se comprueba en H5.

## Tarea 0 — Preparar build y límites de identidad

**Archivos:** ya existen `backend/pom.xml`, wrapper Maven y `backend/src/main/java/com/jhandev/transpormap/TransporMapApplication.java`; faltan persistencia, migraciones y pruebas de integración. Actualizar documentación/ADR afectados junto a los cambios.

**Consume:** metadata oficial de Spring Initializr y puertas H0. **Produce:** build reproducible que ejecuta una prueba de arranque sin conectarse a servicios remotos; parámetros de `issuer`, audiencia y BD declarados sin valores reales.

- [ ] Resolver versiones estables explícitas mediante metadata de Spring Initializr y documentar fecha, JDK y compatibilidad. El legado usa Java 19 pero no es un proyecto Spring: preservar su configuración, sin modificarla como efecto del nuevo backend.
- [ ] Fijar y registrar el contrato verificable para identidad: proveedor candidato, mecanismo de clave pública, `issuer`, audiencia y traducción de `sub` a cuenta local; si no están disponibles, implementar únicamente el puerto y pruebas de comportamiento con identidad criptográficamente controlada por pruebas. No hacer pasar esa prueba por login real.
- [ ] Crear el proyecto Spring con wrapper y dependencias estrictamente necesarias: web, validación, seguridad/resource server, persistencia PostgreSQL, migraciones, health y testing. El scaffolding generado se revisa; las reglas y comportamiento propios se implementan TDD en las tareas siguientes.
- [ ] Comprobar que el proyecto arranca en configuración aislada sin credenciales ni red saliente y que `GET /v1/health` no revela configuración. No exponer Actuator completo o detalles internos por defecto.
- [ ] Ejecutar la verificación completa del wrapper y registrar resultado: desde `backend/`, `./mvnw verify` en shell compatible o `.\mvnw.cmd verify` en PowerShell. La suite local `test` ya pasó; `verify` integral y checksum del wrapper siguen pendientes. Si el runtime de pruebas no está listo, registrar bloqueo en lugar de afirmar pase.
- [ ] Abrir PR y obtener revisión independiente; no hacer merge con builds fallidos ni emitir el hito H1 como completo.

## Tarea 1 — Dominio de alerta y reglas temporales

**Archivos previstos:** existen `AlertCategory.java`, `AlertTimeRules.java` y `AlertTimeRulesTest.java` bajo `backend/src/`; los demás tipos y pruebas de esta tarea permanecen pendientes: `RoadImpact`, `Severity`, `AlertStatus`, `PhotoIntent`, `PhotoStatus`, `GeoPoint`, `CreateAlertCommand` y `GeoPointTest`.

**Consume:** valores `Category`, `Point` y `CreateAlert` del contrato; `java.time.Clock`. **Produce:** `AlertTimeRules.validateObservedAt(Instant observedAt, Clock clock)` y `AlertTimeRules.expiresAt(AlertCategory category, Instant observedAt)`, más validación de `GeoPoint`.

- [ ] Escribir primero `AlertTimeRulesTest`: accidente, derrumbe/inundación y cierres vencen a las 24 h; obras, restricción y mal estado a las 72 h; `+5 min` se acepta y `+5 min +1 s` se rechaza sin alterar la entrada. Un evento tardío conserva `observedAt`; comprobar fronteras y cambio de fecha con `Clock.fixed`.
- [ ] Escribir primero `GeoPointTest`: acepta `(10.99, -74.80)` y bordes `±90/±180`; rechaza valores fuera de rango y `NaN`/infinitos. No intercambiar ejes.
- [ ] Ejecutar ambas pruebas esperando fallo por reglas ausentes, no por dependencias o nombres rotos. **Comando previsto:** desde `backend/`, `.\mvnw.cmd "-Dtest=AlertTimeRulesTest,GeoPointTest" test`.
- [ ] Implementar los tipos y las reglas mínimas, conservando los siete valores `poor_road`, `full_closure`, `partial_closure`, `accident`, `landslide_flood`, `roadworks`, `freight_restriction` en la frontera API. La conversión Java↔JSON debe ser explícita y comprobada en tarea 4.
- [ ] Repetir pruebas y ejecutar el build completo aplicable; registrar resultados y revisión independiente.

## Tarea 2 — Identidad delegada y acceso protegido

**Archivos futuros:** `backend/src/main/java/com/jhandev/transpormap/identity/AuthenticatedUser.java`, `AccountRole.java`, `CurrentUserResolver.java`, `AccountRepository.java`, `AccountProvisioningService.java`, `VerifiedExternalIdentity.java`, `SecurityConfiguration.java`, `ProfileController.java`; `backend/src/test/java/com/jhandev/transpormap/identity/SecurityFixtureIT.java`, `ProfileControllerIT.java` y `AccountProvisioningTest.java`; `backend/src/test/java/com/jhandev/transpormap/identity/fixture/TestOnlySecurityFixtureController.java` exclusivamente en fuentes de prueba.

**Consume:** token firmado validado por resource server y `AccountRepository.findByIssuerAndSubject(String issuer, String subject)`. **Produce:** `CurrentUserResolver.requireActiveUser(Authentication)`, `AccountProvisioningService.provisionVerifiedUser(VerifiedExternalIdentity, String)` y `GET /v1/me` con `{id, alias, role}`. Tanto el resolvedor como la provisión consultan por ambos valores de la identidad verificada, nunca por el sujeto solo ni por valores suministrados fuera del token verificado. `VerifiedExternalIdentity` solo puede construirse en el adaptador tras verificar criptográficamente firma, emisor, audiencia, vencimiento y sujeto; el correo solo se considera confirmado con evidencia verificable del proveedor. La cuenta contiene ID interno, combinación única `(issuer, subject)`, alias, estado y rol; nunca usa correo como clave de enlace ni acepta privilegios administrativos del cliente o claims arbitrarios.

**Provisión mínima H1:** tras autenticación externa verificada, si no existe cuenta, solicitar alias de forma autenticada y crear una cuenta de dominio `ACTIVE/USER` de forma transaccional e idempotente por `(issuer, subject)`; si ya existe, devolver la cuenta existente sin modificar rol, estado ni alias por claims, incluso bajo peticiones concurrentes. Una identidad sin confirmación de correo verificable, un alias inválido o una cuenta suspendida no se activa ni se vincula por coincidencia de correo. El administrador se establece únicamente mediante procedimiento administrativo separado, autorizado y auditado; nunca por autorregistro. El mecanismo HTTP de alta de perfil y los códigos para cuenta aún sin perfil se incorporarán al OpenAPI antes de implementar, sin atribuir al proveedor externo endpoints no acordados. Esta provisión no cierra registro, verificación y recuperación completos de H4; demo remota solo tras comprobar proveedor/correo y autorización del propietario cuando corresponda.

**Contrato HTTP pendiente:** definir y documentar la operación autenticada de provisión. El OpenAPI ya declara 403 para `/me`, listado, creación y detalle de alertas; queda pendiente validar la coherencia completa del contrato actualizado con todos los casos de autorización. Token ausente, inválido o vencido recibe 401; cuenta autenticada pero suspendida o sin permisos recibe 403; un recurso oculto puede responder 404 para no revelar su existencia. El tratamiento de cuenta sin perfil y la condición de correo verificado requieren reflejarse en el contrato final antes de implementación.

- [ ] Primero escribir `SecurityFixtureIT.unauthenticatedRequestsAreRejected` usando únicamente endpoints de prueba `/__test/protected` y `/__test/admin` declarados por `TestOnlySecurityFixtureController` en `src/test`, no incluidos en artefactos de runtime: token ausente, firma inválida, audiencia incorrecta o token vencido produce 401 sin datos; usuario ordinario contra fixture admin produce 403 aunque manipule claims. `SecurityFixtureIT.suspendedAccountIsForbidden`: token válido más cuenta local suspendida produce 403 sin datos. No crear endpoints de producción ficticios para hacer pasar las pruebas.
- [ ] Primero escribir `AccountProvisioningTest`: primera identidad externa verificada y alias válido crean una cuenta `ACTIVE/USER`; repetición y concurrencia producen una sola cuenta y preservan rol/estado/alias existentes; claims de rol `admin`, otro emisor con igual `sub` y coincidencia de correo no confieren privilegios ni enlazan cuentas; correo no verificado y alias inválido no crean cuenta activa. Verificar que el resolvedor y la provisión llaman `findByIssuerAndSubject` con ambos valores verificados y que un mismo sujeto con otro emisor no devuelve la cuenta original. Los dobles del repositorio de la tarea 2 solo prueban reglas de servicio; la unicidad real se prueba en tarea 3 con PostGIS.
- [ ] Primero escribir `ProfileControllerIT.getMeReturnsOnlyAuthorizedProfile`: un token de prueba firmado con clave de prueba y sujeto conocido obtiene ID, alias y rol propios; no se exponen correo ajeno, secreto ni sujeto externo. `/v1/health` responde sin token y no incluye detalles internos.
- [ ] Ejecutar tests de fixture, perfil y provisión esperando fallo de autorización ausente; **comando previsto:** `.\mvnw.cmd "-Dit.test=SecurityFixtureIT,ProfileControllerIT" verify` y `.\mvnw.cmd "-Dtest=AccountProvisioningTest" test`. Si Failsafe no descubre `*IT`, configurar/documentar su objetivo antes de declarar resultado; no contar cero pruebas como éxito. Usar dobles explícitos de `AccountRepository`, pues el adaptador PostGIS se crea en tarea 3.
- [ ] Configurar validación criptográfica de `issuer`, audiencia, expiración y firma, y volver a verificar estado/rol en servidor. Los tests usarán un `JwtDecoder` controlado o claves de prueba solo para comprobar comportamiento, nunca un perfil demo permisivo. Registrar exactamente qué casos usan decoder simulado y cuáles validan firma real.
- [ ] Repetir tests, confirmar que las respuestas no contienen datos de otro usuario, y ejecutar suite pertinente. Registrar que fixture y dobles no demuestran autorización de los endpoints reales ni la persistencia: esas pruebas se ejecutan obligatoriamente en tareas 3–4. Revisión independiente de autorización antes de integrar.

## Tarea 3 — Migración y persistencia espacial

**Archivos futuros:** `backend/src/main/resources/db/migration/V1__h1_foundation.sql`; `backend/src/main/java/com/jhandev/transpormap/identity/persistence/PostgresAccountRepository.java`; `backend/src/main/java/com/jhandev/transpormap/alert/persistence/PostgisAlertRepository.java`; `backend/src/test/java/com/jhandev/transpormap/alert/persistence/PostgisAlertRepositoryIT.java` y `backend/src/test/java/com/jhandev/transpormap/identity/persistence/AccountProvisioningPostgisIT.java`.

**Consume:** `AlertRepository`, `AccountRepository` y los tipos de tareas 1–2. **Produce:** persistencia real de cuenta mínima y alerta, unicidad de `(author_id, client_report_id)`, punto WGS84 y lectura por ID. La imagen PostGIS y versión se fijarán tras validar compatibilidad/licencia; la imagen en CI y local debe ser la misma versión explícita.

- [ ] Primero escribir `PostgisAlertRepositoryIT.roundTripsColombianPoint`: iniciar contenedor PostgreSQL/PostGIS aislado, ejecutar migración, guardar una cuenta y alerta sintéticas, recuperar latitud `10.99` y longitud `-74.80`, título, autor y dos tiempos con tolerancia apropiada de precisión. La prueba debe usar BD real y cerrar recursos.
- [ ] Primero escribir `PostgisAlertRepositoryIT.duplicateClientReportIdPerAuthorIsRejected`: dos inserts con el mismo autor e ID producen una única fila; usar otro autor no debe consultar ni reutilizar el resultado del primero. Verificar también migración desde esquema vacío y constraint de integridad referencial.
- [ ] Primero escribir `AccountProvisioningPostgisIT.concurrentFirstLoginCreatesOneUser`: dos provisiones simultáneas del mismo `(issuer, subject)` crean exactamente una cuenta `ACTIVE/USER` y nunca elevan rol; un mismo `subject` de otro `issuer` no se vincula. Verificar que `findByIssuerAndSubject` devuelve únicamente la cuenta de la pareja exacta y ninguna para una pareja inexistente. Confirmar que una cuenta suspendida existente no se reactiva con un token nuevo y que no se vincula por correo coincidente.
- [ ] Ejecutar pruebas y observar fallo por migración/implementación ausente. **Comando previsto:** `.\mvnw.cmd "-Dit.test=PostgisAlertRepositoryIT,AccountProvisioningPostgisIT" verify` si Failsafe queda configurado en `verify`. Si Docker no está disponible, registrar como **bloqueada** y avanzar solo en tareas independientes; no usar H2 para declarar PostGIS validado.
- [ ] Implementar `V1__h1_foundation.sql` y adaptadores: punto con SRID 4326, acceso a geometría con parámetros preparados, identidad de cuenta y uniqueness transaccional. No guardar teléfono, contraseña, secretos ni coordenadas de búsqueda innecesarias. Verificar efecto de la extensión PostGIS en proveedor real antes de migrar fuera de local.
- [ ] Repetir con contenedor nuevo; revisar que no hay conversiones de grado a metro ni dependencia de Oracle. Ejecutar suite de integración y revisión de migración.

## Tarea 4 — Publicar y consultar la misma alerta

**Archivos futuros:** `backend/src/main/java/com/jhandev/transpormap/alert/AlertApplicationService.java`, `AlertService.java`, `CreateAlertResult.java`, `AlertView.java`, `AlertPage.java`, `AlertQuery.java`, `StoredAlert.java`, `AlertController.java`, `CreateAlertRequest.java`; `backend/src/main/java/com/jhandev/transpormap/shared/ApiExceptionHandler.java`; `backend/src/test/java/com/jhandev/transpormap/alert/CreateAlertPostgisIT.java`, `AlertApiContractIT.java`.

**Consume:** usuario activo de tarea 2, reglas de tarea 1, repositorio y transacciones de tarea 3; OpenAPI `POST /alerts`, `GET /alerts`, `GET /alerts/{alertId}` bajo servidor `/v1`. **Produce:** `AlertApplicationService.create(...)`, `findVisible(...)`, `listVisible(...)`, y respuestas HTTP de contrato.

- [ ] Primero escribir `CreateAlertPostgisIT.identicalRetryDoesNotDuplicate`: usuario autenticado A publica una alerta sintética con `clientReportId` y `Idempotency-Key`, recibe 201, repite misma solicitud y recibe 200 con el mismo `id`; contar una fila. Con mismo ID o clave y contenido modificado responder 409 sin segunda fila. Usuario B no obtiene información privada sobre la operación de A.
- [ ] Primero escribir `CreateAlertPostgisIT.rejectsFutureObservedAtWithoutPersisting`: con reloj fijo, hora +5 min +1 s devuelve 400 `application/problem+json` y no crea fila; +5 min sigue sujeto a validaciones normales. Probar datos de fecha inválidos y campos obligatorios ausentes con errores sin secretos.
- [ ] Primero escribir `AlertApiContractIT.createdAlertAppearsInListAndDetail`: crear alerta, verificar que lista y detalle tienen misma ID, punto, categoría, autor alias y tiempos, y que no aparece `clientReportId`, correo o contraseña en `Alert`. Los marcadores del frontend consumen este mismo punto, sin endpoint público de mapa.
- [ ] Primero escribir `AlertApiContractIT.photoIntentIsNotConflated`: `photoIntent=none` produce estado `none`, `pending` produce `pending`; falta `photoIntent` produce 400. No implementar carga de imagen bajo esta tarea.
- [ ] Primero escribir `AlertAuthorizationIT.unauthenticatedRequestsAreRejected` y `suspendedAccountCannotReadOrCreate` contra **endpoints reales** `GET /v1/alerts`, `POST /v1/alerts` y detalle: token ausente o inválido produce 401; token válido con cuenta suspendida o no habilitada produce 403 sin datos. Comprobar que los recursos retirados responden 404 al usuario ordinario. Estos casos completan la evidencia de las rutas fixture de tarea 2.
- [ ] Ejecutar casos esperando fallo de endpoints/comportamiento ausente. **Comando previsto:** `.\mvnw.cmd "-Dit.test=CreateAlertPostgisIT,AlertApiContractIT,AlertAuthorizationIT" verify` tras configurar Failsafe y runtime. Verificar cantidad de tests ejecutados y causa del fallo.
- [ ] Implementar servicio y controlador con validación de DTO, transacción única para alerta y deduplicación, respuesta 201/200/409, paginación y errores `ProblemDetail`. Para condiciones concurrentes, usar constraint de BD y probar dos solicitudes simultáneas; no depender solo de comprobar antes de insertar.
- [ ] Repetir pruebas y validar JSON/HTTP contra OpenAPI con un verificador reproducible definido en el PR. Una lectura de la respuesta de otro usuario no puede revelar `clientReportId`; no servir alertas retiradas. Si el servidor no puede determinar si un envío antiguo fue publicado, no reenviar automáticamente ni crear duplicados silenciosamente; conservar borrador y foto y pedir revisión manual. La estrategia que detecta dicha incertidumbre y su respuesta HTTP es una puerta H0/H5 pendiente; no presupone marcadores perpetuos.
- [ ] Ejecutar verificaciones de tareas 1–4, cobertura de dominio y escaneo de secretos. Obtener revisión independiente de permisos, migración y contrato antes de merge.

## Tarea 5 — Integración del esqueleto y decisión de despliegue

**Archivos futuros:** `backend/Dockerfile`, `infra/compose.yaml`, `.github/workflows/backend-ci.yml`, documentación local y de despliegue aprobada. Estos archivos **no existen ni se crean mediante este plan**.

**Consume:** backend de tareas anteriores, contrato validado y cliente H1 del equipo frontend. **Produce:** ejecución reproducible, CI verificable y capacidad para un flujo real integrado; despliegue solo tras puertas de coste y cuentas.

- [ ] Ejecutar desde clon limpio, tras instalar prerrequisitos documentados, un comando que arranque backend y PostGIS local y aplique la migración; el nombre exacto del comando se fijará en el PR y se probará. Ningún entorno local debe apuntar por defecto a una base remota real.
- [ ] Configurar CI de PR para build, pruebas unitarias y PostGIS en contenedor, contrato, revisión de seguridad y secretos, usando herramientas y acciones fijadas. Un trabajo que no encuentra pruebas o no puede arrancar contenedor no se marca como pase.
- [ ] Probar con frontend y dos cuentas autorizadas: iniciar sesión, publicar alerta sintética, verla en mapa/lista, abrir detalle y denegar acceso anónimo a API/detalle. Registrar versión de backend/cliente, datos sintéticos y evidencia sin credenciales.
- [ ] Verificar cuota y ausencia de método de pago por proveedor, registro/recuperación por correo real, origen de assets, autorización de fotos y plan de backups antes de activar despliegue. Supabase SMTP por defecto no sirve como correo general para visitantes; resolver proveedor o alternativa comprobable sin desactivar confirmación silenciosamente.
- [ ] Desplegar demo solo con preflight aprobado por sus condiciones y permisos. Registrar URL de demo, revisión independiente, CI y smoke test. Si falta proveedor o control de gasto, el resultado será **local/parcial y H1 seguirá abierto**; continuar tareas independientes sin atribuir un despliegue inexistente.

## Ejecución, revisión y cierre

- El orden mínimo es 0 → 1 y 2 → 3 → 4 → 5; las tareas 1 y 2 pueden avanzar en paralelo con archivos aislados una vez se fijen contratos. La tarea 3 comprueba persistencia real y provisión concurrente; la 4 verifica endpoints reales antes de declarar seguridad H1. La política de revisión manual de reintentos antiguos fue elegida; detectar cuándo aplica sigue siendo una puerta H0/H5.
- Cada tarea es una unidad revisable, pero el implementador puede dividirla en PR menores con pruebas y revisión independiente. Proponer commits convencionales por PR, sin hacer commit ni merge desde este documento.
- **Comandos previstos:** las referencias a `.\mvnw.cmd`, CI, Docker y contenedor son instrucciones futuras. No están presentes en el legado ni se han ejecutado como parte de este plan.
- **Criterio backend H1:** pruebas unitarias y de integración realmente ejecutadas y pasando; migración PostGIS probada; endpoints del subconjunto implementados y validados contra contrato; revisión de seguridad/privacidad y secretos; documentación actualizada. Rutas, fotos, sincronización offline y recuperación completas no se presentan como terminadas.
- **Criterio hito H1 completo:** además de backend y frontend, flujo autenticado real y demostrable, CI/CD, demo desplegada con costo controlado y revisión independiente. Un H0 con proveedor de correo, permisos, coste o respaldo no verificado sigue identificado en el reporte; no cambiar requisitos para cerrar el hito.

## Auto-revisión del plan

| Control | Resultado de esta planificación |
|---|---|
| Cobertura de alcance backend H1 | HU-01 parcial, HU-03 parcial, HU-05 parcial; reglas de identidad, campos, ubicación, fecha e idempotencia de este subconjunto asignadas. HU-02 y HU-04–11 completas se cubren en hitos posteriores. |
| Contratos cruzados | Usuario y puerto de repositorio se consumen explícitamente; DTO debe transformarse desde/a OpenAPI y la clave de idempotencia se pasa al servicio. Los tipos de dominio son propuestos y deben definirse antes del consumidor. |
| Fallos prioritarios | Los cinco casos de revisión están asignados a tareas con pruebas identificadas. La prueba de expiración y la protección tras retiro deben ampliarse en H2/H5/H6. |
| Disponibilidad y riesgos | No hay contenedor detectado ni proveedor/correo con preflight real. Se acordó revisión manual ante reintento antiguo incierto; detectar el caso sigue sin diseño validado. Ninguna tarea puede presentar esos puntos como verificados. |
| Límites de este documento | El esqueleto fija Spring Boot 4.1.2 y Java 25; PostGIS, proveedor real y comando integrado de arranque siguen pendientes, junto con compatibilidad de hosting y gates de preparación. |
