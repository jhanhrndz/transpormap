# ADR 005 — Identidad y fotos protegidas

**Estado:** propuesto. **Contexto:** ninguna alerta o foto accesible sin sesión; foto opcional offline. **Opciones:** Supabase Auth + Storage privado; Spring Security propio con Cloudinary mediado; Firebase Auth. **Decisión candidata:** identidad Supabase validada en Spring, archivos privados y mediación del backend. **Consecuencias:** dependencia, cuotas y correo sin validar; una URL temporal es portable y no equivale a revocación inmediata. Cloudinary queda alternativa, no descartada; no exponer fotos mediante enlaces públicos.
