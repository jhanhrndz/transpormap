# TransporMap — Informe de reconocimiento

Fecha: 12 de noviembre de 2026. Fase 0, para revisión de JhanDev.

Base examinada: rama `main`, commit `ab8ed7d` (`First commit: Initial upload`, 19 de febrero de 2025). El árbol estaba limpio antes del análisis. Este documento es el único entregable escrito; no se modificó código, configuración, dependencias ni historial.

## 1. Conclusión ejecutiva

TransporMap es una aplicación de escritorio Java/Swing, construida con NetBeans y Ant, con acceso JDBC directo a Oracle. Conserva un comienzo del dominio de alertas viales, pero no un producto funcional completo.

El hallazgo principal es que **el botón de publicar nunca inserta la denuncia**: `DenunciaView.java:457` llama a `captura()`, que prepara una entidad y crea lugar/tramo, pero no llama a `DenunciaBO.addDenuncia`. Si `captura()` devuelve éxito, vuelve a insertar el lugar. Después abre `Main()` incluso si la validación falla. Restaurar solamente la BD no resolvería este flujo.

Registro y login tienen consultas reales, pero requieren catálogos y base de datos perdidos, comparan contraseñas sin hash en el código y contienen errores de identidad y tratamiento de fallos. El mapa dibuja ubicaciones, no alertas completas. Perfil, consultas y novedades no están terminados. No hay pruebas, CI/CD, documentación operativa ni despliegue versionados.

Se recomienda conservar el legado como referencia de dominio y evidencia histórica, y reconstruir el producto tras aprobar requisitos, arquitectura y planificación. Spring Boot es una restricción solicitada por el propietario; ninguna otra tecnología queda elegida por este informe. No se propone mantener Oracle.

## 2. Método, evidencia y límites

- Tres subagentes independientes analizaron pantallas/mapas, datos/negocio y dependencias/seguridad. El coordinador contrastó las consultas, los handlers principales, conteos, Git y configuración. Un cuarto subagente realizó la revisión final independiente del informe; se incorporaron sus precisiones sobre Oracle y datos impresos.
- Se leyeron las 34 fuentes Java y los ocho formularios NetBeans; se inventariaron archivos versionados, recursos, configuración y binarios. Los JAR se inspeccionaron como ZIP/metadatos; MyLicense también mediante desensamblado estático. No se ejecutó código de esos JAR.
- Las referencias `src/...:línea` corresponden al commit base. Los archivos fuente declaran `ISO-8859-1`; las diferencias de visualización de acentos no demuestran por sí solas corrupción del archivo.
- **Confirmado** significa observable en el código o metadatos. **Inferido** indica una interpretación del dominio que no demuestra la estructura física de Oracle. **Pendiente** requiere validación externa o del propietario.
- No se inició la aplicación, no se accedió a Oracle, no se probaron las claves encontradas, no se compiló ni se ejecutaron pruebas. Este es un reconocimiento estático, no una certificación de funcionamiento, seguridad o ausencia de vulnerabilidades.
- No se hizo auditoría del código completo de las bibliotecas de terceros ni comparación exhaustiva de cada `.class` con su fuente. Los compilados versionados no demuestran que estas fuentes compilen hoy.
- No se encontraron instrucciones `AGENTS.md` en el repositorio ni en los ancestros consultados.

## 3. Inventario del repositorio

| Elemento | Cantidad / propósito |
|---|---|
| Archivos versionados antes del informe | 173; 112.492.608 bytes fuera de `.git` |
| Fuentes Java | 34: 12 modelos, 6 DAO, 5 BO, 1 conexión, 9 vistas, 1 mapa |
| Formularios `.form` | 8, asociados a vistas Swing |
| Clases compiladas `.class` | 60, incluyendo clases internas, en `build/classes` |
| Imágenes | 26 originales en `src/images` y 26 copias idénticas por SHA-256 en `build/classes/images` |
| JAR en raíz | 9; aproximadamente 111 MB decimales en conjunto, dominados por Chromium win64 |
| Construcción | `build.xml`, `nbproject/build-impl.xml`, propiedades y XML de NetBeans |
| Manifiesto | `manifest.mf`; NetBeans añade la clase principal durante el build |
| Datos / automatización | No se encontraron DDL, dump, migraciones, datos semilla, tests, Docker, workflows, OpenAPI, README, licencia del proyecto ni `.gitignore` |

### 3.1 Configuración y reproducibilidad

`nbproject/project.properties` declara Java fuente/destino **19**, clase principal **`view.Login`**, codificación **ISO-8859-1** y carpeta de pruebas `test`, que no está presente. No es un proyecto Maven ni Gradle.

La ruta del driver Oracle apunta a `C:\app\sqldeveloper\jdbc\lib\ojdbc11.jar`, aunque existe otro `ojdbc11.jar` en raíz. Las bibliotecas `libs.absolutelayout.classpath` y `libs.CopyLibs.classpath` dependen del entorno NetBeans y no están resueltas mediante un descriptor portable (`nbproject/project.properties:50`, `nbproject/build-impl.xml:1007`). `nbproject/private` y `build/built-jar.properties` conservan rutas locales del equipo original. Los 60 compilados declaran major version 63, correspondiente a Java 19. Los artefactos generados e imágenes duplicadas aumentan ruido y tamaño del repositorio.

El equipo actual dispone de herramientas Java y Git; no se encontró `ant` en PATH durante la consulta. No se instalaron herramientas ni se intentó reparar la construcción.

### 3.2 Paquetes y clases de infraestructura y negocio

| Clase | Responsabilidad y estado |
|---|---|
| `connectionbd.ConnectionOracle` | Abre JDBC hacia Oracle local con configuración incrustada; captura errores y puede devolver `null`. No hay pool ni configuración externa. |
| `controller.CtrlBO` | Envuelve búsquedas genéricas de IDs en conexiones independientes; incluye generador numérico basado en UUID, sin evidencia de uso productivo. |
| `controller.UsuarioBO` | Alta y lectura por ID. Actualización y borrado son esqueletos. |
| `controller.DenunciaBO` | Métodos de alta, actualización, borrado y listado con commit por operación. La UI de alta no llama al método de inserción. |
| `controller.Lugar_DenunciaBO` | Alta y listado de ubicaciones; actualización/borrado sin implementación efectiva. |
| `controller.TramoBO` | Alta de tramo; lectura/actualización/borrado incompletos. |
| `model.CtrlDAO` | SELECT genérico con nombres de tabla/columnas concatenados y valores parametrizados; búsqueda de tramo contiene SQL mal formado. |
| `model.UsuarioDAO` | INSERT y SELECT por ID; lectura del rol usa por error `MUN_ID`. |
| `model.DenunciaDAO` | INSERT, UPDATE, DELETE, SELECT de denuncias. No persiste título ni `den_estado`. |
| `model.Lugar_DenunciaDAO` | INSERT y SELECT de ubicaciones. |
| `model.TramoDAO` | INSERT de tramos. |
| `model.EventoDAO` | Lectura por ID, denominada erróneamente `getUsuario`; otros métodos son esqueletos. |

Las clases no forman un MVC desacoplado: las vistas consultan IDs, construyen entidades y coordinan persistencia, y algunas capas DAO/BO muestran diálogos Swing. No existe backend HTTP, contrato API ni separación entre cliente y servidor.

### 3.3 Pantallas, formularios y mapa

| Clase / formulario | Qué hace realmente | Limitación |
|---|---|---|
| `Bienvenida.java/.form` | Pantalla de entrada y botón que abre Login (`:99`). | No es el punto de entrada configurado. |
| `Login.java/.form` | Campos correo/contraseña, validación de presencia, consultas de IDs y apertura de Main (`:31`, `:264`). | Autenticación frágil; recuperación de contraseña solo visual. |
| `Signup.java/.form` | Nombre, apellido, correo, teléfono, nacimiento, municipio, rol, contraseña; inserción de usuario (`:52`, `:354`). | Catálogos fijos y validación parcial; entra a Main aun si el DAO informa error; no recupera ID generado. |
| `Main.java/.form` | Contenedor de paneles; botones mapa, perfil, novedades, consulta y web gubernamental (`:176`). | Consulta tiene TODO. Constructor sin usuario permite estado `null`. |
| `PanelMapa.java/.form` | Mapa, lectura de todos los lugares, marcadores y modo de seleccionar punto para reportar (`:32`, `:105`). | Sin detalle del reporte, filtros, vigencia, agrupación ni consultas por distancia. |
| `DenunciaView.java/.form` | Formulario título/descripción/evento/carretera y selección de extremos A/B de un tramo (`:49`, `:145`). | No inserta denuncia; título descartado, estado vial fijo y múltiples errores de flujo. |
| `PanelNovedades.java/.form` | Genera ocho paneles con áreas de texto vacías (`:19`). | Maquetación de prueba, sin consultas ni contenido comunitario. |
| `PanelUser.java/.form` | Panel inicializado. | Sin perfil ni acciones funcionales. |
| `WebGobierno.java` | Crea Chromium JxBrowser y carga la página de restricciones INVIAS (`:25`, `:32`). | Navegador externo embebido; sin importación, validación ni sincronización de datos. |
| `mapa.Mapa` | Extiende JXMapViewer; teselas OSM, pan/zoom, marcadores, geolocalización IP MapTiler y reverse geocoding Nominatim (`:43`, `:77`). | Sin GPS ni rutas; red síncrona sin timeouts explícitos. |

Los `.form` describen los mismos componentes y eventos del código generado por NetBeans; no constituyen funcionalidades adicionales. El diseño usa tamaños fijos aproximadamente de teléfono, pero sigue siendo una aplicación de escritorio: no hay web responsive, PWA ni aplicación móvil.

### 3.4 Catálogos recuperables de la UI

Son textos codificados en los combos, **no datos oficiales ni filas recuperadas de Oracle**. Los IDs originales se desconocen y no se deben deducir por su posición.

- Roles: Transportista y Usuario Particular.
- Municipios: Medellín, Alejandría, Ciudad Bolívar, Caldas, Sabanalarga, Puerto Parra, Sabanagrande, Soledad, Barranquilla, Santo Tomás, Calamar, Magangué, San Juan Nepomuceno, Turbaco, Galapa y Puerto Colombia.
- Carreteras: Ruta Nacional 01, 03, 05, 08, 10, 12, 13, 17, 19, 21 y 25B.
- Eventos: Deslizamiento, Mantenimiento vial, Pérdida de la banca, Alto flujo vehicular, Derrumbe, Desbordamiento de río, Fallas en la estructura del puente, Caída de árbol, Estabilización de talud, Evento Deportivo, Restricción de carga, Evento Especial, Material en la vía y el texto ambiguo «Para vehículos».

Fuentes: `Signup.java:114`, `Signup.java:116`, `DenunciaView.java:272`, `DenunciaView.java:315`. La taxonomía requiere revisión: hay conceptos solapados, otros demasiado amplios y ninguno acredita un catálogo completo del país. Cierre total/parcial y severidad son hipótesis del nuevo producto, no campos implementados en esta UI.

## 4. Flujos reales

### 4.1 Registro

1. Signup comprueba principalmente que los campos estén presentes. Repite la condición de municipio y omite la condición de rol (`:23`).
2. Busca IDs de rol y municipio por nombre; convierte teléfono a número y copia la contraseña directamente a `Usuario` (`:52`).
3. `UsuarioBO` abre una conexión y el DAO ejecuta INSERT con una secuencia.
4. El DAO devuelve un texto de resultado o error. Signup lo muestra y entra a `Main(nodoUsuario)` sin comprobar éxito (`:354`). El ID generado no se devuelve: el objeto recién creado conserva su valor inicial, 0.

### 4.2 Login

`Login.confiLOgin()` (`:31`) hace dos consultas independientes: usuario por correo y usuario por contraseña. Acepta si el primer ID devuelto en ambas coincide y luego carga el usuario por ID. No hay hash en este recorrido, sesión de servidor, tokens, recuperación ni bloqueo por intentos. Contraseñas compartidas por varios usuarios pueden producir resultados incorrectos porque el SELECT por contraseña no identifica unívocamente al usuario.

### 4.3 Mapa y reporte

1. Main construye PanelMapa, que inicializa el mapa mediante IP y lee `LUGAR_DENUNCIA` completo.
2. Cada fila se dibuja como marcador sin información de denuncia asociada.
3. «Agregar» activa selección manual; un clic hace reverse geocoding y exige que exista una propiedad `road`. Esa propiedad no demuestra que el punto pertenezca a la red vial ni que sea apto para carga.
4. DenunciaView solicita título, descripción, evento, carretera y puntos A/B. Calcula distancia geodésica Haversine, no distancia de recorrido vial (`:182`).
5. `captura()` (`:95`) inserta ubicación, inserta tramo e intenta recuperar IDs por nuevas consultas; prepara una denuncia con `ES_ID=1` y el usuario recibido.
6. **No se ejecuta el INSERT de DENUNCIA**. Si la captura devuelve éxito, el handler inserta nuevamente la ubicación (`:460`).
7. El handler oculta el formulario y abre `Main()` incluso con campos inválidos o fallo de captura (`:468`). Cancelar también abre Main sin transmitir usuario (`:451`).

### 4.4 Otras opciones

Perfil abre un panel vacío. Novedades abre ocho espacios sin datos. Consulta carece de implementación. La opción gubernamental abre la web de INVIAS y vuelve al cerrar el visor. No hay flujo de moderación, administración ni revisión de alertas.

## 5. Modelo de datos inferido

### 5.1 Entidades Java: inventario completo

| Modelo | Campos observados | Alcance de evidencia |
|---|---|---|
| `Usuario` | ID `int`, contraseña/correo/nombre/apellido `String`, nacimiento `Date`, teléfono `long`, rol/municipio `int` | Lectura e inserción SQL. |
| `Denuncia` | ID `int`, estado `boolean`, descripción `String`, fecha `Date`, IDs tramo/estado vial/lugar/usuario/evento `int` | SQL salvo `den_estado`, que no se persiste, lee ni copia en constructor de copia. |
| `Lugar_Denuncia` | ID `int`, latitud y longitud `float` | Lectura e inserción SQL. El atributo se escribe `latutud`. |
| `Tramo` | ID `int`, inicio/fin `String`, longitud `float`, carretera `int` | Inserción SQL. Constructor de copia omite carretera. |
| `Evento` | ID `int`, nombre `String` | SELECT y búsqueda por nombre desde UI. |
| `Rol` | ID `int`, nombre `String` | Tabla/columnas usadas por búsqueda desde Signup. |
| `Municipio` | ID `int`, nombre `String` | Tabla/columnas usadas por búsqueda desde Signup. No contiene referencia a departamento. |
| `Carretera` | ID `int`, nombre `String`, longitud/inicio/fin `int` | Solo ID/nombre aparecen en consultas externas al modelo. Significado de extremos desconocido. |
| `Estado_Via` | ID `int`, nombre `String` | Solo POJO; `ES_ID` de Denuncia sugiere relación, pero no confirma tabla física ni catálogo. |
| `Departamento` | ID `int`, nombre `String` | Solo POJO, sin persistencia ni relación implementada. |
| `Categoria` | ID `int`, nombre `String` | Solo POJO, sin relación implementada con Evento. |
| `Calificacion` | ID `int`, descripción `String`, valor `int`, fecha `Date` | Solo POJO, sin usuario ni denuncia enlazados. No demuestra un mecanismo de votos. |

Fuentes: declaraciones de cada archivo `src/model`, consultas de DAO y llamadas de Signup/DenunciaView. Las faltas ortográficas de nombres no se interpretan como semántica nueva.

### 5.2 Tablas y columnas confirmadas por las consultas

| Tabla | Columnas observadas |
|---|---|
| `USUARIO` | `USR_ID`, `USR_NOMBRE`, `USR_CORREO`, `USR_CONTRASEÑA`, `USR_FECHANAC`, `USR_APELLIDO`, `USR_TELEFONO`, `MUN_ID`, `ROL_ID` |
| `DENUNCIA` | `DEN_ID`, `DEN_FECHA`, `DEN_DESCRIPCION`, `TRAMO_ID`, `ES_ID`, `LU_ID`, `USR_ID`, `EVNT_ID` |
| `LUGAR_DENUNCIA` | `LU_ID`, `LU_LATITUD`, `LU_LONGITUD` |
| `TRAMO` | `TRAMO_ID`, `TRAMO_INICIO`, `TRAMO_FIN`, `TRAMO_LONGITUD`, `CARR_ID` |
| `EVENTO` | `EVNT_ID`, `EVNT_NOMBRE` |
| `ROL` | `ROL_ID`, `ROL_NOMBRE` |
| `MUNICIPIO` | `MUN_ID`, `MUN_NOMBRE` |
| `CARRETERA` | `CARR_ID`, `CARR_NOMBRE`; otras columnas físicas no recuperables desde SQL observado |

No se confirma una tabla física para cada POJO. Particularmente no se inventan columnas `DEN_ESTADO`, `DEN_TITULO`, relaciones municipio–departamento ni evento–categoría.

### 5.3 Relaciones lógicas

| Origen | Destino inferido | Motivo / certeza |
|---|---|---|
| Usuario | Rol, Municipio | IDs explícitos en INSERT; relación lógica fuerte. |
| Denuncia | Usuario, Evento, Tramo, Lugar | IDs explícitos y usados en captura; relación lógica fuerte. |
| Denuncia | Estado vial | `ES_ID` y POJO compatible; destino físico y significado pendientes. |
| Tramo | Carretera | `CARR_ID` explícito; relación lógica fuerte. |

La interpretación habitual sería muchos registros de origen hacia un destino, pero no se conocen cardinalidades mínimas, obligatoriedad, unicidad ni claves foráneas reales. Ninguna relación con Calificacion, Categoria o Departamento queda acreditada.

### 5.4 Catálogo de operaciones SQL

| Archivo | Operaciones |
|---|---|
| `UsuarioDAO.java:14` / `:38` | INSERT usuario con `SEQ_1_1.NEXTVAL`; SELECT por `USR_ID`. |
| `DenunciaDAO.java:16` / `:40` / `:63` / `:78` | INSERT con `SEQ_9_1.NEXTVAL`; UPDATE y DELETE por ID; SELECT completo. |
| `Lugar_DenunciaDAO.java:15` / `:33` | INSERT con `SEQ_3_1.NEXTVAL`; SELECT completo. |
| `TramoDAO.java:11` | INSERT con `SEQ_3_1.NEXTVAL`. Compartir secuencia con lugar no prueba un defecto por sí solo. |
| `EventoDAO.java:18` | SELECT por `EVNT_ID`. |
| `CtrlDAO.java:10` / `:24` | SELECT de ID con tabla y columnas variables, valor `String` o `float` parametrizado. |
| `CtrlDAO.java:38` | SELECT de `TRAMO_ID` por inicio/fin. Falta espacio después de `WHERE`. |

Las secuencias se conocen por nombre, no por definición, valor actual, incremento ni rango. La mayoría de los valores de usuario están parametrizados; no es correcto afirmar que todo el SQL concatena entradas de usuario.

### 5.5 Qué se puede reconstruir sin Oracle

Se puede reconstruir un **modelo lógico mínimo**, catálogos candidatos y operaciones esperadas. No se pueden recuperar datos reales, hashes históricos, IDs originales, tipos Oracle exactos, tamaños, índices, restricciones, valores por defecto, procedimientos, triggers ni reglas que solo existieran en la BD.

Después de aprobar requisitos y arquitectura, la reconstrucción debe:

1. Resolver vocabulario: denuncia/reporte/alerta, punto frente a tramo, tipo frente a estado vial, vigencia frente a severidad.
2. Diseñar un esquema nuevo con tipos y restricciones explícitos; no reproducir los errores del legado.
3. Versionar migraciones de esquema y catálogos; no crear DDL de producción en esta fase.
4. Separar catálogos verificados de demostraciones sintéticas, con procedencia/licencia y datos sin identidad real.
5. Probar integridad, atomicidad y consultas geográficas contra la BD finalmente elegida.

No hay migración de filas posible con el material disponible; habría reconstrucción de esquema y carga inicial nueva. Si aparece un dump o DDL adicional, deberá reevaluarse.

## 6. Cobertura funcional respecto a las hipótesis iniciales

| Capacidad candidata | Estado real |
|---|---|
| Registro | Parcial: INSERT implementado, manejo de error e ID incorrectos. |
| Autenticación | Parcial y no apta para reutilización como seguridad del nuevo producto. |
| Perfil y recuperación de contraseña | Sin implementación efectiva. |
| Publicar alerta geográfica | Parcial: formulario, lugar y tramo; denuncia no insertada. |
| Tipo, descripción, fecha | Preparación y DAO presentes, sin flujo completo. Fecha usa `setDate`, sin garantía de conservar hora. |
| Título | Obligatorio en UI, descartado en modelo/SQL. |
| Foto / evidencia | Ausente. |
| Severidad y cierre total/parcial | Ausentes como campos y reglas de producto. `ES_ID=1` no permite deducir su significado. |
| Mapa | Renderizado de lugares; sin alerta detallada, filtros ni vigencia. |
| Cercanía / alertas por ruta | Ausente. Haversine entre A/B no es consulta por cercanía ni cálculo de ruta. |
| Confirmación comunitaria / reputación | Ausente. POJO Calificacion aislado. |
| Expiración / cierre de alerta | Ausente. Booleano no persistido. |
| Moderación / administración / abuso | Ausente. Rol de transportista no equivale a autorización de moderador. |
| Notificaciones | Ausente. |
| Histórico / estadísticas | Ausente como funcionalidad. SELECT de todas las denuncias no constituye histórico diseñado. |
| Información oficial | Enlace web embebido, sin integración de datos. |
| Offline / envío diferido / voz / GPS | Ausentes. |
| Backend Spring Boot / API / despliegue | Ausentes; pertenecen al producto por construir. |

No hay documentos que acrediten qué se planeaba originalmente. Los nombres de clases y botones indican intención posible, no requisitos aprobados.

## 7. Deuda técnica, defectos y seguridad

Prioridad **P0**: impide publicar responsablemente o compromete credenciales/datos. **P1**: rompe integridad o flujos centrales. **P2**: mantenibilidad, operación o UX. Son prioridades de análisis; no autorizan cambios en esta fase.

| Prioridad | Hallazgo y evidencia | Consecuencia |
|---|---|---|
| P0 | Credenciales JDBC en `ConnectionOracle.java:10`; clave MapTiler en `Mapa.java:47`; clave JxBrowser en `WebGobierno.java:28`; recurso de licencia en MyLicense. | Exposición en fuente/binarios/historial. Validez y restricciones de las claves desconocidas. No se reproducen sus valores. |
| P0 | Contraseña copiada directamente en `Signup.java:61` y escrita por UsuarioDAO; login consulta por contraseña (`Login.java:33`). | El código no aplica hash; no se conoce si existían mecanismos adicionales en la BD perdida. Diseño no reutilizable. |
| P0 | Cliente de escritorio con acceso directo a BD; sin servidor que aplique autorización. | No hay frontera de confianza adecuada para una comunidad pública. |
| P0 | Licencia comercial JxBrowser y MyLicense de procedencia/legitimidad sin acreditar. | No se puede asumir permiso para redistribución ni funcionamiento válido. Véase sección 8. |
| P1 | No llamada a `denunBO.addDenuncia` en `DenunciaView.java:457`. | Publicación nunca completa el INSERT de reporte. |
| P1 | `CtrlDAO.java:40` produce `WHERETRAMO_INICIO`. | Error sintáctico al recuperar tramo. |
| P1 | Alta de lugar/tramo/denuncia mediante conexiones y commits independientes (`DenunciaView.java:95`, BO respectivos). | Escrituras parciales y huérfanos; sin atomicidad de negocio. |
| P1 | Segundo alta de lugar (`DenunciaView.java:460`); ID recuperado por SELECT separados de latitud y longitud (`:119`). | Duplicados, resultados ambiguos y condiciones de carrera; no se recupera la clave generada. |
| P1 | Retorno a `Main()` sin usuario (`DenunciaView.java:453`, `:469`). | Se pierde identidad; posteriores accesos pueden fallar por `null`. |
| P1 | Registro continúa tras texto de error, sin ID generado (`Signup.java:357`). | Apariencia de sesión válida sin usuario persistido, ID 0. |
| P1 | `UsuarioDAO.java:55` asigna rol desde `MUN_ID`. | Identidad de rol incorrecta; todavía no hay control de acceso que pueda considerarse implementado. |
| P1 | Final de tramo calculado usando punto A (`DenunciaView.java:168`). | Inicio y fin textuales pueden coincidir incorrectamente. |
| P1 | Validación usa radios seleccionados en vez de comprobar coordenadas (`DenunciaView.java:49`). | Posible cálculo con puntos `null`; faltan validaciones de dominio. |
| P1 | DAO captura excepciones y devuelve mensajes, BO puede hacer commit sin conocer el fallo; conexión puede ser `null`. | Éxito/fallo ambiguos, errores sin propagación y posibles excepciones no controladas. |
| P1 | `DenunciaDAO.java:46` convierte `java.util.Date` mediante cast a `java.sql.Date`. | Posible `ClassCastException`; debe convertirse, no asumirse subtipo. |
| P2 | Identificadores SQL concatenados en CtrlDAO. | Superficie peligrosa si se admitieran nombres externos; llamadas actuales usan literales, por lo que no se demuestra un exploit de SQL injection desde la UI. |
| P2 | Red y JDBC ejecutados en recorridos de eventos Swing, sin timeouts HTTP explícitos (`Mapa.java:43`, `:77`). | Bloqueo de interfaz y mala tolerancia a desconexión. |
| P2 | Geocodificación no limpia `road` al fallar (`Mapa.java:77`); `sector()` accede sin comprobar al segundo fragmento separado por comas (`DenunciaView.java:198`). | Puede reutilizar una vía anterior o fallar con una dirección inesperada; no valida red vial. |
| P2 | Latitud/longitud `float`; listas sin paginación ni filtro; sin geometría/índice espacial acreditados. | Pérdida de precisión y trabajo proporcional a todas las ubicaciones. |
| P2 | Se imprimen IDs de usuario (`Login.java:35`), respuestas completas de geocodificación (`Mapa.java:96`) y ubicación (`PanelMapa.java:107`); también se muestran errores técnicos. | Exposición innecesaria; sin política de retención ni tratamiento en el repo. No se acreditó impresión de nombre, correo, teléfono o contraseña en esos logs. |
| P2 | Constructor de copia Tramo omite carretera; Denuncia omite estado; nombres/copy-paste inconsistentes. | Propagación silenciosa de datos incompletos. |
| P2 | `Mapa.delateLastWaypoint()` usa último elemento recorrido de `HashSet`. | No garantiza eliminar el último marcador agregado. |
| P2 | Tamaños fijos, acciones basadas en clics/diálogos y componentes con foco deshabilitado. | Requiere rediseño y pruebas de accesibilidad; no hay garantías de uso seguro en carretera. |
| P2 | Binarios/generated versionados, dependencias locales, sin licencia propia ni atribución de imágenes documentada. | Build y redistribución poco reproducibles; origen de assets pendiente. |

Sobre `DenunciaDAO.java:42`: la concatenación produce `?WHERE` sin espacio. Se recomienda corregir el formato al reconstruir, pero **no se afirma un error sintáctico comprobado** por esa frontera de tokens sin ejecutar el driver. El defecto confirmado de espacio es `WHERETRAMO_INICIO` en CtrlDAO.

La respuesta inicial ante credenciales expuestas será identificar titulares y vigencia, revocar/rotar o restringir las que correspondan y acordar cómo tratar binarios e historial. No se ha revocado ninguna clave, contactado terceros, borrado archivos ni reescrito Git. Quitar una cadena del último archivo no la elimina del historial.

## 8. Dependencias, MyLicense y licencias

### 8.1 Inventario

| JAR | Uso observado / riesgo de continuidad |
|---|---|
| `jxbrowser-7.36.jar` | Motor/API de navegador Chromium comercial para WebGobierno. |
| `jxbrowser-swing-7.36.jar` | Integración Swing de JxBrowser. |
| `jxbrowser-win64-7.36.jar` | Binarios Chromium Windows 64 bits; mayor componente del repositorio. |
| `jxmapviewer2-2.5.jar` | Componente de mapa Swing; licencia LGPL-3.0 según proyecto upstream. |
| `jcalendar-1.4.jar` | Selector de fecha en registro. Upstream declara LGPL-2.1 y proyecto sin mantenimiento; conservar/revisar avisos de distribución. |
| `json-20231013.jar` | Lectura JSON de MapTiler/Nominatim. POM embebido y LICENSE del tag exacto declaran dominio público; no se extrapola desde versiones antiguas. |
| `commons-logging-1.1.3.jar` | Logging utilizado por dependencias; licencia Apache-2.0. |
| `ojdbc11.jar` | Manifest: versión 21.8.0.0.0, especificación JDBC 4.3. Incluye Oracle Free Distribution, Hosting, and Use Terms and Conditions en `META-INF/license.txt`; revisar ese texto específico antes de redistribuir. |
| `MyLicense-1.0.jar` | Código y recurso de licencia de origen por esclarecer; no se conservará por inercia. |

La declaración `AbsoluteLayout` de NetBeans es una dependencia adicional de entorno, aunque no haya un JAR correspondiente en la raíz. Tener bibliotecas en Git no acredita licencia válida ni permisos sobre imágenes. No se asignó licencia al código del propietario.

### 8.2 Qué es MyLicense

El ZIP contiene cinco clases bajo `com.kingaspx`, metadatos Maven `com.kingaspx:MyLicense:1.0` y un recurso `META-INF/teamdev.licenses` de 468 bytes. No es solamente un certificado: contiene código ejecutable.

La inspección estática de `BrowserUtil` y `MySimpleDateFormat` observa sustitución por reflexión de un formateador de fechas y alteración del resultado del parseo de algunas fechas. Los metadatos apuntan a JxBrowser **6.22**, con referencias de versión 6.09/6.18/6.22, mientras esta aplicación empaqueta **7.36**. Esto es consistente con un mecanismo para alterar comprobaciones temporales de un JxBrowser anterior; no demuestra que se active, funcione sobre 7.36 ni que su uso sea legítimo.

No se encontraron imports ni llamadas a `com.kingaspx`/`BrowserUtil` en las fuentes de la aplicación. La presencia en classpath y del recurso de licencia no demuestra que sea irrelevante en ejecución. No se ejecutó, no se mostró el contenido del recurso ni se validó ninguna clave.

Conclusión: procedencia, autorización y finalidad concreta pendientes del propietario. No se debe reutilizar ese mecanismo en el nuevo producto. No se atribuye una infracción al propietario ni se afirma que haya comprado una licencia sin evidencia.

### 8.3 JxBrowser: confirmación y efecto

TeamDev documenta que JxBrowser es comercial y requiere una clave válida. Distingue licencias comerciales y evaluación temporal. Las condiciones de distribución dependen del acuerdo adquirido: el repositorio no contiene evidencia suficiente para acreditar un derecho de redistribución. Una clave incrustada o un JAR llamado MyLicense no lo acreditan. [Guía oficial de licencias](https://teamdev.com/jxbrowser/docs/guides/introduction/licensing/).

La documentación de la rama 7 indica fin de soporte desde octubre de 2025. No se hizo análisis de CVEs ni se atribuye una vulnerabilidad concreta a este binario. [Referencia oficial JxBrowser 7](https://teamdev.com/jxbrowser/docs/7/reference/).

Para el nuevo cliente, que aún debe elegirse, el navegador embebido de escritorio no se justifica por el objetivo actual. La propuesta es retirarlo de la arquitectura futura, preservando referencia histórica según lo que se acuerde. No se ha recomendado comprar licencia ni asumido una exención gratuita.

### 8.4 Obligaciones y verificación restante

Antes de redistribuir el legado se necesitaría inventario de licencias y avisos por artefacto, procedencia de MyLicense y assets, y revisión de las condiciones aplicables a las versiones exactas. Para el nuevo producto se generará su inventario de componentes a partir de las dependencias elegidas. Este reconocimiento no es un escaneo CVE ni afirma que versiones antiguas sean explotables solo por su edad.

Fuentes primarias de contraste: [JXMapViewer2 y LGPL-3.0](https://github.com/msteiger/jxmapviewer2/blob/master/jxmapviewer2/build.gradle), [Commons Logging](https://commons.apache.org/proper/commons-logging/), [LICENSE del tag JSON 20231013](https://github.com/stleary/JSON-java/blob/20231013/LICENSE), [JCalendar y LGPL-2.1](https://github.com/toedter/jcalendar), [página del autor de JCalendar](https://toedter.com/jcalendar/), [Oracle JDBC](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html). Las obligaciones específicas no quedan cerradas solo por enlazar el proyecto upstream. No se verificó la correspondencia de todos los binarios locales con sus publicaciones originales.

## 9. Servicios geográficos y fuentes oficiales

### 9.1 Uso actual

- MapTiler se llama para geolocalizar por IP, no para obtener GPS. La documentación lo describe como ubicación aproximada. No se ha verificado el plan o validez de la cuenta original ni utilizado la clave encontrada. [API oficial](https://docs.maptiler.com/cloud/api/geolocation/).
- JXMapViewer obtiene teselas mediante `OSMTileFactoryInfo`. La app no muestra una atribución implementada explícitamente en sus fuentes; debe comprobarse también el comportamiento de la biblioteca antes de calificar cumplimiento.
- `getLocationInfo` envía coordenadas a Nominatim y usa el texto resultante para deducir vía/sector. No hay validación geoespacial contra una red oficial.
- WebGobierno solo abre `https://hermes.invias.gov.co/Restricciones_INVIAS/`.

### 9.2 Restricciones verificadas que afectan futuras decisiones

**Teselas OSM:** la política del servidor estándar exige atribución, identificación y caché, ofrece disponibilidad sin garantía y prohíbe descargas masivas/prefetch para mapas offline. Datos abiertos no equivalen a hosting ilimitado. No debe diseñarse una descarga de regiones sobre ese servicio. [Política oficial de teselas](https://operations.osmfoundation.org/policies/tiles/).

**Nominatim público:** máximo absoluto de una petición por segundo para el conjunto de la aplicación, identificación adecuada, atribución y restricciones de uso; prohíbe autocompletado y consultas sistemáticas. El legado no implementa control global de tasa ni caché explícita. No se recomienda todavía como proveedor del nuevo producto. [Política oficial de Nominatim](https://operations.osmfoundation.org/policies/nominatim/).

**INVIAS:** se localizó un servicio ArcGIS oficial `OpenData/ServiciosOpenData1`, con capa Red Vial, referencia espacial 3857, copyright INVIAS y formatos de consulta JSON/GeoJSON. Esto demuestra una fuente candidata, no permiso de reutilización ni cobertura/calidad suficientes. Falta verificar licencia explícita, actualización, cobertura, restricciones y disponibilidad. La página antigua de restricciones devolvió 502 mediante la herramienta de consulta: no prueba una caída permanente. [Metadatos oficiales del servicio](https://hermes.invias.gov.co/arcgis/rest/services/OpenData/ServiciosOpenData1/MapServer).

La investigación completa de red vial, alertas oficiales, licencias de datasets y planes gratuitos corresponde a las siguientes fases; no se ha aprobado descargar ni importar datos. Como fuente adicional para explorar se identificó el [portal oficial de estado de vías de MinTransporte](https://plc.mintransporte.gov.co/Estado-v%C3%ADas).

### 9.3 Datos personales y seguridad vial

El legado recopila nacimiento, teléfono, correo y municipio, y asocia usuario con coordenadas de reportes. No hay política de privacidad/tratamiento, consentimiento, retención, eliminación ni atención de derechos documentados. Estos son asuntos por diseñar y validar frente a la [Ley 1581 de 2012, texto oficial compilado por MinTIC](https://normograma.mintic.gov.co/mintic/compilacion/docs/ley_1581_2012.htm); este informe no certifica cumplimiento jurídico.

La interacción actual requiere varios clics, escritura y selección de puntos: no acredita un uso seguro conduciendo. En descubrimiento debe fijarse el contexto de uso y evaluar reporte detenido/por acompañante, consulta previa al viaje y avisos de antigüedad y confiabilidad. La voz no debe suponerse automáticamente segura ni gratuita.

## 10. Reutilización propuesta, pendiente de aprobación

| Conservar como referencia | Reconstruir o reemplazar |
|---|---|
| Concepto comunitario de información vial en Colombia. | Swing, formularios y navegador Chromium embebido para el nuevo cliente. |
| Vocabulario Usuario, Reporte, Evento, Ubicación, Tramo y Carretera, tras validarlo. | JDBC Oracle, secuencias específicas, SQL genérico y configuración incrustada. |
| Casos y catálogos iniciales como material para entrevistas de dominio. | Autenticación, roles y gestión de errores. |
| Fórmula Haversine como referencia matemática, solo si existe un requisito que la necesite. | Coordinación transaccional, búsqueda geográfica, identidad de registros y fechas. |
| Historia del repositorio y recursos gráficos cuya autoría/licencia se acredite. | Esqueletos de perfil/novedades/consulta y supuesto sistema de calificación. |

No se identificó un núcleo de negocio suficientemente aislado y completo para trasladarlo sin cambios. Reutilización conceptual no significa portar las clases DAO/BO tal cual. No se borra material legado en Fase 0.

## 11. Equipo y herramientas

Concurrencia disponible: coordinador y tres subagentes simultáneos. Los roles se activarán por necesidad y no implican doce procesos permanentes.

| Rol | Alcance y entregable | Activación |
|---|---|---|
| Tech Lead / arquitecto coordinador | Integración, conflictos, evidencia y checkpoints con propietario. | Todas las fases. |
| Analista de código legado | Inventario, flujos reales, ingeniería inversa. | Fase 0; activo como subagente UI/mapas. |
| Analista de requisitos / Product Owner | Entrevista, alcance, historias y criterios de aceptación. | Fases 1–2; revisiones de alcance posteriores. |
| Arquitecto de software | Comparativas, ADR, límites de módulos, contratos y diagramas. | Fases 3–4. |
| Backend Java/Spring Boot | API, dominio, persistencia y seguridad implementados. | Fase 5 tras aprobación expresa. |
| Frontend / móvil | Cliente según plataforma aprobada y mapas. | Fases 3–6. |
| UX/UI | Flujos seguros, prototipos y accesibilidad. | Descubrimiento y fases 3–6. |
| Datos / geoespacial | Modelo inferido, fuentes, licencias y consultas espaciales. | Fase 0 como subagente datos/negocio; fases 2–6. |
| QA | Estrategia, trazabilidad y pruebas independientes. | Fase 2 en adelante. |
| DevOps / SRE | Límites gratuitos, CI/CD, despliegue, rollback y operación. | Fases 3–6. |
| Seguridad | Secretos, amenazas, datos personales y abuso. | Fase 0 como subagente dependencias/seguridad; fases posteriores. |
| Revisor independiente | Contraste de cambios antes de merge; no revisa como independiente su propia implementación. | Revisiones y cada merge futuro. |
| Documentación técnica | Documentos, manuales y portafolio consistentes con evidencia. | Coordinador ahora; rol específico según volumen. |

### 11.1 Inventario de capacidades disponibles

- **Skills de proceso:** using-superpowers, brainstorming, dispatching-parallel-agents, writing-plans, executing-plans, subagent-driven-development, using-git-worktrees, test-driven-development, systematic-debugging, requesting-code-review, receiving-code-review, verification-before-completion, finishing-a-development-branch y diagnosing-superpowers.
- **Java:** create-spring-boot-java-project, java-springboot, springboot-patterns, springboot-security y springboot-verification.
- **Diseño y revisión:** antislop y sus variantes code/copywriting/human/layoutmobile/ui, install-anti-slop, design-taste-frontend, frontend-design, web-design-guidelines, hyperframes-animation, review-animations y threejs-animation.
- **Documentos y recursos:** mermaid-diagrams, plantuml, pptx, presentation-design, slides, academic-research-writer, humanize-paper, ieee-search, ieee-export e imagegen.
- **Plataforma y extensiones:** openai-docs, skill-creator, skill-installer, find-skills, writing-skills, plugin-management, Sites/sites-remix, windows-diagnostics y work-pets create/pets/update. No todas son pertinentes.
- **Herramientas locales:** lectura/escritura autorizada en workspace, PowerShell, Git, rg, inspección ZIP/JAR/bytecode, ejecución de comandos y visualización de imágenes locales.
- **Investigación:** búsqueda/navegación web y consulta de documentación primaria; generación/edición de imágenes si una fase posterior lo justifica.
- **MCP anunciados:** Codex Document Control; búsqueda web; Sites; gestión de plugins; work-pets; hotline y safety settings. También hay herramientas nativas Codex para worktrees. La disponibilidad anunciada no acredita conexión a una cuenta concreta o permisos de publicación.
- **Coordinación:** subagentes, preguntas interactivas, reloj y seguimiento de trabajo. No se ha supuesto un conector GitHub con capacidad de crear PR/issues; se verificará cuando sea necesario.

Aplicadas ahora: using-superpowers para proceso, dispatching-parallel-agents para reparto independiente y verification-before-completion para contrastar resultados. Brainstorming se leyó para el descubrimiento, adaptando preguntas/checkpoints a la instrucción expresa del propietario. Antislop se leyó; no se activa diseño visual ni instala configuración durante reconocimiento. Las skills de implementación se usarán después de sus aprobaciones, no para adelantar decisiones.

## 12. Dudas que solo el propietario puede resolver

Las preguntas se enviarán en lotes de hasta ocho, con recomendación y motivo. Esta lista registra asuntos pendientes, no exige responderlos todos ahora.

- ¿Quién utilizaría primero el producto y en qué momento real del viaje? ¿Hay transportistas disponibles para entrevistas/pruebas?
- ¿«Como Waze» significa alertas o también navegación giro a giro, cálculo de rutas aptas para carga y seguimiento continuo?
- ¿Cuál es el alcance final que consideras completo? ¿Aceptas un primer hito más pequeño dentro de ese alcance?
- ¿País completo desde el primer día o piloto en un corredor? ¿Qué usuarios y volumen esperas al principio?
- ¿Horas semanales, fecha objetivo y prioridad laboral que deseas demostrar?
- ¿Costo cero estricto sin tarjeta? ¿Aceptas suspensión/cold start? ¿Hay cuentas o dominios existentes?
- ¿Plataformas/dispositivos prioritarios y conectividad de esos usuarios?
- ¿Qué tipos de alerta, severidades, duración y reglas de confirmación representan el dominio real?
- ¿Se permite consulta anónima? ¿Quién puede reportar? ¿Cómo verificar o definir «transportista» sin recopilar datos innecesarios?
- ¿Quién moderará y cuánto tiempo podrá dedicar? ¿Qué abuso es más probable?
- ¿Qué notificaciones, capacidades offline y acceso por voz son imprescindibles?
- ¿Debe existir asociación pública entre identidad y ubicación del reporte? ¿Qué datos personales son indispensables y quién será responsable de tratamiento?
- ¿Origen de MyLicense, clave JxBrowser, clave MapTiler e imágenes? ¿Hubo colaboradores o material ajeno? ¿Hay licencia/comprobantes originales?
- ¿Aparecen formularios adicionales, documentos, DDL, capturas o copias de base fuera del repositorio? ¿Qué significaban `ES_ID=1`, Calificacion, Categoria y «Para vehículos»?
- ¿Idioma de interfaz/documentación, licencia del nuevo código y objetivos de presentación del portafolio?

## 13. Checkpoint

**Entregable:** reconocimiento estático con inventario, modelo inferido, SQL, brechas funcionales, seguridad, licencias, reutilización y dudas. **Estado:** aprobado explícitamente por JhanDev en su respuesta al primer lote de preguntas. Fase 1 en curso; las respuestas y decisiones pendientes se registran en `docs/descubrimiento/01-decisiones-y-pendientes.md`.

Por la petición expresa del primer paso, el informe se acompaña del primer lote de preguntas de Fase 1. No se aprueban requisitos, stack o planificación por recibir esas respuestas. Fases 2–6 y código de producción siguen pendientes de sus checkpoints; la construcción exige aprobación explícita de la planificación de Fase 4.
