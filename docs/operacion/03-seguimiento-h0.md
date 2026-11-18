# Seguimiento H0 — TransporMap

Plan: [03-plan-y-roadmap.md](../03-plan-y-roadmap.md), aprobado por JhanDev. H0 está en curso; este registro no declara terminado el hito ni autoriza costes.

| Trabajo | Estado | Evidencia y siguiente paso |
|---|---|---|
| Preservar antecedentes | En curso | Documentos de fases previas sin seguimiento Git; no se han movido ni eliminado. Rama inicial `main`, commit `ab8ed7d`. |
| Aislamiento | Parcial | Worktree nativo creado fuera de la raíz escribible y sin copiar documentos no versionados; no se usa para editar hasta resolver permisos y traslado seguro. |
| Trazabilidad de legado | Documentada; revisión independiente | [Matriz](../descubrimiento/04-trazabilidad-legado.md); 14 eventos y 11 rutas contrastados. Las equivalencias inciertas están pendientes de decidir. |
| Entorno local | Diagnóstico inicial | [Preflight](01-preflight-entorno-local.md): Java/Node/npm.cmd disponibles; Docker/Maven no encontrados en PATH; no hubo compilación o tests. |
| Proveedores y coste | Investigación documental; sin cuenta probada | [Preflight demo](00-preflight-demo.md): controles de cuenta, correo, acceso privado, cuotas y ausencia de pagos requieren pruebas autorizadas. |
| Identidad durable y respaldos | Riesgos y opciones documentados | [Análisis](02-identidad-sincronizacion-respaldo.md). No se ha validado una solución para reintentos muy tardíos frente a eliminación, ni backup BD+objetos con RPO/RTO y rotación acordados. |
| Contrato OpenAPI | En revisión | [Borrador](../api/openapi-propuesta.yaml); validar 3.1 estructuralmente y resolver huecos de sincronización y autorización antes de iniciar los endpoints. |
| Licencias, assets y secretos | Pendiente | Identificar origen y licencia de assets, titulares de claves y tratamiento autorizado. No publicar valores, usar credenciales heredadas ni reescribir historial automáticamente. |
| Herramientas y versiones | Parcial | JDK 25 y Node 22 detectados; versiones finales deben acordarse con compatibilidad y proveedor. Verificar runtime de contenedores; no se instala ni modifica sistema durante diagnóstico. |
| Revisión y evidencias | En curso | Revisar cambios de cada tarea independientemente. CI, pruebas físicas y prueba de restauración no se han ejecutado. |

H1 y una demo desplegada siguen condicionados a cerrar las puertas pertinentes de H0. La aprobación de Fase 4 no supone que esas puertas ya estén superadas. Continuar trabajo independiente seguro y sin coste mientras se resuelven.
