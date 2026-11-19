# Seguimiento H0 — TransporMap

Plan: [03-plan-y-roadmap.md](../03-plan-y-roadmap.md), aprobado por JhanDev. H0 está en curso; este registro no declara terminado el hito ni autoriza costes.

| Trabajo | Estado | Evidencia y siguiente paso |
|---|---|---|
| Preservar antecedentes | En curso | La documentación y el esqueleto del backend están registrados en `devlop`; el legado permanece como referencia. `main` conserva el commit inicial `ab8ed7d`. |
| Rama de desarrollo | Preparada | Por decisión del propietario se trabaja directamente en la raíz, en la rama `devlop`; se retiraron el worktree y la rama temporal. `main` se mantiene sin cambios hasta terminar el proyecto. |
| Trazabilidad de legado | Documentada; revisión independiente | [Matriz](../descubrimiento/04-trazabilidad-legado.md); 14 eventos y 11 rutas contrastados. Las equivalencias inciertas están pendientes de decidir. |
| Entorno local | Diagnóstico parcial | [Preflight](01-preflight-entorno-local.md): Java/Node/npm.cmd disponibles; Docker y Maven de sistema no encontrados en PATH. El backend se compiló y probó localmente con Maven Wrapper; Testcontainers continúa pendiente. |
| Proveedores y coste | Investigación documental; sin cuenta probada | [Preflight demo](00-preflight-demo.md): controles de cuenta, correo, acceso privado, cuotas y ausencia de pagos requieren pruebas autorizadas. |
| Identidad durable y respaldos | Regla de revisión manual aceptada; implementación pendiente | [Análisis](02-identidad-sincronizacion-respaldo.md). El propietario aceptó conservar borrador y pedir revisión manual cuando se desconozca si el envío ya fue publicado; falta demostrar cómo detectar ese estado. Backup BD+objetos con RPO/RTO y rotación tampoco validado. |
| Contrato OpenAPI | Validación estructural mínima aprobada; diseño abierto | [Borrador](../api/openapi-propuesta.yaml) y [notas](../api/notas-contrato-h0.md). Redocly CLI 2.79.0, perfil `minimal`, salida 0 sin errores ni advertencias; revisión independiente confirmó correcciones de snapshot/fotos/acceso. Faltan validación semántica y decisiones de sincronización/autorización. No se ha probado una API implementada. |
| Licencias, assets y secretos | Pendiente | Identificar origen y licencia de assets, titulares de claves y tratamiento autorizado. No publicar valores, usar credenciales heredadas ni reescribir historial automáticamente. |
| Herramientas y versiones | Parcial | JDK 25 y Node 22 detectados; versiones finales deben acordarse con compatibilidad y proveedor. WSL indica que no está instalado; Docker Desktop no aparece en PATH o ubicación habitual. Preparar runtime para Testcontainers requiere evaluar instalación/privilegios y no se ha modificado el sistema. |
| Revisión y evidencias | En curso | Se ha preparado un workflow de CI para frontend y backend; todavía no se ha ejecutado en GitHub. Las pruebas físicas y la prueba de restauración siguen pendientes. |
| Preparación H1 | Implementación parcial | [Backend](../planes/h1-backend.md), [cliente](../planes/h1-cliente.md) e [intentos de publicación](../planes/h1-intentos-publicacion.md): el backend pasó 29 pruebas y `verify` local; la base React/TypeScript compila y pasó 21 pruebas, incluidas pruebas simuladas de IndexedDB y Web Locks. No hay aún flujo de alertas completo ni demo. |

H1 y una demo desplegada siguen condicionados a cerrar las puertas pertinentes de H0. La aprobación de Fase 4 no supone que esas puertas ya estén superadas. Continuar trabajo independiente seguro y sin coste mientras se resuelven.
