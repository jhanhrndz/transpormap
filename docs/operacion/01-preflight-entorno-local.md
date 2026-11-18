# H0 — Preflight del entorno local

Estado: diagnóstico inicial actualizado con los avances locales. No acredita compatibilidad completa ni ejecución de pruebas con contenedores; no se ha modificado la configuración del sistema.

| Componente | Observación | Implicación |
|---|---|---|
| Git | 2.50.1 Windows; `main` conserva el commit inicial `ab8ed7d`. | Por decisión del propietario, el desarrollo continúa en `devlop`, directamente en la raíz. La documentación y el código nuevo ya están versionados. |
| JDK | `java` y `javac` 25.0.2; distribuidor Oracle, Windows amd64. | Es un candidato local, no decisión definitiva de versión o distribución ni prueba de compatibilidad con todas las dependencias. |
| Node.js | 22.17.1. | Está disponible; comprobar versión soportada y actualizaciones de seguridad para el stack elegido. |
| npm | `npm.cmd --version`: 11.21.2. | `npm` invoca `npm.ps1`, que PowerShell bloquea por política de scripts; `npm.cmd` funciona. No cambiar política global para solucionar esto. |
| Docker, Maven, Gradle, Ant | No encontrados por `Get-Command` en PATH. | No demuestra que no estén instalados en otra ruta. Sin Docker accesible aún no se pueden comprobar tests Testcontainers/PostGIS. El backend usa Maven Wrapper; falta fijar y comprobar el hash de su distribución. Ant no probado. |
| WSL / contenedores | `wsl.exe --status` respondió que el Subsistema de Windows para Linux no está instalado; no se encontró Docker Desktop en `C:\Program Files\Docker\Docker\resources\bin\docker.exe` ni Podman en la ruta habitual. | Confirmar opciones de instalación o runtime alternativo antes de pruebas Testcontainers. Instalar WSL/Docker modifica el sistema y puede requerir privilegios o reinicio; no se ha instalado ni modificado la configuración. |
| Python | El alias `python.exe` del entorno no se pudo ejecutar en el análisis anterior. | No se asume disponible para validar OpenAPI; buscar herramienta apropiada en H0. |
| Sistema | Propiedades Java indican Windows 11 amd64. | Propietario informa 24 GB RAM y GTX 1650 Ti, no verificados mediante CIM: la consulta recibió acceso denegado. No hace falta escalar permisos solo para corroborarlo. |
| Ubicación de trabajo | La carpeta aislada utilizada inicialmente fue retirada a petición del propietario después de trasladar su historial a `devlop`. | El trabajo continúa en la raíz del proyecto. `main` no ha sido modificada. |

## Versiones y capacidades aún por confirmar

- Consulta de `https://start.spring.io/metadata/client` ejecutada en H0 con acceso autorizado: `bootVersion.default` **4.1.2.RELEASE**, con 4.0.9 estable también ofrecida y snapshots/RC separados. La [documentación de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html) de 4.1.2 requiere Java 17 como mínimo y declara compatibilidad hasta Java 27; Java 25.0.2 instalado entra en ese rango. Se generó el esqueleto Spring Boot 4.1.2 y pasaron sus pruebas locales; esto no certifica todas las dependencias ni límites de memoria del hosting.
- [Node.js publica el ciclo de soporte](https://nodejs.org/en/about/previous-releases); la versión local no debe actualizarse implícitamente. Evaluar uso de una LTS compatible en el proyecto y el impacto antes de modificar herramientas locales.
- Pruebas con contenedores requieren Docker u otro runtime compatible, virtualización y recursos. Consultar [requisitos de Docker para Windows](https://docs.docker.com/desktop/setup/install/windows-install/) y verificar disponibilidad real antes de elegir instalación. Las pruebas locales del esqueleto no sustituyen las pruebas de integración con PostGIS.
- El smartphone Android 14 y sus navegadores deben verificarse en pruebas físicas. No se ha conectado ni modificado el dispositivo.
- El contrato OpenAPI fue validado en H0 con Redocly CLI 2.79.0 y perfil `minimal` sin errores ni advertencias; esto comprueba su estructura con ese conjunto de reglas, no todos los aspectos semánticos ni el comportamiento de una aplicación inexistente. Se usó descarga temporal en caché y no se añadieron dependencias al proyecto.
