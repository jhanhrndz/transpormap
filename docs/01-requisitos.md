# TransporMap — Requisitos de producto

**Estado:** Fase 2 aprobada expresamente por JhanDev, incluidas las siete primeras respuestas y las cinco decisiones finales de la sección 9. Este documento es la fuente de verdad de requisitos; las hipótesis y detalles diferidos siguen identificados y cualquier modificación de alcance requiere revisión del propietario. **Propietario:** JhanDev. **Entrada:** [Fase 1 aprobada](descubrimiento/02-resumen-fase-1.md), [decisiones y pendientes](descubrimiento/01-decisiones-y-pendientes.md) e [informe de legado](00-informe-reconocimiento.md).

Los requisitos marcados **[A]** expresan necesidades y restricciones aceptadas por el propietario; sus detalles verificables pueden incluir parámetros que requieren concreción en el diseño. **[P]** identifica una propuesta técnica o detalle aún por concretar, no una decisión de stack aprobada. **[V]** significa capacidad deseada condicionada a viabilidad gratuita. Ninguna marca implica implementación o pruebas existentes. Las decisiones expresas más recientes se registran en la sección 9 y prevalecen sobre redacción histórica de propuestas.

## 1. Visión y resultado

TransporMap permitirá a transportistas registrados compartir y consultar alertas geolocalizadas sobre condiciones y eventos en las carreteras de Colombia, con mapa, publicaciones y señales comunitarias de vigencia. El diseño favorecerá la consulta previa al viaje y el reporte con el conductor detenido o por un acompañante. JhanDev administrará inicialmente los reportes de abuso y el servicio.

**Continuidad con el proyecto original:** este es el proyecto TransporMap existente, objeto de refactorización y mejora profunda para terminarlo, no una idea sin antecedentes. Se conservan como punto de partida su premisa, flujos, pantallas, campos, catálogos y modelo recuperable del código. Su evolución de Swing/Oracle a un cliente por decidir y backend Spring Boot puede requerir reconstruir componentes; las decisiones se justificarán en función del legado y de los requisitos aceptados, sin presentar como operativa funcionalidad que nunca llegó a completarse. La BD y sus datos no están disponibles.

### 1.1 Trazabilidad inicial del legado [A/P]

La tabla liga evidencia existente a necesidades actuales. **A** identifica continuidad solicitada; cada adaptación concreta es **propuesta pendiente de validación**, no decisión irreversible de diseño.

| Legado y evidencia | Continuidad y propuesta para requisitos |
|---|---|
| `Login`, `Signup`, `Usuario`, `Rol`, `Municipio`; nombre, apellido, correo, teléfono, fecha de nacimiento, municipio, rol y contraseña (`src/view/Signup.java:23`, `:52`; `src/model/Usuario.java:7`). | Conservar registro/inicio, identidad y perfil de usuario como funciones. Perfil, alias y datos mínimos se especifican para revisión; no trasladar automáticamente la obligatoriedad de teléfono o nacimiento sin finalidad. Rol transportista del legado se toma como referencia; permisos administrativos son una ampliación. |
| `Main`, `PanelMapa`, `Mapa` y botones de perfil, mapa, novedades, consulta e INVIAS (`src/view/Main.java:176`, `src/view/PanelMapa.java:23`). | Conservar la intención de navegación entre mapa, publicaciones, consulta, perfil y fuentes oficiales; rediseñar su interacción para la plataforma elegida. Perfil, novedades y consulta estaban incompletos; no se describen como funciones ya terminadas. |
| `DenunciaView`: título, descripción, evento, carretera, coordenadas y puntos A/B (`src/view/DenunciaView.java:49`, `:95`, `:145`, `:272`, `:315`). | Título, descripción, tipo y ubicación son punto de partida del reporte. La selección A/B del legado describe un tramo de denuncia, no demuestra un buscador de rutas: el requisito de consulta de ruta A–B es una ampliación. Carretera y extremos se evalúan como datos geográficos del incidente; campos exactos y obligatoriedad requieren aprobación. El título era obligatorio pero no se guardaba. |
| `Denuncia`, `Lugar_Denuncia`, `Tramo`, `Carretera`, `Evento`, `Estado_Via`, fecha e IDs (`src/model/Denuncia.java:7`, `src/model/DenunciaDAO.java:19`). | Reutilizar vocabulario y relaciones como referencia para nuevo modelo, separando hecho, posición, tramo y estado cuando aporten valor. La BD y restricciones físicas se desconocen; no se afirma migración de datos ni equivalencia automática de categorías. |
| Catálogo de 14 eventos y 11 rutas de la vista (`src/view/DenunciaView.java:272`, `:315`). | Conservar el listado como insumo para revisar la taxonomía acordada, incluyendo fenómenos o restricciones no cubiertos; no tratar una lista fija parcial como catálogo oficial colombiano. La tabla de correspondencia queda pendiente. |
| `Calificacion` y `PanelNovedades` (`src/model/Calificacion.java:5`, `src/view/PanelNovedades.java:19`). | Calificación es un modelo aislado y novedades una maqueta; confirmaciones comunitarias y publicaciones reales se desarrollarán como ampliación, no como funcionalidad heredada ya terminada. |
| JXMapViewer y web INVIAS embebida (`src/mapa/Mapa.java:43`, `src/view/WebGobierno.java:32`). | Conservar la necesidad de mapa y consulta de información vial como referencia. Tecnologías y fuente oficial se compararán y verificarán en Fase 3; no asumir que el navegador comercial es reutilizable. |

No se copiarán como reglas deseadas los defectos hallados en el legado —entre ellos el formulario que nunca inserta la denuncia, pérdida del usuario y credenciales incrustadas—. Esta sección no ordena borrar, reescribir historial o descartar recursos; cada decisión técnica requiere justificación posterior.

El resultado es un producto completo respecto al alcance aprobado, públicamente accesible para registro y demostración, con alertas accesibles solo tras iniciar sesión. Servirá para demostrar dominio de Java/Spring Boot y desarrollo profesional de extremo a extremo. **Público** describe acceso al producto, no lectura anónima de alertas. Inicialmente no será comercial.

**Éxito verificable propuesto [P]:** un visitante puede registrarse, iniciar sesión, crear una alerta con una foto opcional, verla en mapa y publicaciones, confirmarla desde otra cuenta, consultar por ruta y cercanía, y repetir creación sin red con posterior sincronización. Un administrador atiende un reporte de abuso y consulta métricas. Todo ello sobre el despliegue aprobado, con pruebas y documentación. Esto demuestra funcionamiento, no validación de campo ni seguridad de una vía.

## 2. Alcance, actores y límites

### 2.1 Incluido [A]

- Registro, inicio de sesión y gestión básica de cuenta para consulta y contribución; administración con permisos separados.
- Alertas en mapa y lista de publicaciones, sin comentarios. Cada publicación corresponde a una alerta.
- Ubicación del incidente, categoría, descripción, fecha y evidencia fotográfica opcional; catálogo aceptado: mal estado, cierre total, cierre parcial, accidente, derrumbe/inundación, obras y restricción de carga. Severidad e impacto requieren definición detallada.
- Consultas mediante ruta entre origen y destino, y por cercanía cuando no haya ruta. Zonas de interés para limitar avisos.
- Confirmaciones modificables «Sigue vigente» / «Ya no está», vigencia y visibilidad de antigüedad; sin presentar votos como verificación objetiva.
- Consulta offline de alertas previamente guardadas y preparación de reportes con fotos pendientes; sincronización al recuperar conexión cuando el cliente pueda ejecutarla.
- Moderación por JhanDev, denuncias de abuso, retirada de contenido y suspensión de cuentas con registro de motivo; panel de métricas.
- Protección de datos, accesibilidad, pruebas, observabilidad, documentación, CI/CD y despliegue gratuito según la planificación a aprobar.

### 2.2 Condicionado / fuera de alcance

- **[V]** Avisos con la aplicación cerrada, supeditados a viabilidad gratuita, plataforma, permisos y condiciones de entrega. No se promete entrega universal o en ausencia de red.
- **[P]** Mapas regionales descargables, navegación giro a giro, voz como medio de reporte, seguimiento continuo, chat y comentarios quedan fuera de la primera versión completa propuesta. La ruta A–B para consultar alertas sí está incluida.
- **[P]** La integración automática de fuentes viales oficiales no se considera aprobada: sí se investigará viabilidad, cobertura, actualización y licencia y se podrán enlazar fuentes verificadas si se aprueba. No suponer API abierta ni licencia reutilizable.
- No se sustituye información oficial ni se certifica seguridad o aptitud de una vía para un camión concreto. No se promete cobertura en tiempo real de todo Colombia.

### 2.3 Actores

| Actor | Permisos y límites |
|---|---|
| Visitante | Acceder a presentación, registro, inicio y políticas; no leer contenido de alertas. |
| Usuario autenticado | Consultar, publicar, confirmar, reportar abuso, gestionar perfil y preferencias; consultar alertas autorizadas guardadas offline bajo política aceptada. |
| Administrador | Permisos de usuario y operaciones administrativas autorizadas. JhanDev inicia como responsable. |
| Servicio de mapas/rutas o imágenes | Dependencias externas sujetas a selección posterior, disponibilidad y condiciones de tratamiento. |

**Supuestos:** uso de datos sintéticos identificados para la demo; no hay transportistas ni corredor confirmado para validación real; una conexión eventualmente disponible permite transmitir colas. Un servicio externo caído o sin cuota debe mostrarse como no disponible. No se autoriza gasto ni registro de tarjeta por defecto; se priorizan servicios sin tarjeta y sin cargos automáticos, y cualquier excepción se presenta para decisión expresa.

## 3. Reglas de negocio propuestas para revisión

Las siguientes reglas completan lo aceptado en Fase 1; **[P] no implica que JhanDev ya las haya aprobado**.

**RB-01 Identidad y permisos [A/P].** Consultar, publicar y confirmar requiere cuenta y sesión. El propietario aceptó permitir consulta offline durante **7 días** desde la última validación en línea, con revocación diferida sin conexión. Sin sesión previa, tras cierre explícito o plazo vencido, no se muestran alertas guardadas ni se crean o editan borradores hasta renovar la autorización. La suspensión o revocación durante desconexión solo se conocerá al reconectar; no se garantiza revocación inmediata offline. Caché, fotos y colas deben aislarse por cuenta, sin acceso o envío por otra cuenta. El tratamiento de pendientes al cerrar sesión se aprobó en la sección 9; los detalles de cuenta suspendida quedan por concretar.

**RB-02 Categoría, impacto y severidad [A/P].** Mantener las siete opciones acordadas como categorías iniciales; registrar por separado impacto vial (abierta, paso parcial, cierre total o desconocido) y severidad (baja, media, alta o desconocida). El usuario puede indicar «desconocido» para evitar precisión ficticia. La relación entre categorías de cierre e impacto deberá ser consistente, sin inferir gravedad solo del nombre.

**RB-03 Campos [P].** Una alerta requiere categoría, punto geográfico, fecha/hora de observación y descripción de 10–500 caracteres; título de 5–100 caracteres y severidad/impacto explícitos o desconocidos. Registrar fecha de recepción por separado. El usuario puede elegir ubicación manualmente si no otorga GPS. No se requiere ubicación continua.

**RB-04 Estados [P].** La vigencia de una alerta puede ser activa, vencida o cerrada por autor/administrador; su visibilidad se gestiona por separado como visible o retirada por moderación. Una alerta retirada puede representar un incidente que sigue activo. Pendiente local, enviando y error son estados de sincronización, no visibilidad pública. Una retirada queda inaccesible en consultas ordinarias tras la siguiente sincronización; puede conservar registro administrativo según retención aprobada. Una alerta vencida no se presenta como activa.

**RB-05 Vigencia [A/P].** Caducidad inicial aceptada: 24 h para accidente, derrumbe/inundación y cierre; 72 h para obras, restricción y mal estado. Son valores iniciales de producto, no evidencia vial. El autor o administrador puede cerrar antes; las confirmaciones no cierran ni extienden automáticamente la vigencia. Como propuesta adicional, la reapertura requerirá nueva alerta, para no ocultar antigüedad. Una recepción tardía conserva hora del hecho y puede entrar vencida. Un reloj adelantado más de cinco minutos se trata según la regla de hora aceptada en sección 9.

**RB-06 Confirmaciones [A].** Una respuesta vigente por cuenta y alerta, modificable; mostrar conteos separados y fecha de confirmación más reciente. Repetir la misma acción no duplica el voto. Ningún número de confirmaciones garantiza veracidad, cierra o valida automáticamente una alerta. No se permiten confirmaciones nuevas ni modificaciones en alertas retiradas, vencidas o cerradas ni auto-confirmación del autor; el usuario puede retirar su respuesta previa incluso después del cierre, según lo aceptado en la sección 9.

**RB-07 Fotos [A/P].** El propietario aceptó un máximo de **una imagen por alerta** y **5 MB** antes de procesamiento; JPEG/PNG/WebP y dimensiones verificadas permanecen como propuesta. Retirar metadatos personales innecesarios de originales y copias antes de distribución y verificarlo; permitir denunciar imágenes que expongan personas o placas. No cargar archivos ejecutables ni claves de proveedores en el cliente. Está aceptado publicar el texto si falla la imagen y mostrarla como pendiente con reintento; la alerta debe seguir siendo identificable sin foto. Subir y asociar archivos requiere cuenta activa, autorización sobre el reporte y comprobación de que no ha sido retirado; distinguir error recuperable de rechazo definitivo. Definir limpieza de huérfanos, borrado de derivados y caducidad de accesos sin exponer enlaces permanentes anónimos.

**RB-08 Rutas y cercanía [A/P].** La ruta conecta A–B mediante una geometría elegida; se aceptó como punto de partida un margen configurable de **1 km** de dicha geometría. Si no existe o falla la ruta, ofrecer cercanía con ubicación autorizada o punto manual, radio inicial aceptado de **20 km**, e indicar cambio de criterio. Estos valores no acreditan precisión ni seguridad y están sujetos a pruebas. Como propuesta, no se requiere cálculo de ruta nueva offline; se señalará la antigüedad de una ruta previamente guardada.

**RB-09 Datos e idioma [A/P].** Alias y punto de incidente visibles solo para cuentas autorizadas; correo, teléfono y trayectorias no públicos. No exigir teléfono, fecha de nacimiento ni documento para operar salvo necesidad justificada. Interfaz y manual en español; README en español con resumen en inglés.

## 4. Historias de usuario y criterios de aceptación

Los criterios describen comportamiento objetivo, no evidencia de que exista. Las reglas con cifras propuestas dependen de aprobación.

### HU-01 Registro e inicio de sesión [A/P]

Como transportista quiero registrarme e iniciar sesión para acceder a alertas y contribuir.

1. Un visitante sin sesión no puede leer alertas, fotos o detalles mediante interfaz, enlaces directos ni API; recibe respuesta de autorización adecuada.
2. El registro solicita solo información necesaria y presenta la política de tratamiento y la autorización cuando corresponda; identifica errores sin exponer datos de otras cuentas.
3. Con credenciales válidas se inicia sesión y con inválidas no se conceden permisos. El usuario puede cerrar sesión y solicitar recuperación sin revelar si un correo existe.
4. La lectura offline solo procede si se cumple RB-01; una cuenta suspendida se bloquea al comprobarla en conexión. La limitación de revocación offline es visible en documentación.

### HU-02 Cuenta y privacidad [A/P]

Como usuario quiero gestionar mi alias y mis datos para controlar mi identidad y solicitudes.

1. Puedo consultar y editar los campos permitidos; correo y teléfono nunca aparecen en publicaciones de otro usuario.
2. Puedo acceder a una política que identifique responsable, fines, canales y derechos; existe un mecanismo documentado para solicitud de consulta, corrección y eliminación cuando corresponda.
3. Tras cerrar sesión, la interfaz oculta alertas guardadas; las decisiones sobre conservar o eliminar borradores y fotos locales se comunican antes de descartar datos.

### HU-03 Crear alerta en línea [A/P]

Como usuario detenido quiero reportar un incidente para que otros estén informados.

1. Selecciono categoría, impacto, severidad o desconocido, ubicación, momento, título y descripción; puedo adjuntar una foto opcional. Se verifican campos y límites antes de enviar.
2. El sistema confirma publicación solo tras recibir aceptación del servidor e identifica la alerta de manera única. Un reintento después de corte no crea dos alertas.
3. La alerta aparece en mapa y publicaciones con categoría, fecha de observación, estado y alias; queda sujeta a vigencia y moderación. El título/fecha no se pierden como ocurría en el legado.
4. La interfaz invita a reportar detenido; no exige interacción durante la conducción. La aplicación no afirma detectar de forma fiable que el vehículo está detenido.

### HU-04 Crear y recuperar reporte offline [A/P]

Como usuario con sesión válida quiero preparar una alerta sin señal para enviarla después.

1. Sin red puedo crear un borrador con foto opcional y guardarlo en el dispositivo; se muestra como pendiente y nunca como publicado.
2. El borrador y foto guardados sobreviven al cierre y reapertura en el entorno soportado mientras el sistema no borre datos locales; si el almacenamiento es insuficiente o falla, se informa sin señalar éxito.
3. Al reabrir con red y sesión aceptada, puedo sincronizar; se informa progreso, éxito o motivo recuperable. Los reintentos y reanudación no duplican reporte ni adjunto.
4. Si la sesión expiró o la cuenta fue suspendida, no se transmite; se muestra el paso requerido o rechazo, sin atribuir el borrador a otra cuenta.
5. La hora de observación original y de recepción se distinguen; si ya venció según RB-05 no se muestra como incidente vigente.

### HU-05 Consultar mapa y publicaciones [A/P]

Como usuario autenticado quiero ver alertas en mapa y lista para entender el estado informado de una zona.

1. Cada vista muestra la misma alerta con identificador y estado coherentes, tipo, impacto, severidad, alias, ubicación y horas pertinentes.
2. Puedo filtrar por categoría, severidad, vigencia y zona; el estado de filtros y ausencia de resultados es visible.
3. Sin red puedo consultar los datos previamente sincronizados autorizados sin descargar mapa regional; se muestra última sincronización y que pueden estar desactualizados. Si no hay datos guardados o venció acceso local, se explica.
4. Una foto no previamente guardada puede no estar disponible offline y debe indicarse; no se promete almacenamiento anticipado de todas las imágenes.

### HU-06 Consultar ruta o cercanía [A/P]

Como usuario quiero consultar alertas relevantes para un trayecto A–B y, cuando no haya ruta, por cercanía.

1. Con ruta disponible puedo seleccionar origen y destino y consultar alertas cercanas a la geometría elegida, aplicando margen configurado y filtros; la visualización explica el criterio.
2. Si no se puede obtener ruta o no hay ruta seleccionada, se ofrece cercanía mediante ubicación autorizada o punto manual; el usuario conoce el radio y que cambió el criterio.
3. Si faltan red, permiso, ubicación o datos para ruta/cercanía, se explica la limitación; nunca se presentan como resultado completo alertas que no se pudieron consultar.
4. Los resultados no se presentan como prueba de seguridad, ruta libre o aptitud para carga; el servicio no proporciona navegación giro a giro.

### HU-07 Confirmar vigencia y reportar abuso [A/P]

Como usuario quiero indicar si una alerta sigue vigente y reportar contenido indebido.

1. Puedo registrar y modificar una de las dos confirmaciones; los conteos y fecha reciente se actualizan sin duplicar contribuciones por reintentos.
2. Puedo informar abuso con motivo; recibo confirmación sin exponer el informe a otros usuarios ordinarios.
3. Las confirmaciones no marcan la alerta como verdadera, y el cierre/expiración/retirada se muestran por separado.

### HU-08 Recibir novedades [A/P/V]

Como usuario quiero conocer nuevas alertas relevantes para mi zona.

1. Puedo seleccionar o modificar zonas de interés y desactivar avisos; no se requiere rastreo continuo.
2. Con cliente abierto, conexión y servicios disponibles, los nuevos reportes elegibles aparecen conforme al objetivo de RNF-01; tras reconexión se ponen al día sin duplicados.
3. Los avisos con cliente cerrado son deseables [V], sujetos a permisos y viabilidad de la plataforma/proveedor. Rechazar permisos no impide consultar al abrir la aplicación; no se promete entrega ni orden universal.

### HU-09 Moderación [A/P]

Como administrador quiero revisar abuso y actuar con trazabilidad.

1. Veo cola de informes pendientes y resueltos y el contenido necesario para decidir; las cuentas ordinarias no acceden a ella.
2. Puedo retirar o restaurar una publicación, cerrar una alerta y suspender o rehabilitar una cuenta; cada acción exige motivo y conserva actor, fecha y tipo de acción. Restauración/rehabilitación son propuestas.
3. Una retirada o suspensión se aplica en línea y se refleja en clientes al sincronizar; una copia offline puede permanecer hasta reconexión o fin del acceso local RB-01.
4. Las operaciones rechazan solicitudes sin rol, incluso si se invoca directamente el servicio. Los usuarios pueden reportar sin exponer sus datos a otros.
5. Un reintento tardío de reporte, foto o confirmación no restaura ni hace visible una alerta retirada. La suspensión se vuelve a comprobar antes de aceptar una operación sincronizada.

### HU-10 Métricas administrativas [A/P]

Como administrador quiero conocer el estado de producto y operación para atender problemas.

1. Visualizo conteos por tipo/estado/periodo, alertas de abuso pendientes y resueltos, errores, latencia y última actualización de cada métrica disponible.
2. Se identifican datos de demostración; no se publican trayectorias ni información personal innecesaria.
3. Si una métrica del proveedor o cuota no está disponible, se indica «no disponible»; no se inventan cifras ni se confunde notificación enviada con recibida.

### HU-11 Cierre, expiración e histórico [A/P]

Como usuario quiero distinguir alertas activas de eventos anteriores.

1. Una alerta caduca según la regla aprobada y muestra su estado y tiempo de observación; puede consultarse en histórico según retención.
2. Autor y administrador pueden cerrar antes de vencimiento; la aplicación refleja la causa de cierre sin confundirla con moderación.
3. La lista histórica permite filtrar por periodo, zona y categoría; estadísticas agregadas básicas respetan permisos y privacidad.

## 5. Casos de uso principales

| Caso | Precondición | Flujo y resultado | Excepciones |
|---|---|---|---|
| CU-01 Acceso | Cuenta activa y conexión; o sesión local válida para caché | Autenticar y consultar alertas permitidas. | Credenciales inválidas, suspensión o sesión offline vencida: acceso denegado y explicación. |
| CU-02 Crear alerta | Usuario autorizado, datos válidos | Registrar alerta y foto opcional; confirmar ID y mostrar en mapa/lista. | Red o foto falla: pendiente/error explícito; ninguna duplicación por reintentos. |
| CU-03 Ruta/cercanía | Sesión y datos disponibles | Elegir A/B, seleccionar trayecto y ver incidentes próximos; alternativa por cercanía. | Ruta, red o ubicación ausentes: explicar y ofrecer alternativa según datos disponibles. |
| CU-04 Sincronizar | Borrador asociado a sesión local y conectividad recuperada | Renovar/verificar sesión, transmitir y confirmar cada elemento. | Rechazo o suspensión: conservar error recuperable cuando corresponda, no cambiar autor ni aparentar éxito. |
| CU-05 Moderar | Administrador autorizado y reporte de abuso | Revisar, actuar con motivo y guardar auditoría. | Sin permisos, servicio caído o acción inválida: no aplicar y comunicar resultado. |

## 6. Requisitos no funcionales y medición

Todas las cifras siguientes son **propuestas de aceptación**, no promesas de capacidad ni pruebas realizadas. Se medirá en un entorno, dataset y proveedor documentados; los umbrales se revisarán tras seleccionar infraestructura gratuita.

| ID | Requisito y condición medible propuesta | Evidencia |
|---|---|---|
| RNF-01 Actualización | Con servicio activo, dos clientes autenticados conectados y carga de 10 usuarios concurrentes, 95 % de al menos 100 alertas elegibles aceptadas visibles en el segundo cliente ≤10 s desde aceptación; medir todos los eventos aceptados, contando los no recibidos como fallos del umbral, no excluirlos. No incluye arranque en frío ni push con app cerrada. | Prueba E2E con reloj de medición común, recuento de aceptados, recibidos y fallos, tiempo máximo y percentiles de los recibidos. |
| RNF-02 Lectura | Con 10.000 alertas de prueba y 10 usuarios concurrentes, p95 de consultas de lista/mapa ≤2 s en servidor activo; especificar límites de respuesta y red. Ruta de terceros se mide por separado. | Prueba de carga reproducible; registrar base, entorno y dataset. |
| RNF-03 Offline | 20 reportes con cortes repetidos, incluidos 5 con foto, persisten tras reinicio de aplicación y, al recuperar conectividad, cada uno queda aceptado una sola vez o en error recuperable explícito. No simular desalojo como imposible. | E2E en dispositivo objetivo y pruebas de sincronización. |
| RNF-04 Seguridad | 100 % de endpoints de datos de alertas y mutaciones protegidas verifican sesión; registro, inicio y recuperación son excepciones públicas con controles apropiados. Todas las acciones administrativas comprueban rol en servidor; HTTPS en despliegue; secretos fuera de repositorio; autenticación segura y contraseñas con hash adaptativo. | Tests automatizados de acceso directo/roles, revisión configuración y escaneo secretos. |
| RNF-05 Privacidad | 0 correo/teléfono en respuestas a otros usuarios; 0 coordenadas EXIF/IPTC/XMP en originales distribuidos y versiones derivadas de un conjunto de fotos de prueba; revisar borradores locales y eliminación. | Tests de contrato y análisis de archivos de prueba; política de tratamiento revisada. |
| RNF-06 Accesibilidad | Flujos críticos operables sin depender solo de color; etiquetas, foco visible, errores comprensibles y teclado cuando aplique; objetivo WCAG 2.2 AA para interfaces soportadas. | Auditoría automática y manual de flujos en móvil y escritorio; registrar excepciones. |
| RNF-07 Resiliencia | 100 % de las pruebas de fallo de red, falta de cuota de imágenes y ruta externa caída muestran estado y alternativa apropiada; sin confirmación falsa. | Pruebas de inyección de fallos para casos definidos. |
| RNF-08 Observabilidad | Health check, logs estructurados sin contraseñas ni claves, y métricas de errores y latencia; informar último dato y disponibilidad de métricas. | Inspección de logs de prueba, health y panel; sin compromiso de herramienta específica. |
| RNF-09 Calidad | Tests unitarios de reglas críticas, integración con BD real en contenedor, contrato, E2E crítico y seguridad; objetivo inicial ≥80 % de cobertura de líneas para dominio propio, sin tratar cobertura global como garantía. | CI publica informe, resultados y análisis estático; selección final de módulos y exclusiones revisada en plan. |
| RNF-10 Operación | Entorno local reproducible con un comando documentado tras prerrequisitos; CI compila, prueba, analiza y despliega; rollback documentado y ensayado antes de entrega. | Ejecución desde clon limpio y ensayo de recuperación. |
| RNF-11 Costo | Infraestructura seleccionada con gasto recurrente COP 0 para escenario de demostración y sin cargos automáticos autorizados; límites, tarjeta, suspensión y degradación documentados por proveedor. | Verificación de planes vigentes en Fase 3 y ensayo de degradación; no garantiza gratuidad ilimitada. |
| RNF-12 Compatibilidad | Flujos críticos probados en el Tecno Android 14 y portátil Windows indicados por JhanDev, con navegadores/plataformas escogidos; versiones exactas registradas. | Matriz con dispositivo físico, versiones y resultados. No implica soporte iOS hasta probarlo. |
| RNF-13 Recuperación | Propuesta: respaldo diario si la plataforma lo permite; restauración ensayada con RPO ≤24 h y RTO ≤4 h en escenario de demo. La factibilidad gratuita se valida en arquitectura. | Ensayo documentado de recuperación; si no es viable, escalar compromiso al propietario. |

## 7. Datos, seguridad y fuentes

- La BD original se perdió: no hay migración de usuarios o alertas existentes. Se diseñará esquema nuevo y migraciones versionadas tras selección de arquitectura.
- Datos iniciales: catálogo y registros sintéticos identificados, sin personas ni incidentes reales atribuidos como hechos. Separar y poder retirar demostraciones. La red vial y fuentes oficiales requieren evaluación de acceso, cobertura, frecuencia y licencia.
- Los datos de alertas son accesibles solo a usuarios autenticados; las fotos y archivos entregados deben respetar la misma autorización, incluidos enlaces directos, caché y acceso offline. Detalles de mecanismos y caducidad se resuelven en Fase 3.
- Las consultas de rutas o ubicación se procesan de forma transitoria como propuesta; no conservar trayectorias individuales en registros o métricas por defecto. La retención aceptada consta en sección 9; se deben resolver aplicación y excepciones.
- La [Ley 1581 de 2012](https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=49981) regula derechos y tratamiento; el [Decreto 1377 de 2013](https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=53646) desarrolla autorización, políticas y responsabilidades. Debe definirse responsable y canal, finalidad, autorizaciones aplicables, derechos, retención, transferencias y procedimientos; esta lista no acredita cumplimiento jurídico.
- El [servicio estándar de teselas OSM](https://operations.osmfoundation.org/policies/tiles/) prohíbe descarga masiva de regiones para uso offline. El alcance actual no exige descarga regional de mapas; la política de cualquier otro proveedor debe verificarse antes de usarlo.
- Amenazas prioritarias: falsificación de reportes, votos coordinados, spam, apropiación de cuentas, acceso directo no autorizado a imágenes, fuga de ubicación, abuso de cuotas, cargas maliciosas y errores de sincronización. El modelado y controles específicos se concretarán en Fases 3–4.
- Los periodos de retención se aceptaron en sección 9; quedan por concretar aplicación, excepciones y tratamiento de derivados, solicitudes y copias. No presumir obligaciones o plazos legales específicos sin validación. También decidir licencia del código propio y verificar procedencia y licencias de imágenes y artefactos del legado antes de redistribuirlos.

## 8. Riesgos y decisiones por validar

| Riesgo | Consecuencia | Mitigación candidata |
|---|---|---|
| Sin transportistas ni corredor de prueba identificados | No hay validación real de necesidades ni adopción. | Etiquetar datos sintéticos; documentar limitación y buscar participantes cuando sea posible. |
| Sin conexión y sesión suspendida | Cliente no recibe revocación inmediatamente. | Límite local RB-01, ocultar al cerrar sesión y revalidar al reconectar; aprobar riesgo residual. |
| Ruta no apta para camión | Usuario puede creer que vía es transitable. | Explicar que ruta y alertas son informativas, indicar fuente/antigüedad y no prometer aptitud. |
| Fuente gratuita cambia o se suspende | Imágenes, mapas, rutas o backend dejan de responder. | Vigilar cuotas, degradación, alternativas y backups; comparar planes actualizados. |
| Reportes tardíos o falsos | Información caducada o engañosa. | Hora observada y recibida, caducidad, confirmaciones separadas, moderación y reglas contra abuso. |
| Fotos o caché eliminadas del dispositivo | Pérdida de pendientes. | Informar límites de persistencia, espacio y estado; reintentos sin duplicación. |
| Reportes masivos o fotos | Se agotan cuotas y capacidad de moderación individual. | Límites, control de abuso, supervisión y carga de prueba ajustados al escenario de demo. |
| Tiempo de 10 h/semana | Retraso e hitos incompletos. | Roadmap por horas con incertidumbre, iteraciones y alcance definido. |

## 9. Respuestas incorporadas y asuntos pendientes

El propietario aceptó las siete propuestas del primer lote de Fase 2:

1. **Sesión offline:** plazo de 7 días desde validación en línea, con revocación diferida mientras no haya conexión. Los detalles de conservación de colas y borradores se concretarán aparte.
2. **Vigencia:** 24 h para eventos agudos y 72 h para obras, restricciones y mal estado; cierre por autor/admin, sin cierre automático por votos. Son valores iniciales de producto, no evidencia de que un evento haya terminado.
3. **Fotos:** una imagen de hasta 5 MB; publicar texto si falla foto y mostrar imagen pendiente para reintentar. Formatos, protección y limpieza exactos siguen sujetos a revisión.
4. **Ruta y cercanía:** margen inicial configurable de 1 km de la ruta y radio de 20 km para cercanía, sujetos a pruebas.
5. **Campos:** título y descripción obligatorios; el propietario pide usar como punto de partida los campos ya existentes, según la sección 1.1. Límites de caracteres y decisión sobre carretera/tramo siguen como propuestas.
6. **Calidad y operación:** acepta RNF-01/02/09/13 como metas iniciales a validar y revisar si la infraestructura gratuita no las admite, no como resultados demostrados.
7. **Fuentes oficiales:** investigar y enlazar; importación automática fuera de esta versión salvo aprobación posterior tras comprobar licencia y viabilidad.

**Retención aceptada [A]:** histórico de alertas y fotos asociadas **90 días desde su cierre/vencimiento**, reportes de abuso y auditoría **180 días desde resolución**, logs operativos **30 días**, y respaldos **30 días** con rotación; borradores y fotos pendientes se mantienen localmente hasta que el usuario los envíe o descarte, con recordatorio visible pasados **30 días**. Son valores de producto, no plazos legales ni una promesa de eliminación inmediata de medios del proveedor. Debe evaluarse la finalidad, solicitudes de titulares, posibles obligaciones aplicables, tratamiento de copias de seguridad y viabilidad del proveedor antes de publicarlos. La conservación debe limitarse a un tiempo razonable y necesario según la finalidad y normativa aplicable: [Decreto 1377 de 2013](https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=53646).

**Campos del legado aceptados [A]:** punto del incidente obligatorio, carretera y extremos de tramo opcionales, para admitir incidentes puntuales y conservar el tramo cuando sea pertinente. Esto no limita la consulta separada por una ruta A–B.

**Sesión y borradores [A]:** al cerrar sesión, ocultar o borrar la caché de consulta y ofrecer conservar los pendientes aislados de otras cuentas o descartarlos expresamente. Recuperar un pendiente solo al volver a autenticar la misma cuenta; no borrarlo silenciosamente. Tras siete días sin validación, el acceso queda bloqueado hasta reconectar, pero se conservan pendientes según la retención aprobada. La administración requiere conexión.

**Reintento antiguo incierto [A]:** si ya no es posible determinar con certeza si un reporte offline antiguo fue aceptado y su alerta se ha eliminado, conservar el borrador y la foto, no publicar de nuevo automáticamente y solicitar revisión manual explicando la incertidumbre. No crear duplicados silenciosamente ni hacer reaparecer una alerta retirada. La detección y evidencia de ese estado se definirán y probarán técnicamente; la decisión no autoriza conservar identificadores personales indefinidamente.

**Hora [A]:** calcular caducidad desde la fecha de observación; si supera por más de cinco minutos la hora del servidor, rechazar con mensaje corregible y conservar el borrador. No modificar silenciosamente la hora. Esta regla trata hechos observados, no eventos futuros programados; se probarán zonas horarias y envío tardío.

**Confirmaciones [A]:** no permitir que el autor se confirme a sí mismo; impedir crear o modificar confirmaciones en alertas cerradas, vencidas o retiradas; permitir retirar la confirmación propia incluso después del cierre. Mantener conteos separados sin calificar de «verificado» el reporte. Una petición idéntica no duplica voto ni modifica la fecha de la confirmación previa.

**Detalles que se concretarán sin reducir el alcance aprobado:** límites de texto y formatos de imagen; combinaciones categoría/impacto; política de cuenta suspendida y borradores locales; restauración; licencia y procedencia de assets. La propuesta [P] que afecte alcance, compromisos o decisiones del propietario se presentará expresamente para revisión.

## 10. Checkpoint de Fase 2

JhanDev aprobó la Fase 2 tras aceptar las siete respuestas iniciales y las cinco decisiones adicionales. Los detalles técnicos marcados [P], la licencia y procedencia de assets siguen por resolver y se registrarán en arquitectura sin alterar unilateralmente el alcance aprobado. La aprobación de Fase 2 no elige plataforma ni servicios y no autoriza código de producción. Fase 3 comparará alternativas, costos y límites actuales; Fase 4 propondrá roadmap y requerirá aprobación expresa antes de implementar.
