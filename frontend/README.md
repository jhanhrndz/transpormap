# Cliente de TransporMap

Incremento local parcial: la preparación de una alerta captura en memoria un cuerpo y una clave estables. Todavía no existe interfaz, transporte, almacenamiento persistente ni sincronización sin conexión. La preparación por sí sola no autoriza un reintento: antes del envío debe integrarse la persistencia aislada por cuenta y la gestión de resultados inciertos prevista para H5.

La comprobación de formato RFC 3339, fecha real y límite futuro de la hora de observación queda pendiente de integración con el esquema y la validación del servidor. El cliente actualmente solo rechaza una hora vacía y no cambia el valor proporcionado. Las demás validaciones del servidor siguen siendo obligatorias.

Las pruebas parciales pueden ejecutarse con Node 22.17 mediante `node --experimental-strip-types --test frontend/src/alerts/prepareAlert.test.ts` desde la raíz. Esta opción de Node es experimental y aún no hay una comprobación estática TypeScript configurada.
