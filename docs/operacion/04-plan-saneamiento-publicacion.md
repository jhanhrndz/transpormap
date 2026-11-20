# Saneamiento previo a la publicación de devlop

**Estado:** limpieza local de Oracle, MapTiler y JxBrowser aplicada y verificada en `main` y `devlop`, por autorización excepcional del propietario. GitHub no se ha modificado. No se han revocado claves. La publicación anterior fue rechazada por la revisión automática de permisos y permanece detenida. No reproducir los valores encontrados en chats, issues, capturas ni logs.

## Situación y respaldo

- `main` local apunta a la raíz saneada y sigue siendo ancestro de `devlop`. El propietario autorizó modificar `main` exclusivamente para retirar datos expuestos; el desarrollo no se ha unido a esa rama. `origin/main` sigue apuntando al commit público original.
- Las ramas `respaldo/main-antes-de-reescritura`, `respaldo/devlop-antes-de-reescritura` y `respaldo/devlop-antes-de-saneamiento` son referencias **locales** que conservan el historial original con datos sensibles. No deben publicarse ni usarse como destino de un push. No usar `git push --all` ni `git push --mirror`, pues incluirían esas referencias.
- Se eliminaron rutas de perfil conocidas en documentos históricos de las dos ramas saneadas; se excluyeron de esas historias `nbproject/private/private.xml` y `private.properties`. Los dos archivos se conservaron únicamente en el equipo y quedaron ignorados en `devlop`. Las referencias antiguas, reflogs y `origin/main` pueden seguir conteniendo las rutas originales.
- En el legado ya publicado en `main` se detectaron literales de conexión Oracle en `src/connectionbd/ConnectionOracle.java`, una clave de mapa en `src/mapa/Mapa.java` y material de licencia JxBrowser. El propietario indicó que las credenciales de Oracle y mapa eran antiguas, de uso local, que ya no conserva la base y que no recuerda los accesos; autorizó retirar ambos valores. En las historias locales saneadas de `main` y `devlop` se reemplazaron las asignaciones literales por configuración externa y se retiraron los dos `.class` que las contenían. **No se ha confirmado que las credenciales estén revocadas o sean inválidas**, y siguen en el commit histórico público y respaldos antiguos. El propietario autorizó retirar JxBrowser: se eliminaron la licencia incrustada, los cuatro JAR y compilados afectados de las ramas saneadas; el botón heredado muestra un aviso sin ocultar la ventana principal. Tratar los valores como expuestos, sin incluirlos aquí.
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

La autorización posterior del propietario permitió reescribir localmente también la raíz de `main`, necesaria para retirar los valores heredados de las historias de ambas ramas. Se conservaron los 30 commits de la historia original de `devlop`, incluida la raíz, y su relación de ancestro; se añadió un commit en `devlop` para ignorar la configuración privada. La comprobación independiente no encontró los valores de Oracle/MapTiler ni rutas de perfil conocidas en las referencias saneadas. Se examinaron JAR y el contenido descomprimido de un 7z anidado buscando únicamente esos valores; para el 7z se usó un archivo temporal que fue eliminado y una comparación por flujo. Esto no comprueba otras credenciales ni la licencia JxBrowser. Las pruebas locales habituales de backend y frontend pasaron; no se acreditaron PostGIS, CI remoto ni Android. Los respaldos conservan deliberadamente datos originales y no son evidencia de limpieza.

Esta operación local **no resuelve por sí sola** las credenciales ya publicadas ni garantiza que una nueva revisión automática autorice publicar. Para actualizar el historial público autorizado por el propietario aún falta revisión de permisos, comprobación de referencias remotas, coordinación con copias existentes y tratamiento de referencias/cachés. Reescribir historia modifica hashes y puede afectar clones y PR; conservar el historial público original intacto y purgarlo son objetivos incompatibles. Ningún proceso local debe emplearse para eludir el rechazo automático anterior.

## Puerta de publicación

1. Completar o registrar con precisión el estado de las credenciales y licencias heredadas; no asumir que están revocadas.
2. Revisar todo el historial de desarrollo que se pretenda publicar, incluidos datos personales y archivos binarios; verificar también las referencias ancestras y no incluir el respaldo local.
3. Revisar el diff final, ejecutar las pruebas disponibles y comprobar que `main` sigue intacta. El test PostGIS requiere un runtime Docker o el runner remoto y no se considera aprobado hasta ejecutarlo con éxito.
4. Solicitar nuevamente la operación de publicación únicamente cuando se haya mitigado el riesgo y el sistema de permisos lo permita. La autorización del propietario no anula una denegación automática; no intentar rutas alternativas para eludirla.

No se ha actualizado el remoto ni publicado el workflow. Las condiciones de seguridad y permisos previas al cambio remoto siguen pendientes; los respaldos originales conservan los datos retirados.
