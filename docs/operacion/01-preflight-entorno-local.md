# H0 — Preflight del entorno local

Estado: diagnóstico inicial; no acredita construcción, pruebas ni compatibilidad completas. Solo se consultaron comandos y propiedades locales; no se modificó la configuración del sistema ni se instalaron dependencias.

| Componente | Observación | Implicación |
|---|---|---|
| Git | 2.50.1 Windows; rama inicial `main`, commit `ab8ed7d`. | Los documentos de fases previas están sin seguimiento; preservarlos y revisarlos antes de cualquier commit o traslado. No reescribir historia. |
| JDK | `java` y `javac` 25.0.2; distribuidor Oracle, Windows amd64. | Es un candidato local, no decisión definitiva de versión o distribución ni prueba de compatibilidad con todas las dependencias. |
| Node.js | 22.17.1. | Está disponible; comprobar versión soportada y actualizaciones de seguridad para el stack elegido. |
| npm | `npm.cmd --version`: 11.21.2. | `npm` invoca `npm.ps1`, que PowerShell bloquea por política de scripts; `npm.cmd` funciona. No cambiar política global para solucionar esto. |
| Docker, Maven, Gradle, Ant | No encontrados por `Get-Command` en PATH. | No demuestra que no estén instalados en otra ruta. Sin Docker accesible aún no se pueden comprobar tests Testcontainers/PostGIS; Maven puede incorporarse mediante wrapper después de validar procedencia e instalación de proyecto. Ant no probado. |
| Python | El alias `python.exe` del entorno no se pudo ejecutar en el análisis anterior. | No se asume disponible para validar OpenAPI; buscar herramienta apropiada en H0. |
| Sistema | Propiedades Java indican Windows 11 amd64. | Propietario informa 24 GB RAM y GTX 1650 Ti, no verificados mediante CIM: la consulta recibió acceso denegado. No hace falta escalar permisos solo para corroborarlo. |
| Git worktree | Herramienta nativa creó `[ruta-local]\.codex\worktrees\b7f7\transpormap`; el checkout actual permanece en la raíz del proyecto. | La nueva ruta está fuera del workspace autorizado de escritura; los documentos sin seguimiento **no se copian** automáticamente. No copiar, editar, ni trabajar allí sin permisos del entorno. Determinar la forma autorizada de llevar los documentos y aislar implementación sin perder trabajo. |

## Versiones y capacidades aún por confirmar

- [Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html) documenta compatibilidad de la versión estable consultada con Java 17 en adelante; esto no certifica los complementos ni los límites de memoria del hosting. Selección exacta debe basarse en versión estable soportada, compatibilidad de librerías, despliegue y verificación de fuente vigente al crear el proyecto.
- [Node.js publica el ciclo de soporte](https://nodejs.org/en/about/previous-releases); la versión local no debe actualizarse implícitamente. Evaluar uso de una LTS compatible en el proyecto y el impacto antes de modificar herramientas locales.
- Pruebas con contenedores requieren Docker u otro runtime compatible, virtualización y recursos. Consultar [requisitos de Docker para Windows](https://docs.docker.com/desktop/setup/install/windows-install/) y verificar disponibilidad real antes de elegir instalación. No asumir que las pruebas existentes pasan: el legado no incluye tests y no se inició una compilación.
- El smartphone Android 14 y sus navegadores deben verificarse en pruebas físicas. No se ha conectado ni modificado el dispositivo.
- El contrato OpenAPI está siendo revisado en H0; la búsqueda manual de referencias no sustituye una validación OpenAPI 3.1 real.
