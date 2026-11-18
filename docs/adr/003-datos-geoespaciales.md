# ADR 003 — Datos y consultas geográficas

**Estado:** propuesto. **Contexto:** alertas a 1 km de línea y 20 km de punto con base perdida. **Opciones:** PostgreSQL/PostGIS; MySQL espacial; Supabase o Neon como hosting. **Decisión candidata:** PostGIS `geography` en metros e índice, Supabase Free a validar. **Consecuencias:** capacidades espaciales robustas con aprendizaje adicional; pausa, cuota y copias sin resolver. Reconstrucción del esquema, sin migración de filas Oracle inexistentes.
