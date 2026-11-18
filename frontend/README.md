# Cliente de TransporMap

Incremento local parcial: la preparación de una alerta captura en memoria un cuerpo y una clave estables. Todavía no existe interfaz, transporte, almacenamiento persistente ni sincronización sin conexión. La preparación por sí sola no autoriza un reintento: antes del envío debe integrarse la persistencia aislada por cuenta y la gestión de resultados inciertos prevista para H5.

El cliente comprueba formato, calendario y rango de zona de la hora de observación sin cambiar el valor proporcionado. Esta comprobación es preliminar: acepta sintácticamente el segundo 60 sin verificar si corresponde a un segundo intercalar real. La validación completa RFC 3339 y el límite futuro quedan pendientes de integración con el esquema y la validación del servidor. Las demás validaciones del servidor siguen siendo obligatorias.

Antes de habilitar envíos se debe acordar y probar el perfil temporal entre cliente y servidor: algunos analizadores Java restringen los desplazamientos a ±18 horas y las fracciones a nueve dígitos, mientras esta comprobación acepta el rango sintáctico y precisión de RFC 3339. No se deben transformar valores silenciosamente para resolver diferencias.

Las pruebas parciales pueden ejecutarse con Node 22.17 mediante `node --experimental-strip-types --test frontend/src/alerts/prepareAlert.test.ts` desde la raíz. Esta opción de Node es experimental y aún no hay una comprobación estática TypeScript configurada.
