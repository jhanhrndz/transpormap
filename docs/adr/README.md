# ADR de arquitectura — propuestas de Fase 3

Estado general: **dirección aprobada de forma condicionada**, con proveedores y pruebas de viabilidad pendientes; el despliegue será una demo de portafolio, no operación vial de producción. El propietario aprobó la Fase 4 y autorizó la implementación; esto no autoriza gastos ni acredita proveedores. Los detalles nuevos de [frontend](../05-arquitectura-detallada-frontend.md) y [backend](../06-arquitectura-detallada-backend.md) están propuestos para revisión; los demás ADR pueden conservar propuestas concretas pendientes. Las condiciones y fuentes están en [stack y arquitectura](../02-stack-y-arquitectura.md).

| ADR | Decisión | Estado y consecuencia clave |
|---|---|---|
| [001](001-plataforma-cliente.md) | PWA React/TypeScript | Sujeta a pruebas reales de offline y fotos en Android; iOS no verificado. |
| [002](002-backend-modular.md) | Spring Boot monolito modular | Una unidad de despliegue; módulos y adaptadores sustituibles. |
| [003](003-datos-geoespaciales.md) | PostgreSQL/PostGIS en Supabase candidato | Pausa, cuota y backup externo pendiente. |
| [004](004-mapas-rutas.md) | Geoapify candidato para mapa y ruta | Cuota compartida y cobertura no verificada; fallback de cercanía. |
| [005](005-identidad-fotos.md) | Supabase Auth/Storage candidato con validación Spring | Fotos privadas; URL temporal no sustituye autorización revocable. |
| [006](006-sincronizacion.md) | Cola local idempotente y sondeo incremental | No garantiza sincronización con app cerrada ni revocación offline. |
| [007](007-despliegue-operacion.md) | Render/Pages y GitHub Actions candidatos | Render Free no recomendado para producción y cargos por cuota aún sin control probado. |
| [008](008-datos-respaldo.md) | Respaldo independiente de BD y objetos | Destino gratuito, RPO/RTO y restauración no demostrados. |
