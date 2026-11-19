# Cliente de TransporMap

Incremento local parcial: existe una base React con una página que identifica la aplicación como demostración en construcción. No hay inicio de sesión, mapa, transporte ni sincronización sin conexión. Existe un adaptador IndexedDB para guardar intentos aislados por identidad, reclamarlos y marcar explícitamente resultados inciertos, pero todavía no está conectado a la interfaz ni verifica la identidad del llamador. Antes del envío debe integrarse con una sesión validada, recuperación tras interrupciones y gestión de resultados inciertos.

El cliente comprueba formato, calendario y rango de zona de la hora de observación sin cambiar el valor proporcionado. Esta comprobación es preliminar: acepta sintácticamente el segundo 60 sin verificar si corresponde a un segundo intercalar real. La validación completa RFC 3339 y el límite futuro quedan pendientes de integración con el esquema y la validación del servidor. Las demás validaciones del servidor siguen siendo obligatorias.

Antes de habilitar envíos se debe acordar y probar el perfil temporal entre cliente y servidor: algunos analizadores Java restringen los desplazamientos a ±18 horas y las fracciones a nueve dígitos, mientras esta comprobación acepta el rango sintáctico y precisión de RFC 3339. No se deben transformar valores silenciosamente para resolver diferencias.

## Desarrollo local

El proyecto requiere npm y Node `^22.13.0`, `^24.0.0` o `>=26.0.0`. Estos rangos reflejan la intersección conservadora de las dependencias fijadas, incluidas las opcionales; se comprobó localmente con Node 22.17.1. Desde esta carpeta:

```powershell
npm.cmd ci
npm.cmd test
npm.cmd run build
npm.cmd run dev
```

`test` ejecuta las pruebas con Vitest; `build` comprueba los tipos y compila el cliente con Vite. Las dependencias se instalan localmente y sus versiones están fijadas en el archivo de bloqueo. El servidor de desarrollo no proporciona por sí solo la API, la autenticación ni el funcionamiento offline.

Las pruebas del adaptador usan `fake-indexeddb`, implementación en memoria para pruebas (Apache-2.0); no demuestran persistencia real, cuota, desalojo ni concurrencia en navegadores o Android. La clave por cuenta evita mezclar registros en las operaciones del adaptador, pero no protege frente a código malicioso del mismo origen ni sustituye autenticación y autorización.

El propietario `(issuer, subject)` lo proporciona el código que llama al adaptador: la clave compuesta filtra los registros, pero no es una frontera de seguridad contra XSS ni contra otro usuario o código con acceso a los datos del mismo origen. Antes de integrarlo se debe obtener esa identidad desde una sesión validada y aplicar la política de acceso en todas las rutas.

El adaptador no implementa los estados de confirmación ni de revisión manual, y no recupera automáticamente operaciones tras un reinicio. La transición `markUncertain` es explícita y podría interferir con un envío aún activo en otra pestaña; se requiere coordinación y pruebas entre pestañas antes de conectarla a una operación de red. Tampoco existe todavía un protocolo para determinar el resultado de un envío incierto ni se permite interpretar la ausencia de una respuesta como prueba de que no se publicó.

Existe un adaptador experimental de Web Locks que calcula un nombre estable mediante SHA-256 de la identidad y el identificador de reporte, y solo ejecuta el trabajo con bloqueo exclusivo disponible. El hash es un identificador seudónimo, no una garantía de secreto. Si Web Locks o el cálculo del hash no están disponibles, no se ejecuta el trabajo. Un coordinador experimental usa ese bloqueo para reclamar un intento, mantenerlo durante una tarea asíncrona y marcarla incierta, o para marcar explícitamente un intento interrumpido. Nunca lo marca confirmado y no incorpora transporte ni recuperación automática. Los métodos del almacenamiento siguen siendo accesibles directamente, por lo que el coordinador no impone exclusividad global; antes de integrar envíos deben encapsularse las rutas y verificar el protocolo en navegador. Sus pruebas usan `fake-indexeddb` y un doble de LockManager, no dos pestañas reales. El bloqueo no permite inferir si el servidor guardó una alerta.

El coordinador relee el intento reclamado bajo el bloqueo y entrega al callback `work` solo una copia inmutable del cuerpo y la clave persistidos. Esto no comprueba la identidad del llamador ni obliga al callback a usar esos valores. Antes de conectar transporte se debe vincular a una sesión verificada y asegurar que ninguna ruta acceda directamente a los métodos que eluden el bloqueo. La implementación actual no habilita envíos reales.
