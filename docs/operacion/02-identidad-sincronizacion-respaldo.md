# H0 — Riesgos de sincronización y recuperación

Estado: diseño en análisis; no es una implementación ni un cambio aprobado de requisitos. La demo sigue sometida a [requisitos](../01-requisitos.md), especialmente borradores conservados hasta envío/descarte, autorización offline por siete días, no duplicación y retención.

## Identidad e idempotencia

Un reporte offline necesita un identificador generado y guardado con el borrador antes del primer envío. El servidor debe ligar ese identificador al autor y al contenido aceptado, y tratar los reintentos como la misma operación. Debe confirmar la cuenta activa y comprobar que una carga tardía de foto no reactive una alerta retirada. La pérdida de la respuesta después del commit se prueba explícitamente.

Hay una tensión que **no está resuelta**: los borradores no tienen fecha de vencimiento aceptada, pero alertas, datos personales y respaldos sí se eliminan según retención. Si el servidor olvida por completo una operación, un dispositivo que perdió la respuesta de aceptación y reintenta mucho tiempo después no dispone necesariamente de evidencia para distinguir un reporte ya aceptado y purgado de uno nunca enviado. Conservar para siempre identificadores asociables al usuario tampoco puede darse por permitido por defecto.

Opciones a evaluar antes de H5:

1. Mantener un registro mínimo de deduplicación con retención y base de tratamiento justificadas; determinar qué ocurre tras vencer y cómo atiende solicitudes de eliminación. No afirmar que un hash es automáticamente anónimo.
2. Establecer una ventana temporal de envío y, cuando no pueda garantizarse deduplicación, detener el envío sin descartar el borrador ni la foto y ofrecer recuperación explícita. Esa ventana cambiaría o precisaría el alcance y necesita revisión del propietario.
3. Mantener una prueba firmada persistente de cada aceptación y un mecanismo para que el cliente la recupere; evaluar el mismo problema de cuánto tiempo se conserva, seguridad y disponibilidad tras el borrado.

Requisitos irrenunciables para evaluar cualquier opción: no duplicar silenciosamente, no eliminar borradores sin acción del usuario, no cambiar de autor, respetar solicitudes de eliminación y no resucitar alertas retiradas. Registrar la solución y su límite demostrable en contrato y ADR antes de implementar. La simple expiración de una clave HTTP no resuelve el caso.

## Instantánea y cursores

El cliente obtiene una instantánea coherente de alertas autorizadas, paginada, y un cursor de cambios asociado a ese corte. Recupera eventos posteriores ordenados, incluidas retiradas; los reintentos no duplican estados. Si el cursor vence o se pierden eventos, el servidor lo comunica y el cliente reconstruye la instantánea antes de dar por actualizada la caché, preservando por separado las operaciones pendientes locales.

Quedan por especificar las garantías transaccionales de la instantánea, aislamiento por usuario, retención del registro de cambios, paginación, resolución de estados expirados durante el proceso y restauración tras fallos. La sincronización no acredita revocación inmediata offline; después de siete días la vista local se bloquea hasta revalidación.

## Respaldo y eliminación

Objetivos aceptados: retención de respaldos por 30 días; RPO de 24 h y RTO de 4 h como metas sujetas a prueba gratuita. Se necesita una copia coherente de metadatos, relaciones, migraciones y archivos de fotografía; `pg_dump` aislado no incluye archivos. Existen fallos parciales posibles entre base y objetos, por lo que se requiere inventario, comprobación de integridad y reglas para restaurar un par consistente.

Diseño candidato: generar copias cifradas de BD y objetos a destino independiente, guardar claves fuera de Git, aplicar rotación y realizar periódicamente una restauración en entorno aislado. La eliminación o retirada se debe reaplicar tras restaurar copias anteriores; guardar evidencias de RPO/RTO medidos y presupuesto total. El destino, scheduler, cuotas, facturación, seguridad de claves y prueba de restauración **no están demostrados**.

Un registro de eliminación o marcador también tiene su propia retención y contenido; no declarar resuelto el derecho de supresión solo por marcar un registro. La política deberá reflejar finalidades, respaldos y excepciones verificadas. Si la solución gratuita no permite demostrarla, conservar requisito pendiente y proponer alternativas al propietario, sin activar cargos ni reducir plazos por cuenta propia.
