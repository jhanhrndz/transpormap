# TransporMap — Trazabilidad del legado para H0

**Estado:** análisis documental de trabajo. **Base:** `main`, commit `ab8ed7d` identificado en el [informe de reconocimiento](../00-informe-reconocimiento.md). **Alcance:** conservar la continuidad del TransporMap original y señalar qué necesita validación antes de trasladarse al modelo aprobado. No se modifica código, esquema, datos, recursos ni historial como parte de este documento.

La fuente de verdad de alcance es [requisitos de Fase 2](../01-requisitos.md). La base Oracle y su contenido se perdieron: esta matriz describe textos, atributos y consultas encontrados en el repositorio, **no** una migración de registros, un catálogo oficial, claves originales ni un esquema físico comprobado. `src/view/*.java` usa la codificación `ISO-8859-1` declarada en `nbproject/project.properties`; los literales se leyeron con esa codificación. Las propuestas de correspondencia no autorizan asignaciones automáticas, desaparición de categorías ni cambios del alcance aprobado.

## 1. Criterios de lectura

- **Observado:** el texto, campo o flujo aparece en una fuente identificada. Su presencia no prueba que funcionara extremo a extremo.
- **Continuidad aprobada:** capacidad o restricción expresamente aceptada en requisitos; puede exigir un diseño nuevo.
- **Candidato:** relación semántica posible para discutir; puede ser imprecisa, agrupar varios conceptos o necesitar un campo adicional.
- **Pendiente:** no existe correspondencia suficiente para elegir sin perder información, ni evidencia sobre el significado original o la fuente.

Los siete grupos aprobados son: **mal estado, cierre total, cierre parcial, accidente, derrumbe/inundación, obras y restricción de carga**. Las reglas nuevas separan categoría, impacto vial y severidad; «cierre total/parcial» puede describir un impacto además del tipo seleccionado. La forma final de representar esa relación debe evitar categorías o impactos contradictorios y se concretará sin reemplazar silenciosamente lo aprobado ([RB-02](../01-requisitos.md)).

## 2. Eventos heredados y correspondencias candidatas

El combo `cmbEvento` contiene **14 valores de evento** además de «Seleccionar»; la lista está codificada en [`DenunciaView.java:272`](../../src/view/DenunciaView.java#L272). La vista busca el ID de evento por nombre ([`DenunciaView.java:95`](../../src/view/DenunciaView.java#L95)), lo cual no recupera los IDs de la BD perdida. El texto siguiente respeta el valor original; las explicaciones y las posibles correspondencias son interpretaciones, no valores hallados en Oracle.

| # | Etiqueta original | Correspondencia candidata con los siete grupos | Decisión pendiente / riesgo de pérdida |
|---:|---|---|---|
| 1 | Deslizamiento | **Derrumbe/inundación**, si se confirma que el grupo abarca deslizamientos de terreno. | No se acredita que «deslizamiento» y «derrumbe» fueran sinónimos en el dominio original; distinguir subtipos podría ser útil. No inferir cierre o severidad. |
| 2 | Mantenimiento vial | **Obras** como actividad vial planificada o correctiva. | Confirmar si se agrupa con obras; la etiqueta sola no indica paso parcial, cierre ni duración. |
| 3 | Pérdida de la banca | **Mal estado** como condición de la vía, posiblemente con impacto de cierre. | La pérdida de banca puede afectar gravemente la transitabilidad; el impacto real debe recogerse aparte, sin traducirla automáticamente a cierre total. |
| 4 | Alto flujo vehicular | **Sin correspondencia directa.** | Congestión no equivale necesariamente a mal estado ni accidente. Decidir si se conserva como subtipo, texto descriptivo u opción adicional mediante aprobación; no forzar su inclusión en uno de los siete grupos. |
| 5 | Derrumbe | **Derrumbe/inundación** como candidato directo para el fenómeno. | Mantener explícito el subtipo si se requiere diferenciarlo de una inundación; el grupo combinado no implica que ambos ocurrieron. |
| 6 | Desbordamiento de rio | **Derrumbe/inundación**, componente de inundación como candidato. | Un río desbordado puede o no inundar la calzada; confirmar cómo describir el hecho y su impacto. El original escribe «rio» sin tilde. |
| 7 | Fallas en la estructura del puente | **Mal estado** como posible condición de infraestructura. | Una falla de puente no equivale por sí sola a cierre total o parcial; el grupo genérico puede ocultar que es un puente. Conservar detalle y permitir impacto desconocido. |
| 8 | Caída de arbol | **Sin correspondencia directa inequívoca.** | Puede generar material en la vía o cierre, pero ninguna consecuencia es segura. No clasificar automáticamente como derrumbe o accidente; validar un subtipo o campo descriptivo. El original escribe «arbol». |
| 9 | Estabilización de talúd | **Obras** si describe trabajos en ejecución. | Si describe el estado o el riesgo de un talud, la interpretación puede ser distinta. No inferir automáticamente que existe un derrumbe. |
| 10 | Evento Deportivo | **Sin correspondencia directa.** | Puede causar restricción o cierre, pero no necesariamente. El impacto debe declararse y la conservación del tipo de evento necesita decisión. |
| 11 | Restricción de carga | **Restricción de carga** como candidato directo. | Aclarar si comprende peso, altura, horario u otras restricciones; no inventar datos o restricciones oficiales. |
| 12 | Evento Especial | **Sin correspondencia directa.** | El texto es demasiado amplio para inferir fenómeno o impacto; no equipararlo a accidente u obras. Solicitar definición de dominio antes de reutilizarlo como categoría. |
| 13 | Material en la vía | **Sin correspondencia directa inequívoca.** | Puede ser consecuencia de derrumbe, caída de objetos u otras causas. «Mal estado» puede ser una aproximación demasiado amplia; preservar descripción y validar tratamiento. |
| 14 | Para vehiculos | **Sin correspondencia directa.** | No se conoce el significado: podría ser un texto incompleto. No inferir que equivale a restricción de carga. El original escribe «vehiculos». |

No todas las categorías acordadas tienen antecedente específico en el combo: **accidente, cierre total y cierre parcial** son necesidades aprobadas, no equivalencias demostradas de una de estas 14 etiquetas. Asimismo, la ausencia de una correspondencia no elimina un evento legado: la decisión de ampliar o ajustar taxonomía se revisará con el propietario conforme al alcance aprobado.

No existe evidencia de datos de incidentes originales que permitan medir frecuencia o confirmar cuáles etiquetas eran usadas. La columna `EVNT_NOMBRE` y el uso de `EVENTO` sí aparecen en la lectura DAO y la búsqueda de la vista ([`EventoDAO.java:21`](../../src/model/EventoDAO.java#L21), [`DenunciaView.java:96`](../../src/view/DenunciaView.java#L96)); no se conoce si el combo coincidía íntegramente con el catálogo real de la BD.

## 3. Carreteras y rutas en el legado

El combo `cmbCarretera` contiene las siguientes **11 cadenas fijas**, más el marcador «Seleccionar», en [`DenunciaView.java:315`](../../src/view/DenunciaView.java#L315):

1. Ruta Nacional 01
2. Ruta Nacional 03
3. Ruta Nacional 05
4. Ruta Nacional 08
5. Ruta Nacional 10
6. Ruta Nacional 12
7. Ruta Nacional 13
8. Ruta Nacional 17
9. Ruta Nacional 19
10. Ruta Nacional 21
11. Ruta Nacional 25B

La búsqueda de la vista consulta `CARR_ID` por `CARR_NOMBRE` ([`DenunciaView.java:129`](../../src/view/DenunciaView.java#L129)); el modelo `Carretera` contiene nombre, longitud y extremos enteros ([`Carretera.java:5`](../../src/model/Carretera.java#L5)). No hay evidencia suficiente para establecer unidad de longitud, significado de extremos, geometría o correspondencia con registros viales oficiales.

**Criterio de conservación:** mantener estos nombres como trazas históricas y muestras para contrastar los casos de uso, no cargarlos como una representación completa o actualizada de carreteras colombianas. Sus IDs, geometrías, jurisdicciones, existencia actual y licencias no están verificados. Una fuente oficial o abierta deberá validarse por separado antes de poblar un catálogo. Los extremos A/B que el formulario antiguo solicita para un **tramo de denuncia** no equivalen a la nueva consulta de alertas cercanas a una **ruta calculada A–B**.

## 4. Trazabilidad de pantallas y flujos

| Legado y evidencia | Lo observado | Continuidad hacia requisitos aprobados / brecha |
|---|---|---|
| Bienvenida ([`Bienvenida.java:9`](../../src/view/Bienvenida.java#L9), [`:99`](../../src/view/Bienvenida.java#L99)) | Pantalla de bienvenida con transición a Login; no es el punto de entrada configurado del proyecto. | Conservar la intención de presentación como referencia; la forma exacta de la entrada se definirá en UX. |
| Login ([`Login.java:31`](../../src/view/Login.java#L31), [`:264`](../../src/view/Login.java#L264)) | Verifica presencia y busca IDs por correo y contraseña; recuperación solo visible. | HU-01 exige registro/inicio y recuperación funcional; todas las alertas y fotos requieren sesión. Sustituir validación insegura, no portar el esquema de comparación heredado. |
| Signup ([`Signup.java:23`](../../src/view/Signup.java#L23), [`:52`](../../src/view/Signup.java#L52), [`:354`](../../src/view/Signup.java#L354)) | Formulario de datos personales, rol y municipio; intenta registrar. Puede entrar a Main sin comprobar el resultado ni recuperar el ID generado. | HU-01/02 mantienen cuenta y perfil; reducir datos a lo necesario y resolver errores, identidad y privacidad. No presumir que el registro original estuviera completo. |
| Main / navegación ([`Main.java:12`](../../src/view/Main.java#L12), [`:176`](../../src/view/Main.java#L176)) | Contenedor principal: perfil, novedades, consulta, mapa y web gubernamental; consulta tiene TODO. | Mantener los propósitos: cuenta, publicaciones, consultas, mapa y referencia oficial. La navegación se rediseña según PWA aprobada condicionalmente; la forma visual concreta no está decidida. |
| PanelMapa / Mapa ([`PanelMapa.java:23`](../../src/view/PanelMapa.java#L23), [`:33`](../../src/view/PanelMapa.java#L33), [`:98`](../../src/view/PanelMapa.java#L98), [`Mapa.java:43`](../../src/mapa/Mapa.java#L43)) | Carga ubicaciones y dibuja marcadores; selección manual para comenzar denuncia; ubicación inicial aproximada por IP. | HU-05/06 necesitan alertas completas y coherentes en mapa/lista, filtros, ruta o cercanía y permiso de ubicación; IP no equivale a GPS. La compatibilidad offline de datos es nueva, sin exigir mapa regional descargable. |
| DenunciaView ([`DenunciaView.java:49`](../../src/view/DenunciaView.java#L49), [`:95`](../../src/view/DenunciaView.java#L95), [`:457`](../../src/view/DenunciaView.java#L457)) | Exige título/descripción, evento, carretera y selección de dos extremos; crea lugares y tramos parcialmente. El botón no llama al alta de denuncia. | HU-03/04 conservan reporte, título, descripción, evento y ubicación, con punto obligatorio y carretera/tramo opcionales aprobados. Persistencia real, foto y cola offline son trabajo pendiente; no llamar «refactorizado» a copiar el flujo defectuoso. |
| PanelNovedades ([`PanelNovedades.java:19`](../../src/view/PanelNovedades.java#L19)) | Crea ocho paneles vacíos de ejemplo. | HU-05/08 requieren publicaciones y novedades reales. El antecedente visual no acredita actividad comunitaria. |
| PanelUser ([`PanelUser.java:11`](../../src/view/PanelUser.java#L11)) | Panel sin datos de perfil funcionales. | HU-02 requiere datos y preferencias de cuenta; no hay función heredada lista para migrar. |
| Consulta en Main ([`Main.java:188`](../../src/view/Main.java#L188)) | Handler con TODO. | La consulta de alertas por ruta/cercanía es requisito aprobado nuevo; no se confunde con selección de tramo de DenunciaView. |
| WebGobierno ([`WebGobierno.java:32`](../../src/view/WebGobierno.java#L32)) | Abre la web de INVIAS en JxBrowser embebido. | Se conserva el interés por fuentes oficiales; investigar y enlazar, sin importación automática hasta aprobación y verificación de licencia. No llevar JxBrowser/MyLicense al nuevo runtime. |

## 5. Trazabilidad de datos y reglas

### 5.1 Usuario y sesión

Los campos están declarados en [`Usuario.java:7`](../../src/model/Usuario.java#L7), se capturan en [`Signup.java:52`](../../src/view/Signup.java#L52) y se insertan en [`UsuarioDAO.java:16`](../../src/model/UsuarioDAO.java#L16).

| Campo/concepto legado | Evidencia | Continuidad, cambio o punto pendiente |
|---|---|---|
| `idUsuario` / `USR_ID` | `Usuario.java:7`; `UsuarioDAO.java:16` | Mantener identidad estable por cuenta; no hay IDs originales o filas que migrar. El registro legado no recupera el ID generado. |
| `usu_Nombre`, `usu_Apellido` | `Usuario.java:10`; `Signup.java:56` | Servían al perfil original. Alias fue aprobado; decidir con finalidad explícita si nombre/apellido son necesarios u opcionales, sin asumirlos obligatorios. |
| `usu_Correo`, `usu_Contrasena` | `Usuario.java:8`; `Signup.java:58` y `:61`; `Login.java:31` | Conservar intención de identidad y acceso; correo privado y gestión segura de credenciales. No portar contraseñas en texto ni consultas independientes por contraseña. No se conocen contraseñas reales anteriores. |
| `usu_Fechanac`, `usu_telefono` | `Usuario.java:12`; `Signup.java:59` | No exigir fecha de nacimiento o teléfono sin necesidad justificada: RB-09. Si se considera recuperarlos, especificar finalidad, privacidad y consentimiento aplicable antes de recogerlos. |
| `idMun` / `MUN_ID` | `Usuario.java:15`; `Signup.java:54` | Municipio como contexto geográfico posible; no inferir domicilio obligatorio ni relación con departamento. Hay una lista parcial fija de 16 nombres en `Signup.java:116`, no catálogo nacional. |
| `idRol` / `ROL_ID` | `Usuario.java:14`; `Signup.java:53` y `:114` | El combo ofrece «Transportista» y «Usuario Particular». Preservar como antecedentes; no confundirlos con el permiso de administración nuevo ni permitir elegir privilegios administrativos en registro. UsuarioDAO asigna erróneamente rol desde municipio ([`UsuarioDAO.java:55`](../../src/model/UsuarioDAO.java#L55)). |

El consentimiento y los periodos de tratamiento se rigen por requisitos aprobados y por la política que se documente; no se derivan automáticamente de la existencia histórica de un campo.

### 5.2 Denuncia, lugar, tramo y evento

| Campo o relación heredada | Evidencia | Continuidad y tratamiento |
|---|---|---|
| `Denuncia` / `DEN_ID` | [`Denuncia.java:7`](../../src/model/Denuncia.java#L7), [`DenunciaDAO.java:19`](../../src/model/DenunciaDAO.java#L19) | Antecedente del concepto **alerta/publicación**; nueva identidad persistente e idempotencia requeridas. No afirmar que el botón viejo guardaba el registro. |
| Título `txtTitulo` | [`DenunciaView.java:49`](../../src/view/DenunciaView.java#L49), [`:95`](../../src/view/DenunciaView.java#L95), [`DenunciaDAO.java:19`](../../src/model/DenunciaDAO.java#L19) | Existía y era obligatorio en la vista, pero la captura y el INSERT observado no lo guardan. El nuevo requisito mantiene título obligatorio y exige persistencia; no inventar columna Oracle. |
| Descripción `den_descrpcion` / `DEN_DESCRIPCION` | [`Denuncia.java:9`](../../src/model/Denuncia.java#L9), [`DenunciaView.java:102`](../../src/view/DenunciaView.java#L102) | Conservar descripción obligatoria como texto de alerta; la validación exacta y corrección de nombre forman parte del nuevo contrato, no del esquema original. |
| Fecha `den_fecha` / `DEN_FECHA` | [`Denuncia.java:10`](../../src/model/Denuncia.java#L10), [`DenunciaDAO.java:17`](../../src/model/DenunciaDAO.java#L17) | Hay un atributo `Date` y se usa `setDate`; no garantiza hora conservada. La separación de fecha/hora observada y recibida, tolerancia futura y vigencia son ampliaciones aprobadas. |
| `den_estado`, `ES_ID` | [`Denuncia.java:8`](../../src/model/Denuncia.java#L8), [`DenunciaView.java:104`](../../src/view/DenunciaView.java#L104), [`DenunciaDAO.java:19`](../../src/model/DenunciaDAO.java#L19) | `den_estado` no figura en SQL observado; `ES_ID` se fija a 1 sin significado recuperable. No mapear esos valores a vigencia, severidad o impacto nuevos. Diferenciar estados de alerta y retirada de moderación. |
| `usr_id`, `evnt_id` | [`Denuncia.java:14`](../../src/model/Denuncia.java#L14), [`DenunciaView.java:106`](../../src/view/DenunciaView.java#L106) | La alerta mantiene autor y categoría/evento con nuevas reglas de autorización; no hay claves originales que migrar. El modelo heredado tampoco prueba cardinalidades físicas. |
| `lu_id`, `latutud`, `longitud` | [`Lugar_Denuncia.java:5`](../../src/model/Lugar_Denuncia.java#L5), [`Lugar_DenunciaDAO.java:17`](../../src/model/Lugar_DenunciaDAO.java#L17) | Conservar punto del incidente, ahora obligatorio y seleccionable manualmente. Revisar precisión, validación y geometría; no recuperar identidad buscando latitud y longitud por separado, como hace `DenunciaView.java:119`. |
| `tramo_id`, `inicio_tramo`, `fin_tramo`, `longitud`, `carr_id` | [`Tramo.java:5`](../../src/model/Tramo.java#L5), [`TramoDAO.java:13`](../../src/model/TramoDAO.java#L13) | Conservar posibilidad de carretera y extremos como contexto **opcional** de un incidente. El cálculo heredado es distancia entre dos puntos, no longitud de ruta vial; el extremo B se describe erróneamente usando A ([`DenunciaView.java:168`](../../src/view/DenunciaView.java#L168)). No portar esa lógica como cálculo vial. |
| `Carretera.nombre`, `logitud`, `inicioCarretera`, `finCarretera` | [`Carretera.java:5`](../../src/model/Carretera.java#L5) | Nombre de carretera es referencia útil; tipos y semántica de longitud/extremos no acreditan columnas, unidades ni relaciones en la BD. No suponer geometría o topología por el nombre. |
| `Evento.event_nombre` | [`Evento.java:5`](../../src/model/Evento.java#L5), [`EventoDAO.java:21`](../../src/model/EventoDAO.java#L21) | Referencia para taxonomía; las 14 etiquetas se contrastan en sección 2. Nuevas severidad e impacto son reglas ampliadas, no campos demostrados del evento. |

### 5.3 Modelos sin relación confirmada

| Modelo | Evidencia | Límite de la inferencia |
|---|---|---|
| `Calificacion`: ID, descripción, valor, fecha | [`Calificacion.java:7`](../../src/model/Calificacion.java#L7) | Solo POJO; no se observa relación con usuario/denuncia ni un sistema funcional de votos. Confirmaciones son ampliación con una respuesta por cuenta. |
| `Categoria` y `Estado_Via` | [`Categoria.java:5`](../../src/model/Categoria.java#L5), [`Estado_Via.java:5`](../../src/model/Estado_Via.java#L5) | Nombres sugieren catálogos, pero no hay SQL que confirme tabla o una relación Categoría–Evento. `ES_ID` no demuestra su significado. |
| `Departamento` y `Municipio` | [`Departamento.java:5`](../../src/model/Departamento.java#L5), [`Municipio.java:6`](../../src/model/Municipio.java#L6) | Ambos tienen nombre e ID, pero no hay relación municipio–departamento demostrada. No cargar jerarquías inferidas. |

## 6. SQL y reconstrucción sin datos

Hay ocho tablas referenciadas por SQL o por llamadas genéricas con identificadores literales: `USUARIO`, `DENUNCIA`, `LUGAR_DENUNCIA`, `TRAMO`, `EVENTO`, `CARRETERA`, `ROL` y `MUNICIPIO`. Las cinco primeras tienen operaciones SQL explícitas en DAO; las otras tres aparecen en búsquedas de la UI. La tabla y las columnas no demuestran restricciones físicas o cardinalidad. Fuentes: [`UsuarioDAO.java:16`](../../src/model/UsuarioDAO.java#L16), [`DenunciaDAO.java:19`](../../src/model/DenunciaDAO.java#L19), [`Lugar_DenunciaDAO.java:17`](../../src/model/Lugar_DenunciaDAO.java#L17), [`TramoDAO.java:13`](../../src/model/TramoDAO.java#L13), [`EventoDAO.java:21`](../../src/model/EventoDAO.java#L21), [`Signup.java:53`](../../src/view/Signup.java#L53), [`DenunciaView.java:131`](../../src/view/DenunciaView.java#L131).

Para construir el modelo nuevo tras los pasos y revisiones aprobados:

1. Usar las entidades y consultas como insumo de diseño; documentar cada nombre heredado y su equivalente, ampliación o exclusión justificada en migraciones y ADR. No asumir que la tabla de arriba es un DDL ejecutable.
2. Definir tipos, claves, constraints, índices y semántica de datos a partir de requisitos aprobados y pruebas, no de conjeturas sobre Oracle. Los IDs de las secuencias históricas no están disponibles y no deben reutilizarse como si correspondieran a registros reales.
3. Conservar el esquema lógico de lectura para auditoría documental; crear únicamente catálogos y ejemplos sintéticos identificados como tales y sin datos personales reales.
4. Validar cambios con pruebas de persistencia y acceso; distinguir fallos previos conocidos de requisitos aprobados. No hay información para verificar una migración de usuarios o denuncias desde Oracle.

## 7. Conservación y riesgos

La preservación recomendada es **conceptual y trazable**, no copia literal de las capas `DAO`/`BO`: el legado mezcla interfaz y persistencia, tiene resultados de error ambiguos y no completa la creación de denuncias. Cualquier movimiento de `src/`, `nbproject/`, JAR o imágenes debe planificarse y revisarse por separado; este documento no mueve ni elimina nada. Mantener la historia Git existente y no reescribirla como efecto automático del trabajo.

Hay credenciales y claves en fuentes heredadas y un recurso de licencia empaquetado, cuya titularidad, vigencia y condiciones siguen sin acreditarse. No copiarlas a documentación, configuración nueva, capturas, semillas ni pruebas; no utilizarlas para probar servicios. La mera eliminación de una cadena de la versión actual no elimina su presencia en historial o binarios. La revisión de titularidad, rotación y tratamiento del historial pertenece a H0 y a la decisión apropiada del propietario o responsable de las cuentas.

Las imágenes y bibliotecas del legado pueden tener terceros propietarios; su presencia en el repositorio no acredita autorización de redistribución. Antes de reutilizar o redistribuir un recurso, registrar procedencia y condiciones, y mantener avisos aplicables; sustituirlo solo mediante una decisión justificada y revisada. Los ejemplos para demo deben ser sintéticos y etiquetados.

## 8. Decisiones abiertas para seguimiento

1. Para los eventos sin equivalencia directa de la sección 2, ¿cómo conservar su significado sin ampliar o reducir unilateralmente las siete categorías aprobadas? En particular congestión, caída de árbol, eventos deportivos/especiales, material en la vía y «Para vehiculos».
2. Definir la representación coherente de **categoría/impacto**, especialmente cierres total y parcial, cuando una alerta combine un fenómeno físico y una consecuencia en el tránsito.
3. Precisar si nombre, apellido, municipio o la distinción Transportista/Usuario Particular aportan un fin válido y cuáles serían opcionales; no usar fecha de nacimiento ni teléfono como requisito por herencia.
4. Definir cómo se especifica un tramo opcional y qué se muestra de su carretera sin suponer que los 11 nombres existentes constituyen una red vial oficial.
5. Verificar procedencia/licencias de assets y titularidad de credenciales legadas sin imprimirlas; acordar su tratamiento y preservación segura antes de distribuirlos.

Las preguntas son decisiones de dominio/gestión pendientes, no evidencia de que falte recuperar filas de Oracle. La documentación estática no valida funcionamiento, disponibilidad de proveedores ni cobertura geográfica.
