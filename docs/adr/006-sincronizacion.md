# ADR 006 — Sincronización y novedades

**Estado:** propuesto. **Contexto:** reportes/fotos offline, sesión local siete días y novedades rápidas en cliente abierto. **Opciones:** cola local idempotente más sondeo incremental; cola más WebSocket/SSE. **Decisión candidata:** cola y sondeo mientras cliente abierto; notificaciones cerradas sujetas a viabilidad. **Consecuencias:** sondeo consume cuota y puede mantener servicios activos; no promete envío con cliente cerrado ni revocación remota sin conexión. Medir 10 s en condiciones RNF-01 y validar seguridad local.
