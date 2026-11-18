# TransporMap — Registro de descubrimiento

Fecha de registro: 12 de noviembre de 2026.

Última actualización: 18 de noviembre de 2026, con corrección de acceso y especificaciones de dispositivos.

Estado: Fases 0, 1 y 2 aprobadas; dirección de Fase 3 aprobada de forma condicionada. Fase 4 en preparación. El propietario aclaró que el despliegue será una demo de portafolio, no operación vial de producción. Este documento recoge decisiones del propietario y propuestas por validar. La fuente de verdad de requisitos es `docs/01-requisitos.md`; ninguna aprobación autoriza código de producción hasta aprobar expresamente la planificación de Fase 4.

## 1. Decisiones confirmadas por JhanDev

| ID | Decisión | Alcance de lo aprobado |
|---|---|---|
| D-01 | Informe de Fase 0 aprobado. | Se acepta el diagnóstico estático; no implica ejecución validada ni aprobación de las siguientes fases. |
| D-02 | Usuarios principales: transportistas; también intervienen administradores. | La consulta de alertas y las contribuciones requieren cuenta y sesión iniciada. JhanDev será administrador inicial. |
| D-03 | Consulta previa al viaje y reportes estando detenido. | Aprobó la recomendación de seguridad vial; no diseñar el reporte como tarea para el conductor en movimiento. |
| D-04 | La referencia a Waze se limita a publicaciones y alertas comunitarias. | Navegación giro a giro queda fuera de la referencia solicitada. Consulta de alertas sobre una ruta aún debe precisarse; no se asume motor de navegación. |
| D-05 | Offline: consultar alertas guardadas y preparar reportes pendientes. | Recomendación aprobada en lote 2. Descargar mapas regionales queda fuera del alcance inicial; no confundir datos de alertas guardados con mapa offline. |
| D-06 | Actualización rápida con la app abierta y avisos limitados a zonas elegidas. | Recomendación aprobada. Meta propuesta de hasta 10 segundos con conexión y servicio activo, pendiente de condiciones de medición y viabilidad. Avisos con app cerrada siguen condicionados; no hay garantía de recepción offline ni elección de protocolo. |
| D-07 | Confirmaciones «Sigue vigente» / «Ya no está». | Una contribución actual por usuario, modificable; aprobación expresa del lote 2. Sustituye al like social como mecanismo solicitado; no equivale a certificación de veracidad. |
| D-08 | Validación territorial acotada, manteniendo Colombia como alcance conceptual. | El propietario no conoce un corredor ni identifica transportistas de prueba. Se documenta falta de validación real. No existe piloto con usuarios acreditado; escenario geográfico de demostración pendiente de proponer. |
| D-09 | Disponibilidad aproximada de 10 horas por semana, sin fecha límite. | Proyecto en segundo plano; estimar por hitos. La disponibilidad personal no equivale a capacidad ilimitada de implementación o moderación. |
| D-10 | Java con Spring Boot obligatorio. | Principal objetivo profesional: backend Java, complementado por herramientas y cliente que se justifiquen. No hay frontend o arquitectura elegidos. |
| D-11 | Producto profesional, completo y público para portafolio; inicialmente no comercial. | Completitud respecto de alcance aprobado y verificable. No se ha comprometido un servicio crítico de información vial. |
| D-12 | Despliegue mediante herramientas gratuitas. | Priorizar proveedores sin tarjeta ni cargos automáticos; si una alternativa exige tarjeta, presentarla como excepción para decisión expresa. Se acepta suspensión o arranque lento compatible con la demo. No se autoriza gasto implícito. |
| D-13 | Reemplazar dependencias comerciales de evaluación, incluido JxBrowser. | Propietario recuerda periodos de prueba de 30 días y no acredita origen/licencia de MyLicense. La alternativa se decidirá en arquitectura; no se ha borrado ni ejecutado el legado. |
| D-14 | No codificar hasta autorización explícita. | Se mantienen checkpoints de fases y aprobación expresa de planificación antes de producción. |
| D-15 | Cada publicación corresponde a una alerta; sin comentarios. | Aprobado en lote 2. No se incorpora un tablón de mensajes generales ni una red social paralela. |
| D-16 | Catálogo inicial aceptado. | Mal estado, cierre total, cierre parcial, accidente, derrumbe/inundación, obras y restricción de carga. Nombres definitivos, distinción entre fenómeno e impacto, severidad y duración requieren reglas explícitas en requisitos. |
| D-17 | Consulta y contribución solo para usuarios registrados con sesión iniciada. | Corrección expresa del propietario el 18 de noviembre, que sustituye la lectura pública aceptada anteriormente. No se exige acreditar profesión mediante documentos. Acceso administrativo separado. |
| D-18 | Moderación manual inicial a cargo de JhanDev. | Retirar contenido, atender reportes de abuso y suspender cuentas con motivo registrado. Alertas visibles antes de revisión salvo controles antiabuso; no depender de aprobación manual previa de cada reporte. |
| D-19 | El administrador debe visualizar métricas del sistema. | Necesidad aprobada. Propuesta de contenido en sección 2; no se presupone acceso a ubicación continua o a datos personales innecesarios. |
| D-20 | Fotos opcionales en la versión completa; Cloudinary propuesto por el propietario. | Comparar el servicio en Fase 3 y diseñar envío diferido con foto guardada localmente. No hay proveedor seleccionado. |
| D-21 | Consultar alertas por ruta de A a B y, si no hay ruta, por cercanía. | Ambas capacidades forman parte del alcance; no implica navegación giro a giro. Precisión de trazado, radio y casos de indisponibilidad a detallar en requisitos. |
| D-22 | Dispositivos disponibles: Tecno Camon 20 Pro 4G con Android 14 y HP Pavilion Gaming 15-dk10000 (8VD47AV), 24 GB RAM y GTX 1650 Ti de 4 GB VRAM. | Datos proporcionados por el propietario; Windows observado en el entorno de trabajo. Navegadores, CPU y espacio disponible pendientes; no se han efectuado pruebas. |
| D-23 | Avisos con la aplicación cerrada condicionados a viabilidad gratuita; voz fuera del alcance inicial. | No se garantiza recepción y no se eligió mecanismo. |
| D-24 | Privacidad: alias y punto del incidente visibles para usuarios autenticados; teléfono, correo y trayectorias privados. | D-17 excluye acceso anónimo. Sin seguimiento continuo ni verificación documental exigida. Tratamiento, retención y eliminación a especificar en Fase 2. |
| D-25 | Interfaz y manual en español; README en español con resumen en inglés. | Aprobado para público colombiano y portafolio. |

No quedan aprobadas por inferencia: PWA, framework frontend, base de datos, Cloudinary como proveedor definitivo, proveedor de mapas/hosting, mecanismo de autenticación, tecnología push, WebSocket, puntuación de reputación, número de usuarios ni cobertura nacional operativa.

## 2. Precisiones que debe recoger el diseño

### Offline e inmediatez

Un dispositivo sin conexión no puede recibir reportes nuevos del servidor ni publicar los suyos a otros dispositivos hasta recuperar comunicación. El producto debe informar claramente qué está guardado localmente, cuándo se sincronizó y qué está pendiente de enviar. «Publicado» no puede significar solamente «guardado en el teléfono».

Alcance aprobado: consultar alertas previamente sincronizadas y crear reportes pendientes sin conexión. La propuesta de comportamiento verificable es enviarlos al recuperar conectividad y cuando la plataforma permita ejecutar la sincronización, evitando duplicados; no se promete ejecución con la app cerrada. Conservar la hora de observación y la de recepción en el sistema, sin presentar una alerta antigua como recién ocurrida. El contenido offline puede estar desactualizado: debe ser visible.

Descargar un mapa regional queda fuera del alcance inicial aprobado. La consulta offline debe poder aportar valor mediante los datos guardados aunque no haya teselas disponibles. La difusión «inmediata» se plantea con una meta de 10 segundos bajo conexión y servicio disponible; faltan carga, percentil y entorno de medición. También necesita una experiencia diferenciada ante suspensión del hosting, error o reconexión. La cifra no se presenta como una garantía permanente ni como resultado ya medido.

### Credibilidad comunitaria

Un like acredita apoyo declarado, no veracidad objetiva. Popularidad, confirmación presencial y evaluación de moderación no deberían confundirse.

Se aprueban acciones explícitas «Sigue vigente» y «Ya no está» en cada publicación, con una contribución actual por cuenta y posibilidad de cambiarla. Se propone presentar antigüedad y confirmaciones recientes y evitar una insignia de «verdadero» basada solo en el total. No se incluye un like social separado ni comentarios. Se necesitan medidas de abuso, pero no se propone de entrada una fórmula compleja de reputación. Debe definirse en requisitos cuándo las confirmaciones influyen en el estado, quién puede cerrar y cómo se resuelven señales contradictorias.

### Seguridad vial y privacidad

El reporte se diseña para usuario detenido o acompañante. No se presupone seguimiento continuo, funcionamiento en segundo plano ni envío de ubicación a todos los usuarios. Se aprueba limitar los avisos a zonas elegidas; el requisito de avisos con la app cerrada y el canal concreto siguen pendientes.

JhanDev será administrador inicial y aprueba retirar contenido, atender reportes de abuso y suspender cuentas con un motivo registrado. El sistema debe distinguir un aviso retirado por moderación de una alerta que dejó de estar vigente. Los datos públicos del autor y conservación de datos se resolverán antes de cerrar requisitos.

### Métricas administrativas

La necesidad del panel de métricas está aprobada. Se propone este contenido para revisión con los requisitos, sin seleccionar herramientas:

- **Producto:** alertas activas y cerradas por tipo/periodo/zona; publicaciones recibidas y confirmaciones. Los datos de demostración deben distinguirse de actividad real.
- **Moderación:** reportes de abuso pendientes y resueltos, retiros y suspensiones; historial con actor, fecha y motivo.
- **Operación:** disponibilidad observada, errores y latencia de la API, resultados de sincronización y retraso de distribución. El servidor solo mide operaciones que conoce; no puede contar con precisión colas de dispositivos desconectados ni garantizar recepción por mostrar un envío exitoso.
- **Recursos:** consumo de cuotas gratuitas cuando el proveedor lo exponga, con fecha de actualización. No prometer información en tiempo real ni métricas que requieran un servicio adicional de pago.

Se propone evitar métricas de seguimiento individual, trayectorias de conductores y exposición de reportes privados. Las definiciones, ventanas temporales y retención se concretarán en Fase 2; no se agregan herramientas por cada métrica.

### Limitación de validación con transportistas

El propietario indica que no conoce un corredor para el piloto. No se ha identificado acceso a usuarios reales. Se documentará la limitación y se propondrá un escenario geográfico de demostración con datos sintéticos claramente etiquetados. Las pruebas técnicas y escenarios de uso no se presentarán como entrevistas, adopción ni validación de campo. Conseguir participantes puede mejorar el producto, pero no se presupone como un recurso disponible.

## 3. Efectos sobre requisitos y planificación

Las siguientes son implicaciones propuestas, no un roadmap aprobado ni estimaciones de entrega:

| Área | Ajuste a preparar | Qué lo condiciona |
|---|---|---|
| Producto | Dos formas complementarias de consultar las mismas alertas: mapa y publicaciones, sin comentarios ni mensajes generales. | Campos, filtros y ordenamiento pendientes de criterios de aceptación. |
| Esqueleto andante | Autenticación, publicación real y consulta en mapa, desplegadas con CI/CD, según el encargo original. | Arquitectura y proveedores gratuitos aprobados; incluir identidad estable del reporte para soportar reintentos posteriores. |
| Sincronización | Lectura local y cola de reportes aprobadas; diseñar reintentos, duplicados y manejo de sesión expirada. | Plataforma por seleccionar; no se promete sincronización con cliente cerrado. |
| Distribución | Actualización de clientes interesados tras publicar. | Audiencia, canal, latencia, cliente abierto/cerrado y límites gratuitos. |
| Vigencia | Fechas, cierre y caducidad visibles tanto en publicaciones como en mapa y caché. | Tipos de alerta, reglas de cierre y quién puede cambiarlas. |
| Comunidad | Confirmaciones «Sigue vigente» / «Ya no está» y reporte de abuso. | Reglas de cierre, discrepancias y expiración; no hay comentarios. |
| Administración | Cola de revisión, acciones auditables y métricas. | JhanDev es responsable inicial; contenido y retención del panel por concretar. |
| QA | Escenarios de red interrumpida, reconexión y estados de información desactualizada. | Criterios medibles acordados; probar además autorización y acciones concurrentes. |
| Operación | Demostración pública sostenible a costo cero, con comportamiento explícito cuando un servicio gratuito no esté disponible. | Restricciones sobre tarjeta, cuotas, suspensión y proveedores vigentes. |
| Roadmap | Estimar cada hito en horas y traducirlo usando 10 h/semana. | Alcance final y margen para aprendizaje, revisiones, pruebas y despliegue. No fijar una fecha sin estos datos. |

La decisión de plataforma se evaluará en Fase 3 comparando web responsive/PWA y alternativas móviles nativas o multiplataforma, incluidas restricciones reales de GPS, offline, notificaciones, publicación y mantenimiento individual. No se resuelve por familiaridad o tendencia.

## 4. Criterios de aceptación candidatos para discutir

No están aprobados ni constituyen garantías de implementación:

- Sin red, las alertas guardadas siguen siendo consultables con indicador de desconexión y última sincronización.
- Un reporte creado offline aparece como pendiente; solo se identifica como publicado tras confirmación del servidor.
- Los reintentos por pérdida de conexión no generan varias publicaciones del mismo envío.
- Tras recuperar red se sincronizan cambios, cierres y retiros; las operaciones rechazadas o que requieren volver a autenticarse no se descartan silenciosamente.
- Los reportes diferidos conservan la hora observada y no se presentan como hechos nuevos por haberse recibido más tarde. Se debe acordar cómo tratar reloj incorrecto y reportes demasiado antiguos.
- Cambiar de cuenta o cerrar sesión no mezcla colas ni datos privados entre usuarios.
- La publicación se confirma al autor aunque falle un canal secundario de notificación; el receptor tiene un mecanismo para ponerse al día.
- El total de apoyos no sustituye los estados de vigencia ni las decisiones de moderación.
- El cliente recibe novedades dentro de un umbral por definir, bajo condiciones de conectividad y servicio también por definir; se mide con una prueba reproducible.
- La misma alerta mantiene identidad y estado coherentes en mapa, publicaciones y datos guardados localmente.

## 5. Segundo lote de preguntas — respondido

Respondido el 13 de noviembre de 2026. Las preguntas se conservan como historial; las decisiones vigentes están en la sección 1. No es necesario volver a responder este lote.

Nota histórica: la recomendación de lectura pública de la pregunta 6 fue sustituida por la corrección expresa del 18 de noviembre: consulta solo con sesión iniciada (D-17).

1. **Offline:** ¿es suficiente consultar alertas guardadas y preparar reportes pendientes, o es indispensable descargar el mapa de una zona? Recomendación: alertas guardadas y cola de reportes como alcance inicial; mapas regionales requieren evaluación propia de licencias, almacenamiento y distribución.
2. **Difusión:** ¿necesitas novedades con la app abierta, avisos con la app cerrada, o ambos, y para qué zona/corredor? Recomendación: actualización rápida con app abierta y avisos de zona elegida como candidato adicional; proponer hasta 10 segundos bajo conexión y servicio activo como meta a validar técnicamente, no garantía permanente.
3. **Apoyo comunitario:** ¿el like debe expresar simpatía o confirmar que el hecho sigue ocurriendo? Recomendación: «Sigue vigente» / «Ya no está», una contribución actual por usuario modificable; aporta información accionable sin equiparar popularidad con verdad.
4. **Publicaciones:** ¿cada publicación debe corresponder a una alerta o también habrá mensajes generales y comentarios? Recomendación: una publicación por alerta, con confirmaciones y reporte de abuso; dejar el alcance de conversaciones generales para una decisión expresa.
5. **Taxonomía y vigencia:** ¿aceptas revisar como base mal estado, cierre total, cierre parcial, accidente, derrumbe/inundación, obras y restricción de carga, con gravedad e impacto diferenciados? Recomendación: empezar con ese catálogo candidato y acordar después duración por tipo y cierre; no conservar automáticamente los textos ambiguos del legado ni inventar duraciones.
6. **Acceso:** ¿cualquier visitante puede consultar y solo cuentas registradas publican/confirman, o toda consulta requiere cuenta? Recomendación: lectura pública y cuenta para contribuir, por facilidad de acceso y demo; no exigir documentos de conductor sin necesidad justificada.
7. **Piloto y validación:** ¿qué corredor conoces y tienes transportistas que puedan probar el producto? Recomendación: seleccionar un corredor donde puedas conseguir retroalimentación; si no hay acceso a usuarios, identificar explícitamente esa limitación y no presentar pruebas simuladas como validación real.
8. **Administración:** ¿serás tú el administrador inicial y autorizas que el alcance incluya retirar contenido, atender abuso y suspender cuentas con registro de motivo? Recomendación: administración manual sencilla y reportes visibles antes de revisión salvo restricciones antiabuso; evita depender de moderación previa continua que retrase todos los avisos. Sus acciones exactas requieren aprobación.

## 6. Tercer lote: respondido el 17 de noviembre de 2026

Las decisiones resultantes constan en D-09, D-12 y D-20 a D-25. Las preguntas se conservan como historial; no deben volverse a pedir.

1. **Plazo:** ¿tienes una fecha límite o prefieres fijarla tras estimar el alcance? Recomendación: estimar primero usando 10 h/semana y acordar hitos; evita una fecha que no incluya aprendizaje, QA y operación.
2. **Gratuidad:** ¿deben descartarse proveedores que pidan tarjeta aunque tengan plan gratuito y aceptas suspensión/arranque lento? Recomendación: priorizar sin tarjeta y sin cargos automáticos; si una alternativa lo exige, presentarla como excepción a decidir, nunca autorizar gasto implícitamente. Documentar la suspensión compatible con la demo, sin simular disponibilidad permanente.
3. **Dispositivos:** ¿qué teléfonos y computadoras tienes disponibles para probar, y qué sistema usan? Recomendación: priorizar experiencia móvil y acceso desde navegador de escritorio; elegir plataforma después de comparar requisitos y dispositivos reales. No asumir PWA ni descartar iOS sin respuesta.
4. **Fotografías:** ¿las fotos opcionales deben formar parte de la versión completa? Recomendación: incluir evidencia opcional con límites y moderación si aporta al objetivo; retirar metadatos de ubicación y evitar rostros/placas. Se evaluarán almacenamiento y viabilidad gratuita antes de cerrar arquitectura.
5. **App cerrada y voz:** ¿los avisos con la app cerrada son indispensables o pueden condicionarse a viabilidad gratuita? Recomendación: tratarlos como capacidad deseada por evaluar y no prometer recepción garantizada; dejar reporte por voz fuera del alcance inicial y mantener publicación estando detenido.
6. **Zona o ruta:** ¿basta consultar por cercanía y zonas elegidas, o es indispensable buscar alertas a lo largo de una ruta entre origen y destino? Recomendación: cercanía y zonas primero; búsqueda sobre rutas es una capacidad distinta de navegación y requiere evaluar datos/servicios adicionales. La decisión define alcance final, no solo orden de implementación.
7. **Privacidad:** ¿aceptas mostrar un alias y el punto del incidente, sin teléfono, correo ni trayectorias públicas, y recopilar solo los datos necesarios para la cuenta? Recomendación: sí, sin seguimiento continuo ni verificación documental del conductor; el punto representa el incidente y no certifica presencia del autor. Política de tratamiento, eliminación y retención se detallarán para aprobación de requisitos.
8. **Idioma:** ¿aceptas interfaz y manual de usuario en español, con README principal en español y un resumen en inglés? Recomendación: esa combinación atiende a usuarios colombianos y permite presentar el proyecto a reclutadores de otros idiomas sin duplicar toda la documentación.

Pendientes para propuestas concretas de Fase 2, no decisiones que se asumirán aprobadas: duración por tipo y reapertura/cierre; severidad frente a impacto vial; límites de fotos y retención si se aprueban; recuperación de acceso; campos del perfil; métricas y permisos detallados; licencia del código y procedencia de assets. En Fase 3 habrá verificación vigente de condiciones gratuitas antes de recomendar proveedores. Las restricciones actuales no justifican adelantar la elección del stack.

## 7. Checkpoints restantes

1. Fase 1: resumen aprobado por JhanDev el 18 de noviembre de 2026.
2. Fase 2: `docs/01-requisitos.md` aprobado por JhanDev, incluidas las siete decisiones iniciales y cinco finales.
3. Fase 3: dirección de arquitectura aprobada por el propietario para demo de portafolio, con condiciones de proveedores, respaldo, seguridad y viabilidad pendientes.
4. Redactar `docs/03-plan-y-roadmap.md` con hitos, backlog, estimaciones a 10 h/semana, DoD, pruebas y estrategia Git; solicitar aprobación explícita de Fase 4.
5. Solo entonces, y con autorización del propietario, construir y desplegar iterativamente.
