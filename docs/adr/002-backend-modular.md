# ADR 002 — Backend modular

**Estado:** propuesto. **Contexto:** Spring Boot obligatorio y recursos de una persona. **Opciones:** monolito modular; microservicios; Maven o Gradle para build. **Decisión candidata:** monolito modular y Maven por despliegue y transacciones simples; Java LTS y versión soportada se verificarán. **Consecuencias:** menor complejidad operativa, pero fallos afectan al proceso completo y el uso de memoria/arranque debe medirse. Se conserva trazabilidad de módulos Swing/DAO al dominio actual.
