# Contrato API — notas de H0

El [OpenAPI propuesto](openapi-propuesta.yaml) describe una API futura y no constituye una implementación. La dirección de arquitectura está aprobada de forma condicionada; las decisiones que siguen abiertas no se convierten en hechos por aparecer en el contrato. La API usa nombres y códigos provisionales que deben congelarse junto con el proveedor y los tests de contrato de cada iteración.

## Alcance cubierto

- Cuenta y alias, preferencias de zonas, autorización local candidata, recepción de solicitudes de privacidad, alertas y filtros, cercanía y ruta, confirmaciones, fotografía privada, abuso, moderación, métricas y comprobación básica de salud.
- Una instantánea coherente entrega páginas vinculadas a un mismo corte y un cursor de cambios. El servidor revalida cuenta y visibilidad en cada página e invalida con `410` la instantánea afectada por una retirada o cambio de autorización; nunca entrega el contenido retirado desde una página posterior. Tras `410` el cliente conserva la copia previa aislada y sin mostrar, hasta completar la nueva instantánea y aplicar las retiradas posteriores al corte; luego puede sustituirla. Una retirada posterior a una respuesta completada se propaga en cambios; no hay revocación remota inmediata sin red.
- Todas las operaciones salvo `/health` heredan Bearer. Registro, inicio, cierre y recuperación son responsabilidad del proveedor de identidad escogido o una interfaz equivalente todavía por definir; no se documentan aquí como endpoints propios. Spring deberá validar token, estado y permisos en cada operación protegida.
- No se entrega foto con URL pública permanente: lectura condicionada a sesión y visibilidad, cabecera de caché privada; subida autorizada al autor con cuenta activa y revalidación tras carreras con moderación. El servidor no puede borrar copias ya descargadas desde dispositivos ajenos.
- `clientReportId` se genera antes de guardar offline y no se incluye en el esquema de respuesta visible para otros usuarios. Las peticiones reutilizadas con contenido distinto producen conflicto; si el reporte fue retirado o la cuenta perdió acceso, el reintento no devuelve su contenido ni crea otra alerta. `photoIntent` indica `none` o `pending` desde la creación para que texto publicado con foto pendiente no se confunda con publicación sin foto. Los datos locales y las colas se separan por cuenta.
- La cercanía envía coordenadas en el cuerpo POST sobre HTTPS, no en URL. Se requiere evitar el registro de cuerpos y coordenadas en servidor, proxies y telemetría, e impedir cachés compartidas; el método POST y TLS por sí solos no garantizan privacidad. La ruta A–B también transporta información sensible y exige los mismos controles.
- La fecha futura mayor a cinco minutos genera rechazo corregible; el borrador y la fotografía no se sustituyen ni eliminan automáticamente. El contrato de transporte describe el rechazo: preservar los archivos es responsabilidad del cliente y debe probarse.
- El recurso de autorización offline es **propuesta de interfaz**, no prueba de que una firma o proveedor concreto implementen la política; debe vincularse a una cuenta y no superar siete días desde la validación en línea. No puede garantizar detección de suspensión remota sin red ni protección absoluta de un dispositivo comprometido.

## Decisiones no resueltas que bloquean cerrar el contrato operativo

1. **Identidad y recuperación:** proveedor, cookies frente a Bearer, ciclo de tokens, revocación de sesiones, registro, correo y recuperación. No se ha probado ningún servicio y no deben inventarse sus endpoints. Si se eligen cookies, revisar CORS/CSRF y actualizar `securitySchemes`.
2. **Idempotencia y eliminación:** los borradores locales no tienen vencimiento, las alertas se conservan 90 días y los datos pueden requerir eliminación. La decisión aprobada para un envío antiguo incierto es conservar borrador/foto, no publicar automáticamente y solicitar revisión manual. Queda por demostrar cómo identificar el estado incierto sin conservar información personal indefinidamente; con evidencia suficiente se devuelve el resultado previo solo si sigue autorizado. No se promete idempotencia indefinida.
3. **Instantánea y cursores:** TTL, tamaño máximo, volumen admisible, almacenamiento de snapshots y estrategia para garantizar el corte y los cambios concurrentes deben definirse y probarse; `hasMore`, cursor de página, cursor de cambios y eventos de retirada describen el comportamiento esperado, no un algoritmo implementado.
4. **Sesión offline:** formato y verificación íntegra de la prueba, clave, rotación, manejo de reloj manipulable, separación de cuentas y almacenamiento local deben definirse y evaluarse frente al requisito aceptado de siete días. El servidor no puede imponer revocación inmediata en un cliente desconectado.
5. **Fotos:** máximo 5 MB/una imagen aceptados; formatos, dimensiones, validación por contenido, estados del proveedor, limpieza de huérfanos, expiración de enlaces y originales/derivados pendientes de diseño y comprobación. Si se elige subida directa, la autorización y la confirmación de asociación deben resistir el retiro y suspensión concurrentes.
6. **Paginación y geometría:** ventanas, orden estable y vida de cursores de lista/cercanía/ruta; selección de alternativas, representación de zona, límites de consulta y tratamiento de filtros deben probarse con PostGIS y con condiciones de cuotas reales. Ruta A–B no garantiza aptitud de vía para transporte de carga.
7. **Operación y privacidad:** materialización y periodos de métricas, proceso de solicitudes del titular, auditoría, restauración y reintegración de eliminaciones siguen por especificar; `application/problem+json` y sus códigos de negocio son candidatos, sin revelar datos personales o secretos.

## Comprobaciones locales y límite de la validación

El YAML fue parseado con `js-yaml` 3.14.1 ya disponible en una extensión local de VS Code, sin instalar paquetes. Se revisaron **24 rutas, 29 operaciones y 134 referencias locales**, parámetros de ruta, respuestas 2xx, seguridad global, cursor `410`, foto privada, cercanía POST y que `/health` sea la única operación explícitamente pública. También se ensayaron seis casos de `Point`, `CreateAlert` y `Alert` con AJV 4.11.8 y otros seis específicos: intención de foto requerida, `upsert` sin alerta rechazado, `removed` sin payload aceptado, `removed` con payload rechazado y métricas nulas aceptadas. Para probar `oneOf` con AJV 4 se convirtió `const` a `enum` en memoria: esa conversión no valida JSON Schema 2020-12 ni constituye prueba de compatibilidad OpenAPI 3.1.

En el equipo actual se puede repetir el parseo sin instalar dependencias mediante PowerShell:

```powershell
node -e "const y=require('[ruta-local]/.vscode/extensions/ritwickdey.liveserver-5.7.10/node_modules/js-yaml');const d=y.safeLoad(require('fs').readFileSync('docs/api/openapi-propuesta.yaml','utf8'));console.log(d.openapi,Object.keys(d.paths).length)"
```

El parser y AJV pertenecen a una extensión local y sus rutas/versiones no son una dependencia reproducible del proyecto; el pipeline deberá fijar sus propias herramientas cuando se apruebe e implemente.

Posteriormente se validó el documento con **Redocly CLI 2.79.0**, perfil `minimal`, ejecutado desde caché local sin red. Se corrigieron dos errores estructurales causados por comas sin comillas en mapas YAML, se agregaron descripciones de tags y `operationId`, y se cambió la ruta de instantáneas a `/sync/alerts/snapshots` para eliminar ambigüedades. El resultado del comando de abajo fue **salida 0, sin errores ni advertencias**:

```powershell
$env:REDOCLY_TELEMETRY='off'
npx.cmd --offline --yes --cache "$env:TEMP\transpormap-npm-cache" @redocly/cli@2.79.0 lint --extends minimal docs/api/openapi-propuesta.yaml
```

El perfil `minimal` valida la estructura y reglas seleccionadas; no es una auditoría de seguridad ni prueba de comportamiento, cumplimiento de requisitos o validación exhaustiva de todos los vocabularios JSON Schema 2020-12. La ubicación de caché es local y puede no existir en otra máquina. AJV 4 tampoco verifica todos los vocabularios y formatos de 3.1. H0 sigue requiriendo resolver las decisiones anteriores y una revisión independiente antes de considerar el contrato operativo cerrado.

Referencia primaria para composición, rutas, seguridad y datos binarios: [OpenAPI Specification 3.1.0](https://spec.openapis.org/oas/v3.1.0); los binarios enteros se representan por su media type, y las partes multipart mediante `encoding` de acuerdo con dicha especificación. No se enviaron documentos ni datos del usuario a un validador externo.
