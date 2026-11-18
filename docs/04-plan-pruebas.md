# TransporMap — Plan de pruebas de la demo

**Estado:** propuesta de Fase 4, pendiente de aprobación del plan. No se han ejecutado estas pruebas. Base: [requisitos](01-requisitos.md) y [roadmap](03-plan-y-roadmap.md). QA estimado 80–130 horas transversales **incluidas** en los hitos del roadmap; no sumarlas otra vez.

## 1. Objetivo y evidencia

Verificar las historias y riesgos de la demo pública de portafolio con datos sintéticos identificados. Las pruebas muestran qué se comprobó en una versión y entorno determinados; no prueban exactitud de información vial ni sustituyen validación con transportistas. Los entornos y cuentas usados para inyectar fallos o restaurar copias deben ser aislados.

Cada ejecución registra commit, versión, fecha, entorno, dispositivo/navegador, datos, resultado, fallos y evidencia. No registrar contraseñas, tokens, fotos personales ni rutas reales de usuarios. Las pruebas automatizadas deben poder repetirse; las manuales señalan pasos, resultado y responsable.

## 2. Niveles y puertas

| Nivel | Qué cubre | Puerta |
|---|---|---|
| Unitario | Vigencia 24/72 h, +5 min, estados, confirmaciones, reglas de categoría y sesión de 7 días con reloj controlado. | Cada PR que modifique dominio. |
| Integración | PostGIS real en contenedor, migraciones, restricciones, concurrencia, idempotencia, autorización, archivos con dobles y limpieza. | Cada PR correspondiente. |
| Contrato/API | Validación OpenAPI 3.1, esquemas, errores, filtros, rutas protegidas, paginación/cursor y roles. | Cada PR; contrato actual es preliminar y debe completarse en H0. |
| E2E | Registro/login, publicación, mapa/lista, ruta/fallback, confirmaciones, offline/foto y moderación en entorno aislado. | Flujos pertinentes por PR; conjunto crítico antes de integrar y desplegar. |
| Seguridad | Acceso directo sin sesión, roles, suspensión, idempotencia, imágenes, escaneo de dependencias y secretos. | CI y revisión independiente. |
| Manual / físico | Accesibilidad, red y almacenamiento en Tecno Android 14 y portátil Windows. | Antes de cierre de hitos afectados y demo final; registrar versión de navegador. |
| Carga / recuperación | Latencia, cuotas, backup/restore, migraciones y rollback. | H7/H8 y cambios de infraestructura; no ejecutar en recursos reales de terceros sin controlar límites. |

Cobertura inicial aceptada como meta: **≥80 % de líneas del dominio propio**; publicar denominador, exclusiones justificadas y resultado. La cobertura no reemplaza las pruebas de seguridad o sincronización. El conjunto de comandos reales se fijará al crear wrappers y scripts; ningún comando propuesto aquí implica ejecución o disponibilidad actuales.

## 3. Matriz de requisitos y escenarios

| Requisito | Casos que deben pasar | Evidencia y fase |
|---|---|---|
| HU-01/02, RB-01/09, RNF-04/05 | Visitante no obtiene lista, detalle, mapa, foto ni datos por API; usuario normal no administra; cuenta suspendida es rechazada en línea; registro y recuperación no enumeran cuentas; otro usuario no ve correo/teléfono. | Matriz por rol y recurso, contrato e integración; H1/H4. |
| HU-02/04/05, RB-01 | Sesión previa permite leer caché hasta el plazo local de 7 días; al vencer, bloquea lectura/edición hasta validación. Salir permite conservar o descartar pendientes; otra cuenta no puede leerlos o enviarlos; al reconectar se verifica suspensión. | Reloj controlado, E2E y dispositivo; H5. No afirmar revocación remota inmediata offline. |
| HU-03, RB-02/03 | Crear alerta con título, descripción, categoría, punto, hora, impacto y severidad/desconocido; carretera y tramo opcionales. Título y autor persisten y la alerta tiene misma identidad en mapa/lista. | Unitario, PostGIS y E2E; H1/H2. |
| HU-04, RNF-03 | 20 reportes con cinco fotos, cortes antes/después de aceptación y cierre/reapertura; cada uno se acepta una sola vez o deja error recuperable visible. Probar pérdida de respuesta, sesión expirada, falta de espacio y cambio de cuenta. A los 30 días mostrar recordatorio sin borrar pendientes. | Integración y Android físico; H5. No afirmar que el navegador nunca borra datos. |
| HU-04/09, RB-04 | Retiro de alerta durante reintento o carga de foto no restaura visibilidad; reintento después de caducar clave de transporte y después de retención se maneja sin duplicado o con rechazo explicado conforme al diseño aprobado. Conservar borrador/foto, explicar recuperación y no borrar silenciosamente; rechazo sin alternativa no se considera automáticamente satisfactorio. | Prueba concurrente con BD y archivos aislados; H5/H6. Resolver tensión entre borrador indefinido, deduplicación y eliminación antes de implementar. |
| RB-07, RNF-05/07 | Una imagen máx. 5 MB; validar tipo real y tamaño en servidor. Fallo de foto deja texto aceptado y foto pendiente o rechazada con causa; probar archivo corrupto, cuota, suspensión y retiro durante carga. Original y derivados sin coordenadas EXIF/IPTC/XMP. | Tests de archivos sintéticos y autorización directa; H5/H6. Comprobar proveedor real antes de demo. |
| RB-05/06, HU-07/11 | Con reloj controlado: 24/72 h, fechas observada/recibida, envío tardío y hora futura >5 min rechazada sin mutar borrador. Una respuesta modificable por cuenta, sin autoconfirmación; repetición no cambia fecha; no modificar tras cierre/vencimiento/retiro; permitir retirada propia incluso después del cierre. | Unitario e integración concurrente; H2. |
| HU-06, RB-08 | Incluir/excluir puntos en bordes de 1 km alrededor de geometría completa y 20 km del centro, no solo extremos. GPS denegado → punto manual; ruta caída → alternativa anunciada; falta de datos ≠ cero alertas. | Fixtures espaciales en PostGIS real y proveedor simulado; H3. |
| HU-08, RNF-01 | Zonas de interés y novedades con cliente abierto; reconexión recupera cambios sin duplicar. Cliente cerrado solo si se comprueba viable y sujeto a permisos. | E2E con dos clientes y cursor; H5/H7. Push no bloquea si su viabilidad se descarta explícitamente. |
| HU-09/10 | Administrador atiende abuso y suspende o retira registrando actor/fecha/motivo; acceso directo ordinario rechazado; retorno de retirada separado de estado vial. Métricas con periodo y «no disponible»; datos demo diferenciados. | Integración, E2E y auditoría; H6/H7. |
| RNF-06/12 | En teléfono y portátil, etiquetas, foco visible, teclado cuando aplique, contraste, mensajes y estados sin depender solo de color; invitación a reportar detenido. | Revisión automatizada y manual; H7. No afirmar detección fiable de conducción. |
| RNF-08/10/11/13 | Caída de red/rutas/fotos, cuota y recuperación sin éxito falso. Restaurar BD y objetos cifrados en entorno aislado; comprobar correspondencia, retención y que los borrados no reaparezcan; ensayar rollback con migraciones. | Informe fechado con RPO/RTO observados y costes; H7/H8. No asumir que tier Free da backups. |
| HU-11 y políticas | Históricos/estadísticas; limpieza 90 días desde cierre/vencimiento, auditoría/abuso 180 días desde resolución, logs y respaldos 30 días, recordatorio de borradores a 30 días sin eliminarlos y solicitudes de titulares. El inicio para acciones sin resolución queda por definir. | Reloj controlado y revisión política; H5/H6/H7. Valores no equivalen a aprobación legal. |
| Fuentes oficiales | Comprobar fecha, cobertura y licencia de enlaces propuestos; no presentar importación automática como implementada. | Revisión documentada de fuente primaria; H3. |

## 4. Rendimiento y cargas

**RNF-01 [meta aceptada, no demostrada]:** servicio activo, clientes autenticados, 10 usuarios concurrentes y ≥100 alertas elegibles. Medir del instante de aceptación por servidor a aparición en segundo cliente; ≥95 % en ≤10 s. El denominador incluye todos los aceptados elegibles para el segundo cliente: los no recibidos cuentan como fallos del objetivo. Reportar p95 entre recibidos, máximo, número y proporción de fallos, reloj de medición común y red. Medir cold start y avisos con aplicación cerrada por separado.

**RNF-02 [meta aceptada, no demostrada]:** 10.000 alertas sintéticas y 10 usuarios; p95 de consultas lista/mapa ≤2 s con servidor activo. Registrar distribución de datos, paginación, límites de respuesta, latencia de red y entorno. Medir servicio de rutas externo por separado. Acordar la tasa exacta de solicitudes antes de la prueba para que sea reproducible y no exceda cuotas de terceros.

**RNF-13 [meta aceptada condicionada]:** respaldos diarios de BD y objetos con retención 30 días y restauración demostrada RPO ≤24 h / RTO ≤4 h. Las condiciones gratuitas de destino, programación, seguridad y recuperación deben comprobarse; si no se alcanzan, informar y solicitar decisión al propietario. No se declara cubierto por una instantánea de 6 h o por una exportación sin fotos.

## 5. Matriz de dispositivo y condiciones

1. Tecno Camon 20 Pro 4G, Android 14 declarado: registrar navegador y versión; probar permisos de cámara y ubicación, selección manual, uso offline, captura de imagen, cierre/reapertura, falta de espacio y sincronización con sesión válida o caducada.
2. Portátil HP con Windows, 24 GB RAM y GTX 1650 Ti declarados: registrar navegador, versión y capacidad; probar interfaz adaptable, teclado, administración, herramientas locales y contenedores tras verificar virtualización y espacio.
3. Controlar fecha/hora y tiempo mediante mecanismos de prueba aislados; no alterar permanentemente datos o configuración del usuario para simular 7 días.
4. Probar sin conexión desde una instalación/sesión previamente validada, sin asumir mapa regional o fotos no guardadas. Repetir cambio de cuenta, cierre y datos previamente retirados.
5. Otras plataformas —incluido iOS— se marcan no verificadas hasta contar con dispositivo y pruebas; no se extrapola compatibilidad.

## 6. Ejecución, revisión y salidas

- **PR:** build, formato, análisis, tests unitarios/dominio, integración PostGIS, contrato, roles y escaneos apropiados; revisión independiente y evidencia. Nunca usar secretos reales en PR no confiable.
- **Antes de desplegar:** verificar condición de coste, cuenta y cuotas; pruebas de humo autenticadas, acceso a foto y gestión de fallos. No activar CD a proveedores con potencial de facturación sin límite verificado.
- **Entrega:** reporte de criterios cumplidos, fallidos y condicionados; resultados de carga, accesibilidad, seguridad y restauración; capturas sin datos personales; documentación y limitaciones de demo.
- Los comandos exactos se documentarán cuando existan los archivos y herramientas; por ejemplo `./mvnw verify` solo después de crear y configurar el wrapper. Un comando previsto no es evidencia de una ejecución.
