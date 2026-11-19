# H1 — Diseño de intentos de publicación

**Estado:** diseño técnico del ajuste aprobado para H1. Existe un adaptador parcial IndexedDB con pruebas simuladas; no hay transporte ni recuperación automática, y este diseño aún no está acreditado en navegador real. Amplía la [arquitectura del cliente](../05-arquitectura-detallada-frontend.md) sin dar por terminada la cola offline de H5.

## Alcance y datos

Antes de un envío en línea, guardar en IndexedDB una operación con propietario `(issuer, subject)`, `clientReportId`, `Idempotency-Key`, cuerpo serializado exacto, estado y fechas de operación. No guardar token, contraseña ni correo en el registro. La operación se crea y confirma en una transacción local antes de cualquier POST. No enviar si esa transacción falla. La implementación debe demostrar el aislamiento de cuentas; IndexedDB por origen no lo proporciona por sí mismo.

H1 no cubre captura offline, fotos ni sesión local de siete días. El navegador puede denegar espacio, desalojar datos o permitir su borrado; no se promete durabilidad absoluta. Una cuenta suspendida no puede publicar aunque posea una operación guardada.

## Estados y transición

| Estado | Significado y acción |
|---|---|
| `preparado` | Identidad, cuerpo y propietario guardados; aún no se ha iniciado el envío. |
| `enviando` | El inicio se registró de manera durable antes de llamar a la red; solo una pestaña puede reclamarlo. |
| `confirmado` | Se recibió respuesta válida y se guardó de forma durable la identidad de la alerta. |
| `incierto` | Fallo de red, timeout, cierre o reinicio durante envío; conservar operación y detener reenvío automático. |
| `revision_manual` | No hay evidencia suficiente para resolver el resultado; explicar incertidumbre y conservar datos. |

Si falla el guardado de la confirmación tras una respuesta satisfactoria, conservar el intento para recuperación y no presentarlo como confirmado de forma durable. Un rechazo solo se considera definitivo cuando el contrato y el servidor acrediten que no hubo persistencia; `401/403` obligan a revalidar la identidad y conservar la operación bajo su propietario original. No interpretar un `404` por sí solo como prueba de que nunca se publicó.

## Concurrencia y servidor

El cliente necesita un reclamo atómico para impedir que dos pestañas envíen simultáneamente el mismo intento. Esto no sustituye la restricción transaccional del servidor por `(autor, clientReportId)`, ni garantiza deduplicación después de purgar evidencia. Reutilizar el cuerpo y la clave originales, nunca regenerarlos silenciosamente. La consulta de resultado, clasificación verificable de errores, retención de evidencia y acciones específicas de revisión manual aún deben definirse en API y probarse. No habilitar un reintento automático cuando no pueda demostrarse que es seguro.

## Pruebas de aceptación pendientes

- Fallo de transacción local o cuota: se comunica el error y no sale ninguna petición.
- Respuesta perdida tras escritura remota y reinicio durante `enviando`: se conserva el intento y no se envía automáticamente.
- Dos pestañas compiten por el mismo intento: como máximo una lo reclama; el servidor conserva una sola alerta bajo peticiones concurrentes.
- Cambio de cuenta, cierre de sesión y cuenta suspendida: sin acceso cruzado ni cambio de autor; los pendientes se conservan.
- Solicitud repetida autorizada: cuerpo e identidades exactamente iguales; alteración de contenido no cambia silenciosamente un intento incierto.
- Respuesta válida seguida de fallo al guardar la confirmación: conservar y recuperar sin afirmar confirmación durable.
- Evidencia eliminada y resultado desconocido: conservar datos y solicitar revisión manual, sin publicar automáticamente.

La política concreta de recuperación y el protocolo con el backend son condiciones previas al envío real, no garantías ya demostradas.
