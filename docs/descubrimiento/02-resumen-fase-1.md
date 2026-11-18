# TransporMap — Resumen de descubrimiento para revisión

Estado: **Fase 1 aprobada por JhanDev el 18 de noviembre de 2026**. El detalle y la trazabilidad de cada respuesta se encuentran en [decisiones y pendientes](01-decisiones-y-pendientes.md).

## Propósito

Crear un producto público y profesional de portafolio para consultar y compartir alertas sobre carreteras colombianas, dirigido principalmente a transportistas. JhanDev será el administrador inicial. Java con Spring Boot es obligatorio; las demás tecnologías se decidirán después de comparar alternativas y condiciones gratuitas vigentes.

## Alcance acordado hasta ahora

- Solo los usuarios registrados con sesión iniciada pueden consultar, publicar y confirmar alertas. La consulta sin conexión de alertas guardadas deberá respetar este requisito; su mecanismo de sesión se definirá posteriormente.
- Cada publicación corresponde a una alerta. No habrá comentarios ni publicaciones generales en el alcance acordado.
- El catálogo inicial incluye mal estado, cierre total o parcial, accidente, derrumbe/inundación, obras y restricción de carga. El detalle de campos y reglas de vigencia queda pendiente para requisitos.
- La comunidad puede indicar «Sigue vigente» o «Ya no está», con una respuesta actual por usuario que puede cambiar. Las confirmaciones son señales comunitarias, no una certificación de verdad.
- Se consultan alertas guardadas sin conexión y se pueden preparar reportes pendientes. Sin red no se pueden recibir nuevos reportes ni transmitir los pendientes. La descarga regional de mapas queda fuera del alcance inicial.
- Se prioriza la actualización rápida con la aplicación abierta y avisos sobre zonas elegidas. El objetivo propuesto de diez segundos requiere condiciones de medición y verificación de viabilidad; no es una garantía. Los avisos con aplicación cerrada quedan condicionados a viabilidad gratuita.
- Se incluyen fotografías opcionales. Cloudinary es candidato propuesto por JhanDev, sujeto a comparación en arquitectura; la fotografía de un reporte offline debe esperar en el dispositivo hasta poder enviarse.
- La consulta debe cubrir alertas sobre una ruta entre A y B, con cercanía como alternativa cuando no exista una ruta disponible. Esto no implica navegación giro a giro.
- Se favorece la consulta antes del viaje y la publicación con el conductor detenido. No se ha solicitado navegación giro a giro.
- El administrador podrá atender abuso, retirar contenido y suspender cuentas registrando el motivo; también visualizará métricas. Las definiciones exactas de métricas y permisos siguen pendientes.
- Se emplearán herramientas gratuitas para un producto de portafolio inicialmente no comercial; se priorizarán proveedores sin tarjeta ni cargos automáticos y se acepta suspensión o arranque lento. La disponibilidad es de aproximadamente diez horas semanales, sin fecha límite.
- Se aprobó mostrar un alias y el punto del incidente a usuarios autenticados, manteniendo privados correo, teléfono y trayectorias y sin seguimiento continuo. Interfaz y manual en español; README en español con resumen en inglés.

## Límites y decisiones pendientes

No se ha identificado un corredor ni transportistas disponibles para validar en campo. Se documentará esa limitación y se distinguirán datos sintéticos de actividad real. Las pruebas técnicas no equivalen a validación con usuarios.

Dispositivos de prueba disponibles, según el propietario: Tecno Camon 20 Pro 4G con Android 14 y portátil HP Pavilion Gaming 15-dk10000 (8VD47AV) con 24 GB de RAM y GTX 1650 Ti de 4 GB de VRAM. Los navegadores y el espacio disponible se verificarán al definir la matriz de pruebas.

La expiración y cierre, resolución de confirmaciones contradictorias, campos y permisos, retención, contenido de métricas y objetivos de rendimiento deben concretarse en la propuesta de requisitos. También hay que definir el estado de una fotografía pendiente o rechazada y la selección del trayecto y radio de búsqueda. Sus detalles aún no cuentan como aprobados.

No se ha elegido PWA, aplicación nativa, base de datos, proveedor cartográfico o alojamiento. Tampoco se ha acreditado que todos los requisitos sean posibles con costo cero o con disponibilidad continua. Esas decisiones y límites se investigarán durante arquitectura.

## Checkpoints

Fase 1 aprobada. Se preparan requisitos para aprobación (Fase 2); después se propondrán arquitectura comparada y ADR para aprobación (Fase 3), y planificación y roadmap para aprobación explícita (Fase 4). No se escribirá código de producción antes de esa aprobación.
