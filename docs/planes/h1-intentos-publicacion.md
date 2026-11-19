# H1 — Diseño de intentos de publicación

**Estado:** diseño técnico del ajuste aprobado para H1. Existen adaptadores parciales IndexedDB y Web Locks y un coordinador experimental con pruebas simuladas; no hay transporte ni recuperación automática, y este diseño aún no está acreditado en navegador real. Métodos del almacenamiento pueden eludir el coordinador; el callback tampoco recibe todavía el snapshot autenticado. Resolver ambos antes de conectar envíos. Amplía la [arquitectura del cliente](../05-arquitectura-detallada-frontend.md) sin dar por terminada la cola offline de H5.

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

**Coordinación candidata, no acreditada:** evaluar Web Locks exclusivo por operación, con un nombre opaco que no revele datos de la cuenta. Ese nombre debe ser estable y compartido entre las pestañas para la misma operación; su construcción sigue pendiente de diseño y prueba. Todas las rutas participantes mantendrían el mismo bloqueo desde antes del reclamo hasta completar el transporte y guardar su resultado. Una recuperación solo podría intervenir tras adquirir ese bloqueo y volver a leer el estado en una transacción IndexedDB; si está ocupado, no debe modificar la operación. No usar `steal`. Si la API no está disponible o falla, conservar el intento y deshabilitar el envío y la recuperación automáticos; no usar un tiempo de espera como prueba de que otra pestaña terminó. Esta propuesta depende de que todas las rutas cooperen y requiere pruebas en dos pestañas y en Android real, incluidos cierres, suspensión y errores de persistencia. [Web Locks API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Locks_API).

La liberación o adquisición de un bloqueo **no indica si el servidor recibió o guardó la alerta**. Un resultado incierto se conserva y no se reenvía automáticamente sin evidencia suficiente. La prueba actual con `fake-indexeddb` usa varias instancias dentro de un proceso; no acredita reinicio real ni coordinación entre pestañas.

## Pruebas de aceptación pendientes

- Fallo de transacción local o cuota: se comunica el error y no sale ninguna petición.
- Respuesta perdida tras escritura remota y reinicio durante `enviando`: se conserva el intento y no se envía automáticamente.
- Dos pestañas compiten por el mismo intento: como máximo una lo reclama; el servidor conserva una sola alerta bajo peticiones concurrentes.
- Cambio de cuenta, cierre de sesión y cuenta suspendida: sin acceso cruzado ni cambio de autor; los pendientes se conservan.
- Solicitud repetida autorizada: cuerpo e identidades exactamente iguales; alteración de contenido no cambia silenciosamente un intento incierto.
- Respuesta válida seguida de fallo al guardar la confirmación: conservar y recuperar sin afirmar confirmación durable.
- Evidencia eliminada y resultado desconocido: conservar datos y solicitar revisión manual, sin publicar automáticamente.

La política concreta de recuperación y el protocolo con el backend son condiciones previas al envío real, no garantías ya demostradas.
