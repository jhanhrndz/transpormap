# TransporMap — Plan de implementación y roadmap

> **Estado: Fase 4 aprobada expresamente por JhanDev. H0 iniciado.** Dirección de ejecución: equipo de subagentes con revisión independiente antes de cada merge. Las puertas de coste, autorización y viabilidad continúan vigentes; aprobar el plan no acredita proveedores ni autoriza cargos.

**Objetivo:** evolucionar el TransporMap original hasta una demo pública de portafolio, funcional de extremo a extremo, con alertas accesibles solo mediante cuenta, información sintética identificada y backend Java/Spring Boot.

**Arquitectura aprobada condicionalmente:** monolito modular Spring Boot; PWA React/TypeScript; PostgreSQL/PostGIS; identidad, fotos privadas, mapas y rutas mediante adaptadores. La combinación Render/Supabase/Pages/Geoapify es candidata, no se ha confirmado como gratuita, segura o utilizable en la cuenta real. La demo no es un servicio vial operacional ni garantiza disponibilidad continua.

**Documentos base:** [requisitos aprobados](01-requisitos.md), [arquitectura](02-stack-y-arquitectura.md), [reconocimiento legado](00-informe-reconocimiento.md), [OpenAPI preliminar](api/openapi-propuesta.yaml), [plan de pruebas](04-plan-pruebas.md) y [ADR](adr/README.md).

## 1. Restricciones y alcance

- Respetar los checkpoints: el propietario aprobó expresamente esta planificación. La implementación debe seguir sus puertas de revisión, viabilidad y costes.
- Proyecto existente: conservar trazabilidad de flujos y campos del legado, sin copiar defectos ni afirmar que la BD perdida puede migrarse. JxBrowser/MyLicense no forman parte del nuevo runtime.
- Cuenta y sesión para todas las alertas y fotos. PWA candidata sujeta a comprobación física de sesión offline de 7 días, almacenamiento y sincronización en Android 14.
- Consulta por ruta A–B con margen inicial 1 km y por cercanía con radio 20 km; no declarar que la ruta es segura o apta para carga. Confirmaciones no verifican automáticamente un evento.
- Texto de alerta y foto opcional de máximo 5 MB; envío offline con identidad estable e idempotencia. Si foto falla, texto aceptado puede mostrarse con foto pendiente. Evitar que retiradas se reviertan por reintentos.
- Objetivo: COP $0 y priorizar sin tarjeta ni cargos automáticos. Si no se prueba un control de gasto, no se habilita despliegue o servicio sujeto a cargo. No crear cuentas ni activar facturación por inferencia.
- Objetivos RNF-01/02/09/13 se aceptaron como metas verificables sujetas a viabilidad; cualquier reducción exige decisión del propietario, no una sustitución tácita.
- Datos sintéticos de demostración claramente etiquetados. No hay transportistas ni corredor validados; las pruebas de sistema no equivalen a validación de campo.
- Avisos con app cerrada son condicionales; mapas offline regionales, comentarios y navegación giro a giro fuera del alcance acordado. No alterar esa distinción por conveniencia de implementación.

## 2. Condiciones iniciales y decisiones bloqueantes

La aprobación de este plan autoriza comenzar su trabajo técnico, pero no autoriza gastos, divulgación de secretos, contratación de planes ni cambios de alcance. H0 es el primer trabajo **después** de aprobación:

1. Confirmar estado del repo, preservar historia y documentar los secretos encontrados sin reproducirlos. Verificar titularidad y opciones de rotación/revocación, con intervención del propietario cuando una cuenta o permiso sea suyo. No borrar ni reescribir historial sin autorización específica.
2. Reconstruir tabla de correspondencia de 14 eventos y 11 rutas del legado a taxonomía acordada; registrar los ambiguos sin inventar equivalencias o datos Oracle.
3. Completar y validar el OpenAPI propuesto: semántica de snapshot/cursor vencido, paginación, filtros, cuenta/sesión, recuperación, zonas, fotos, abuso, métricas, errores, claves estables y ejemplos. El YAML actual **no está validado** por herramienta OpenAPI. No desarrollar contra detalles indefinidos sin decidirlos.
4. Verificar versiones soportadas, recursos locales y `Docker`/virtualización, restricciones reales del navegador Android 14, y licencias del código y assets. No presumir disponibilidad por el modelo del equipo.
5. Para cada proveedor candidato, registrar fecha, plan, necesidad de tarjeta, límites, cobro por excedentes, controles duros de presupuesto, suspensión, retención de datos y exportación. Las fuentes y condiciones deben consultarse de nuevo al momento de elegir. No usar credenciales históricas.
6. Definir y probar un diseño de imágenes con acceso solo autorizado, captura offline, original y derivados sin metadatos y eliminación consistente. Cloudinary se compara con almacenamiento privado Supabase; una firma ordinaria no basta para probar caducidad/revocación.
7. Resolver el diseño de identidad durable de reportes y reintentos tardíos después de 90 días y frente a solicitudes de eliminación. Como los borradores no tienen vencimiento, ni una tabla de claves que expire pronto ni un marcador eterno de datos personales pueden asumirse sin decisión. Documentar opciones, garantía y tratamiento antes de implementar sincronización.
8. Diseñar un ensayo recuperable de BD **y fotos** con respaldo cifrado, destino separado, 30 días de rotación, eliminación reaplicada y metas RPO 24 h/RTO 4 h. Debe probarse con cuentas/servicios reales solo cuando esté autorizado y se confirme coste cero. No afirmar que los proveedores Free lo ofrecen de serie.

**Puerta de decisión:** si una condición de coste, privacidad, plataforma o retención no se cumple, continuar trabajo independiente local cuando sea posible, marcar el hito remoto como bloqueado y presentar alternativas y consecuencias a JhanDev. Cambiar requisitos aceptados o incurrir en cargos requiere su decisión. El producto no se considera desplegado por existir una captura o mock.

## 3. Mapa de trabajo y carpetas propuestas

Las rutas son una propuesta de organización, no archivos creados; se confirmarán al preparar el primer PR. No borrar el legado durante el arranque. Antes de moverlo, comparar opciones de mantener `src/` intacto o copiarlo a una ruta `legacy/` y preservar recursos/historial sin introducir secretos nuevos. La alternativa elegida se explica en PR/ADR.

| Área candidata | Responsabilidad |
|---|---|
| `backend/` | Aplicación Spring, dominio modular, adaptadores, migraciones, configuración, pruebas, contenedor. |
| `frontend/` | PWA, mapa, autenticación, cola local y panel administrador. |
| `infra/` | Configuración versionada de desarrollo, pruebas, respaldo y despliegue; nunca secretos. |
| `.github/workflows/` | Verificaciones de PR y despliegue condicionado. |
| `docs/`, `docs/adr/`, `docs/api/` | Requisitos aprobados, decisiones, contrato, operación, pruebas y material de portafolio. |
| raíz (`README.md`, `CHANGELOG.md`, configuración de ejecución) | Entrada de documentación, convenciones y comando reproducible. |

La división es una decisión de planificación sujeta a inspección al empezar; contratos y tareas se especifican por capacidad sin inventar clases o scripts que todavía no existen. Los nombres exactos de clases, comandos y versiones se fijarán en planes de iteración revisables antes de cada PR, sin reducir este alcance.

## 4. Hitos, entregables y estimaciones

Horas estimadas de **una persona**, incluidos desarrollo, pruebas y documentación de cada hito. Los subagentes colaboran, pero sus tiempos de coordinación y revisión no convierten automáticamente el trabajo en paralelo ilimitado. No sumar por separado el plan de QA o DevOps: están incluidos. Rangos preliminares de orden de magnitud; sin calendario garantizado ni disponibilidad de servicios. Las 10 h/semana son disponibilidad declarada, no velocidad observada.

| Hito | Entregable demostrable y condición de cierre | Horas |
|---|---|---:|
| **H0 Preparación y riesgos** | Matriz legado→dominio, versiones, contrato OpenAPI completado y validado, licencias, plan de identidad/idempotencia y preflight de proveedores/costo. Pruebas de concepto reversibles **solo tras aprobación Fase 4**. Si no hay proveedor apto, no declarar listo el despliegue. | 24–40 |
| **H1 Esqueleto andante** | Spring + PWA + PostGIS, identidad básica real, CI/CD y migración inicial. Usuario inicia sesión, crea alerta sintética y la ve en mapa y lista; acceso anónimo denegado. Un proveedor demo con coste controlado es prerrequisito del despliegue remoto. CI y funcionalidad local pueden revisarse aparte, pero H1 sin despliegue es parcial, no un hito terminado. | 44–70 |
| **H2 Ciclo de alerta** | Campos de denuncia heredados y nuevos, carretera/tramo opcionales, hora observada/recibida, +5 min, caducidad 24/72 h, cierres, histórico y confirmaciones reversibles sin duplicados ni auto-voto. | 50–80 |
| **H3 Rutas y cercanía** | Ruta A–B, geometría y PostGIS en metros 1 km/20 km, filtros, GPS con permiso o punto manual, alternativas y fallo de cuota/red visibles. Investigar cobertura, actualización y licencia de fuentes oficiales y enlazar fuentes verificadas; no importar automáticamente. No se afirma aptitud de ruta para camiones. | 55–90 |
| **H4 Identidad y perfil completo** | Completar registro/recuperación, alias, controles de acceso, cuenta suspendida, privacidad y políticas. La identidad básica usada en H1 no se vuelve a estimar como implementación nueva. | 45–75 |
| **H5 Offline y fotografías** | Sesión local 7 días, consulta guardada, cola durable y aislada, recordatorio visible a 30 días sin borrar pendientes, reconexión/idempotencia, una foto de 5 MB, metadatos retirados y carga posterior; cursor vencido y retirada; pruebas en Tecno Android 14. | 65–110 |
| **H6 Moderación y retención** | Cola de abuso, acciones con motivo, suspensión/restauración autorizadas, auditoría, vigencia/visibilidad separadas y retención aceptada; carrera de foto retirada sin resurrección. | 45–70 |
| **H7 Endurecimiento y operación** | Zonas de interés, desactivación de avisos y novedades con cliente abierto y puesta al día. Métricas y health, seguridad, accesibilidad, carga RNF-01/02, CI y revisión de dependencias, restauración de BD + fotos ensayada y política de respaldos/rollback. Cada meta incumplida se registra y escala. | 55–95 |
| **H8 Entrega de portafolio** | README español/resumen inglés, guías de usuario/despliegue, ADR y OpenAPI validados, changelog, datos de demo etiquetados, capturas/vídeo y guion para entrevistas; despliegue revisado. | 45–80 |
| **Total obligatorio estimado** | Suma de H0–H8, sin push cerrado condicionado ni espera externa. | **428–710** |

Alrededor de **43–71 semanas efectivas** a 10 h/semana (aprox. 10–17 meses), si la disponibilidad se cumple de forma sostenida. Las semanas no incluyen esperas de proveedor, pausas personales, retrasos de cuenta, validaciones con usuarios ni cambios de alcance; no constituyen fecha de entrega. Conforme se midan H0 y H1, revisar la estimación con evidencia y presentar ajustes.

**Opcional condicionado:** investigar y, solo si es viable gratuitamente, implementar avisos con la aplicación cerrada: **20–45 h adicionales**; no está incluido en el total ni se convierte en requisito garantizado. Los hitos pueden dividirse en varios PR pequeños. Toda iteración termina con pruebas/revisión/documentación y despliegue si el entorno aprobado está disponible. Si la dependencia remota impide desplegar, la capacidad local se puede revisar, pero la iteración no se presenta como entrega desplegada completa.

## 5. Backlog priorizado y aceptación

Orden propuesto por dependencia y riesgo; cada capacidad se cierra con tests relevantes, revisión y evidencia. Enlace de trazabilidad a [HU y RNF](01-requisitos.md):

| Prioridad | Trabajo | Hito | Evidencia mínima de aceptación |
|---|---|---|---|
| P0 | Licencias/secretos, OpenAPI, cuotas y recuperación: preflight | H0 | Lista de fuentes y límites actuales, matriz legado, contrato validado; riesgos sin resolver visibles. |
| P0 | Login, rol, datos persistentes, publicación y mapa/lista | H1 / HU-01,03,05 | E2E con dos cuentas; denegar lectura anónima de alertas/fotos; migración y despliegue demo si preflight pasa. |
| P0 | Vigencia, confirmaciones y campos heredados | H2 / HU-03,07,11 | Fecha futura +5 min; fecha tardía; límites 24/72 h; no autovoto; voto único, reversible y retirada. |
| P0 | Ruta A–B y fallback por cercanía | H3 / HU-06 | Tests espaciales contra PostGIS de 1 km/20 km, lat/lon, alternativa con permiso denegado y error sin ruta. |
| P0 | Recuperación de cuenta, privacidad y perfil | H4 / HU-01,02 | Usuario activo o suspendido; acceso por rol; correo real/verificado según cuotas; sin enumeración; sin datos privados en respuestas. |
| P0 | Cola, sesión offline, fotos privadas y sincronización | H5 / HU-04,05 | 20 reportes/5 fotos con cortes, identidad estable, reinicio, sesión vencida, cierre de sesión y cambio de cuenta en Android y servidor. |
| P0 | Zonas y novedades en cliente abierto | H7 / HU-08 | Crear/modificar zonas, desactivar avisos, recibir cambios elegibles y ponerse al día; medir RNF-01. Push cerrado permanece condicionado. |
| P0 | Fuentes oficiales | H3 / HU-06 | Registrar cobertura, fecha de actualización, licencia y enlaces verificables; no importar automáticamente sin autorización posterior. |
| P0 | Moderación, abuso y retención | H6 / HU-09 | Usuario ordinario rechazado; motivo/actor/fecha; retiro concurrente con carga/reintento sin reaparición; plazos 90/180/30. |
| P0 | Métricas, accesibilidad, seguridad y recuperación | H7 / HU-10, RNF-01–13 | Informe de carga, accesibilidad, escaneo y recuperación de BD+fotos; diferenciar meta incumplida de aprobado. |
| P0 | Documentación, demo y portafolio | H8 | Instalación desde clon limpio, guías, material de demo verificable, datos sintéticos y limitaciones visibles. |
| P1 condicionado | Push app cerrada | Después de H5, si procede | Demostrar viabilidad en plataforma/servicios sin costes, permisos y fallos; no afirmar entrega garantizada. |

Las historias del contrato de requisitos no asignadas directamente a una fila se cubren en el [plan de pruebas y matriz](04-plan-pruebas.md). Planes de iteración deben fijar archivos, firmas, casos y comandos reales cuando se conozcan sin simular que ya existen.

## 6. Plan de cada PR e iteración

1. Crear issue o entrada del backlog con HU/RNF, criterio de aceptación, archivos previstos y dependencia; definir una tarea revisable y su evidencia. La posibilidad real de crear issues/PR depende de permisos GitHub y se verifica al ejecutar; no inventar enlaces ni estados.
2. Crear rama de trabajo aislada desde la base acordada; comprobar y respetar cambios del usuario. Antes de cambiar infraestructura, volver a verificar coste y condiciones. Crear worktree solo si hace falta y con las herramientas disponibles.
3. Para lógica de riesgo, definir prueba que falle por el comportamiento ausente, comprobar fallo pertinente, implementar y comprobar que pase. Para configuración o cambios de bajo riesgo usar comprobación apropiada; evitar pruebas que solo repitan la implementación.
4. Ejecutar pruebas aplicables a la capacidad, contrato, seguridad, lint/format y migraciones; actualizar docs/ADR/changelog. Una revisión independiente de otro subagente examina requerimientos, fallos y resultados antes del merge.
5. Abrir PR con problema, alcance, criterios, riesgos y pruebas reales. Commits convencionales `feat:`, `fix:`, `test:`, `docs:`, `chore:` según corresponda. Evitar incluir credenciales, datos reales y binarios no justificados.
6. Integrar solo con checks aprobados y revisión atendida, usando el método GitHub disponible. Si el proveedor GitHub no permite autoaprobar como dueño, adjuntar revisión independiente real sin fingir una aprobación formal. No hacer merge mientras checks fallen.
7. Desplegar demo tras integración solo si el preflight de cuenta y coste autoriza ese destino. Comprobar health, acceso autenticado, flujo del hito, datos demo y versión; registrar URL, evidencias y procedimiento de rollback. Si despliegue o pruebas fallan, no etiquetar entrega como completa.

## 7. Estrategia de ramas, versiones y CI/CD

- `main` conserva una versión revisada y reproducible; ramas cortas `feat/<tema>`, `fix/<tema>`, `docs/<tema>`; PR por cambio. Proteger con checks si la configuración y plan GitHub lo permiten. No forzar historial ni reescribir el legado.
- Versionado semántico: `0.x` durante construcción, `1.0.0` para demo completa con DoD; etiquetas solo después de verificación y consentimiento sobre acciones externas cuando corresponda. `CHANGELOG` generado/actualizado por entregable, no prometer releases automáticos antes del preflight.
- CI por PR: compilación backend/cliente, formato y análisis, unitarias, integración PostGIS en contenedor, contrato OpenAPI, seguridad y escaneo de secretos/dependencias; E2E crítico con proveedores simulados. Fallos visibles y sin secretos expuestos a contribuciones externas.
- CD solo desde commit revisado, con permisos mínimos y control de coste; pruebas de humo de login, lectura protegida, creación y mapa/lista. Degradación de rutas/fotos no debe divulgar datos. No usar monitor de 5 min como keepalive encubierto.
- Migraciones compatibles hacia atrás cuando sea posible. Rollback de versión debe comprobar compatibilidad del esquema; ante migración destructiva documentar restauración y pérdida potencial antes de ejecutar. Mantener registro de versión y artefactos reproducibles.

## 8. Estrategia de pruebas

La [matriz detallada](04-plan-pruebas.md) define pruebas por riesgo y objetivo: dominio unitario, integración PostGIS, contrato y autorización, E2E de flujos, seguridad, offline en dispositivo físico, accesibilidad, carga y recuperación. Se busca **≥80 % de líneas del dominio propio** como meta inicial, con excepciones justificadas y sin convertir el porcentaje en sustituto de pruebas de comportamiento.

Pruebas destructivas o de recuperación se ejecutan en recursos aislados de demo con datos sintéticos; no introducir fallos deliberados sobre usuarios reales. Los tiempos/objetivos son propuestas aprobadas condicionadas a medición: 95 % de al menos 100 alertas elegibles ≤10 s con 10 usuarios y servicios activos, consultas p95 ≤2 s con 10.000 alertas y 10 usuarios, y restauración objetivo RPO 24 h/RTO 4 h. Medir arranque en frío y fallos aparte. Si no se cumple, registrar evidencia y solicitar decisión, no relajar el umbral silenciosamente.

## 9. Definición de terminado

Una capacidad se considera terminada cuando:

- Cumple los criterios aplicables de requisitos aprobados, con estados de error y controles de cuenta/rol; no hay contradicciones entre mapa, publicación y datos sincronizados.
- Pruebas relevantes y CI pasan con evidencia reciente; decisiones de cobertura, accesibilidad y excepciones están registradas. No se afirma verificación por inspección sola.
- Seguridad, acceso a fotos, retención y privacidad relevantes revisados; logs sin secretos ni trayectorias innecesarias, acciones administrativas auditadas.
- Revisión independiente completada y observaciones atendidas; PR describe el cambio y sus pruebas; documentación y contrato actualizados.
- La versión se puede reproducir localmente, desplegar en demo solo con coste y permisos validados, y retroceder con un procedimiento proporcionado al cambio. La restauración completa se exige en H7/H8, no se presume en H1.
- No se confunden datos sintéticos con información real; limitaciones de disponibilidad, antigüedad y falta de validación de campo se muestran de forma clara.

Para **demo completa**, además: todos los hitos obligatorios terminados; enlace accesible para registro; flujos protegidos y cuentas de demostración sin privilegios excesivos; pruebas en Android 14 y portátil; ruta, fotos, offline, recuperación, moderación y métricas demostrables; README, manual, ADR, contrato validado, guía de despliegue y material de portafolio; respaldo/recuperación ensayados o excepción aprobada explícitamente. La retención de backups de 30 días es un requisito aceptado separado de las metas condicionadas RPO/RTO; tampoco se descarta sin decisión del propietario. El proyecto no se presenta como servicio vial 24/7.

## 10. Riesgos y dependencia de decisiones

| Riesgo | Tratamiento y condición de avance |
|---|---|
| Render Free advierte no usarlo en producción y puede cobrar excesos. | Alinear destino a demo; verificar límites y bloqueo de cargos antes de cuentas/CD. Si no hay alternativa, informar y pedir decisión. |
| Backups gratuitos no resuelven BD + objetos ni retención. | Ensayar copia cifrada y restauración independiente; si no es viable a COP 0, no declarar RNF-13 cumplido. |
| Acceso offline y suspensión remota | Plazo aceptado de 7 días; demostrar aislamiento de cuentas, borradores y explicación de riesgo residual. |
| Sesión y fotos privadas con links | Validar permisos por servidor y acceso directo; firma ordinaria no acredita revocación o caducidad. |
| Claves históricas y assets | Determinar titularidad/licencias y plan de revocación; nunca publicar valores nuevos ni reescribir historia sin autorización. |
| Proveedor de rutas y cuotas | Manejar error e indicar fallback; validar costos diarios y no afirmar aptitud de carga. |
| Idempotencia duradera vs eliminación | Resolver retención de claves/marcadores y reintentos viejos antes de H5 sin perder borradores o resucitar alertas. |
| Falta validación con transportistas | Explicitar limitación y validar técnicamente con escenarios sintéticos, sin afirmar adopción. |

## 11. Checkpoint de Fase 4

**Aprobada expresamente por JhanDev.** H0 iniciado; mantener revisiones independientes y entrega iterativa. La aprobación no acepta cargos ni garantiza que proveedores mantengan condiciones.
