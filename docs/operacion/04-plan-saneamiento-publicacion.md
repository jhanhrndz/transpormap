# Saneamiento previo a la publicación de devlop

**Estado:** limpieza de Oracle, MapTiler y JxBrowser aplicada a las historias de `main` y `devlop`, por autorización excepcional del propietario. El 20 de noviembre de 2026 ambas ramas saneadas se actualizaron en GitHub. No se ha confirmado revocación de claves ni purga de cachés, forks o clones. Una publicación anterior fue rechazada por la revisión automática; la actualización posterior se realizó tras sanear y verificar las referencias. No reproducir valores en chats, issues, capturas ni logs.

## Situación y respaldo

- `main` apunta a la raíz saneada y sigue siendo ancestro de `devlop`. El propietario autorizó modificar `main` exclusivamente para retirar datos expuestos; el desarrollo no se ha unido a esa rama. `origin/main` se actualizó a la misma raíz saneada.
- Las ramas `respaldo/main-antes-de-reescritura`, `respaldo/devlop-antes-de-reescritura` y `respaldo/devlop-antes-de-saneamiento` son referencias **locales** que conservan el historial original con datos sensibles. No deben publicarse ni usarse como destino de un push. No usar `git push --all` ni `git push --mirror`, pues incluirían esas referencias.
- Se eliminaron rutas de perfil conocidas en documentos históricos de las dos ramas saneadas; se excluyeron de esas historias `nbproject/private/private.xml` y `private.properties`. Los dos archivos se conservaron únicamente en el equipo y quedaron ignorados en `devlop`. Los respaldos y reflogs locales, y posibles copias externas, pueden seguir conteniendo las rutas originales.
- En el legado ya publicado en `main` se detectaron literales de conexión Oracle en `src/connectionbd/ConnectionOracle.java`, una clave de mapa en `src/mapa/Mapa.java` y material de licencia JxBrowser. El propietario indicó que las credenciales de Oracle y mapa eran antiguas, de uso local, que ya no conserva la base y que no recuerda los accesos; autorizó retirar ambos valores. En las historias saneadas de `main` y `devlop` se reemplazaron las asignaciones literales por configuración externa y se retiraron los dos `.class` que las contenían. **No se ha confirmado que las credenciales estén revocadas o sean inválidas**; el commit histórico anterior persiste en respaldos locales y podría existir en cachés, clones o forks. El propietario autorizó retirar JxBrowser: se eliminaron la licencia incrustada, los cuatro JAR y compilados afectados de las ramas saneadas; el botón heredado muestra un aviso sin ocultar la ventana principal. Tratar los valores como expuestos, sin incluirlos aquí.
- La revisión de secretos fue heurística y no garantiza que no existan otros datos sensibles o de licencia.

## Acciones que requieren al titular de las cuentas

1. Identificar a quién pertenece cada credencial y si aún proporciona acceso o genera costos. Si está activa, revocarla o rotarla en el proveedor; revisar el uso de la cuenta y límites de facturación cuando corresponda. La contraseña de Oracle solo debe sustituirse o revocarse si esa instalación o credencial aún se usa; la BD perdida no acredita su vigencia. No compartir los valores por chat.
2. Revisar la clave de MapTiler en la cuenta correspondiente y deshabilitarla o sustituirla si aún está activa. Consultar al proveedor sobre el tratamiento de la licencia de JxBrowser si sigue vigente; no asumir que publicar el binario o la licencia está permitido.
3. Registrar únicamente estado y fecha de cada acción: activa/revocada/no identificada, sin valores. La falta de acceso a una cuenta debe permanecer como riesgo abierto; no se interpreta como revocación.

[GitHub recomienda revocar o rotar primero los secretos expuestos](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/removing-sensitive-data-from-a-repository). Quitar una cadena de la última versión no invalida una credencial ni elimina copias ya existentes.

## Alternativas para la rama local

| Alternativa | Resultado | Consecuencias |
|---|---|---|
| Reescribir solo los commits locales posteriores a `main` | Retirar rutas locales de los commits de desarrollo y conservar la secuencia de cambios. | Cambian los hashes de esos commits; hay que verificar cada snapshot y el diff resultante. El historial de `main`, incluidas las credenciales heredadas, sigue igual. |
| Reconstruir `devlop` desde `main` con un snapshot limpio | Rama de desarrollo con un historial nuevo y más corto. | Pierde la granularidad de los commits actuales y **no elimina los datos del ancestro `main`**. No adoptar este camino como medio de evadir la revisión de seguridad. |
| Mantener historial local y corregir solo la versión actual | Conserva los hashes actuales. | Las rutas siguen en commits anteriores; no resuelve la objeción que bloqueó la publicación. |

La autorización posterior del propietario permitió reescribir la raíz de `main`, necesaria para retirar los valores heredados de las historias de ambas ramas. Se conservaron los 30 commits de la historia original de `devlop`, incluida la raíz, y su relación de ancestro; se añadieron commits de saneamiento en `devlop`. La comprobación independiente no encontró los valores conocidos de Oracle, MapTiler ni JxBrowser en los commits revisados, ni los tres en los JAR conservados examinados por flujo. Esto no acredita ausencia de otras credenciales o de material codificado. Las pruebas locales habituales de backend y frontend pasaron; también finalizó correctamente el trabajo remoto que ejecuta `verify -Ppostgis-it`, aunque no se obtuvieron logs detallados para verificar el conteo de pruebas. Android no se ha acreditado. Los respaldos conservan deliberadamente datos originales y no son evidencia de limpieza.

La actualización de ramas **no invalida por sí sola** las credenciales ya publicadas. La escritura remota se autorizó y efectuó tras comprobar sus referencias; aún falta coordinar cualquier copia existente y, si procede, tratar referencias o cachés externas. Reescribir historia modifica hashes y puede afectar clones y PR; conservar el historial público original intacto y purgarlo son objetivos incompatibles. Ningún proceso local debe emplearse para eludir una denegación automática.

## Puerta de publicación

1. Completar o registrar con precisión el estado de las credenciales y licencias heredadas; no asumir que están revocadas.
2. Revisar todo el historial de desarrollo que se pretenda publicar, incluidos datos personales y archivos binarios; verificar también las referencias ancestras y no incluir el respaldo local.
3. Revisar el diff final, ejecutar las pruebas disponibles y comprobar que `main` sigue intacta. El test PostGIS requiere un runtime Docker o el runner remoto y no se considera aprobado hasta ejecutarlo con éxito.
4. Solicitar nuevamente la operación de publicación únicamente cuando se haya mitigado el riesgo y el sistema de permisos lo permita. La autorización del propietario no anula una denegación automática; no intentar rutas alternativas para eludirla.

El remoto y el workflow se actualizaron el 20 de noviembre de 2026. Los respaldos originales conservan los datos retirados; la revocación y la limpieza de copias/cachés externas no están acreditadas.
