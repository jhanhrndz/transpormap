# Cliente de TransporMap

Incremento local parcial: existe una base React con una página que identifica la aplicación como demostración en construcción. No hay inicio de sesión, mapa, transporte, almacenamiento persistente ni sincronización sin conexión. La preparación de una alerta captura en memoria un cuerpo y una clave estables; por sí sola no autoriza un reintento. Antes del envío debe integrarse la persistencia aislada por cuenta y la gestión de resultados inciertos.

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
