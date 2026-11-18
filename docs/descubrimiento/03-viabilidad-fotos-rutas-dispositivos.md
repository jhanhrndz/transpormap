# Viabilidad preliminar: fotos, rutas y dispositivos

Consulta de fuentes: 17 de noviembre de 2026. Este análisis informa el descubrimiento; no decide el stack de Fase 3 ni demuestra funcionamiento en los dispositivos.

## Fotografías y Cloudinary

Cloudinary es una candidata razonable que se comparará con al menos otra alternativa en arquitectura. Su [documentación de planes](https://cloudinary.com/documentation/billing_and_plans) anuncia registro gratuito sin tarjeta y 25 créditos mensuales compartidos entre almacenamiento, transferencias y transformaciones. Un crédito equivale, según el tipo de consumo, a 1 GB almacenado, 1 GB transferido de imágenes o 1.000 transformaciones. **No son tres cuotas independientes de 25 unidades**; el uso se suma. Las condiciones exactas y la cuenta concreta deben verificarse de nuevo antes de elegir.

Ante excedentes de créditos base, la misma documentación indica avisos y eventual desactivación si no se actúa; no garantiza servicio ininterrumpido ni afirma un corte inmediato. Las cuotas de complementos pueden comportarse distinto. El plan gratuito puede dejar de alcanzar aun para un proyecto no comercial.

### Flujo candidato

1. La persona detenida crea el reporte y elige una foto opcional. El dispositivo guarda reporte y archivo localmente, con espacio y formato validados. Sin espacio suficiente, debe avisar y no fingir que se guardó.
2. La interfaz muestra «pendiente de conexión», hora de observación y qué archivo está pendiente. La foto no ha llegado a Cloudinary mientras no haya comunicación.
3. Cuando exista red y la plataforma permita sincronizar, se recupera autenticación y se transmiten reporte y foto con reintentos e identidad estable. Se registra el estado y se evitan duplicados y adjuntos huérfanos, incluso con cortes parciales.
4. Propuesta a validar en requisitos: permitir que el texto del reporte se publique aunque falle la foto, indicando de forma visible que la imagen sigue pendiente; permitir reintentar y avisar sobre rechazos. Mantener separadas confirmaciones del servidor y del proveedor. La alternativa es publicar todo junto; afecta frescura del aviso frente a consistencia de la evidencia.
5. Limitar cantidad, tamaño y resolución mediante parámetros configurables, entregar miniaturas y versiones comprimidas, vigilar consumo de cuota y controlar eliminación de archivos temporales y remotos. Las cifras concretas y duración de retención se aprobarán en requisitos.

El estado local no es infalible. Si se eligiera una solución web, el navegador puede desalojar almacenamiento bajo ciertas condiciones y el usuario puede borrarlo; solicitar persistencia no equivale a obtenerla. Las sesiones privadas tienen restricciones propias. [MDN: cuotas y expulsión de datos](https://developer.mozilla.org/en-US/docs/Web/API/Storage_API/Storage_quotas_and_eviction_criteria). La [sincronización web en segundo plano](https://developer.mozilla.org/en-US/docs/Web/API/Background_Synchronization_API) no funciona en todos los navegadores; diseñar reintento cuando la aplicación vuelva a estar abierta y no prometer envío inmediato con la aplicación cerrada.

Para privacidad, se propone eliminar metadatos EXIF/IPTC/XMP innecesarios antes de publicar y verificar tanto originales como imágenes derivadas; las imágenes pueden contener rostros o placas. Cloudinary explica la [eliminación de metadatos](https://cloudinary.com/documentation/user_generated_content_upload) y advierte que las transformaciones de entrada conservan metadatos por defecto salvo configuración adecuada. La configuración debe comprobarse sobre los archivos reales, no suponerse por usar el proveedor. Fotos deben estar sujetas a moderación y eliminación. La [subida firmada](https://cloudinary.com/documentation/ios_image_and_video_upload) permite autorización desde backend sin exponer el secreto al cliente; la integración concreta depende de arquitectura.

## Consulta de alertas por ruta y cercanía

Requisito aprobado: buscar por trayecto A–B y, si no hay ruta, por cercanía. A y B pueden admitir varios trayectos: para identificar alertas sobre uno hace falta su geometría y un margen alrededor, no basta buscar puntos cercanos a los extremos. Ese margen, el origen de la ruta y los mensajes cuando no exista trazado se precisarán en Fase 2.

Propuesta por revisar: presentar el trayecto elegido y alertas próximas a él; si no se selecciona uno o falla su cálculo, ofrecer búsqueda cercana con ubicación autorizada o zona elegida. Si tampoco hay ubicación disponible, permitir selección manual o explicar la limitación. No presentar una alternativa sin informar de que se cambió el criterio.

Buscar incidentes alrededor de un trayecto no equivale a navegación giro a giro ni garantiza que el trayecto sea apto para carga: altura, peso, cierres y restricciones requieren fuentes actualizadas y capacidad explícita. No afirmar que una ruta es segura o apta para camiones sin acreditarlo. Los proveedores, datos y licencias para calcular trazados y la cobertura colombiana se compararán en Fase 3.

Sin conexión no se garantiza calcular un trayecto nuevo; se debe distinguir una ruta previamente guardada de una calculada ahora. Se deben mostrar antigüedad de alertas y datos descargados.

## Dispositivos para pruebas

- **Tecno Camon 20 Pro 4G:** el propietario indica que su equipo tiene Android 14. [TECNO describe la familia CAMON 20 Pro con Android 13](https://www.tecno-mobile.com/id/phones/tech-specs/techspecs/camon-20-pro/); esa ficha no determina la versión instalada en este dispositivo. Navegador, permisos y políticas concretas aún no se han inspeccionado. Es un objetivo inicial para ensayos de interfaz, ubicación, conectividad, cámara y almacenamiento, sujeto a prueba real.
- **HP Pavilion Gaming 15-dk10000 (8VD47AV):** el propietario indica 24 GB de RAM y GTX 1650 Ti con 4 GB de VRAM. [HP identifica la familia](https://support.hp.com/es-es/product/setup-user-guides/hp-pavilion-gaming-15-dk1000-laptop-pc-series/model/2100017897); el entorno compartido muestra Windows. Es una base razonable para desarrollo y administración, aunque la ejecución de contenedores dependerá también de virtualización, CPU, espacio y configuración, sin verificar todavía.
- Estos dos equipos permiten una primera matriz Android/Windows, pero no acreditan compatibilidad con iOS, otros navegadores ni otros teléfonos. Las pruebas reales deben incluir cortes de red, reinicio de la app, espacio bajo, permisos denegados y cargas interrumpidas. No se ha ejecutado ninguna de ellas.
