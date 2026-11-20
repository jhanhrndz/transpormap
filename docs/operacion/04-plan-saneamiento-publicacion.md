# Saneamiento previo a la publicación de devlop

**Estado:** plan local preparado; no se han revocado claves, reescrito historiales ni publicado ramas. La publicación fue rechazada por la revisión automática de permisos y permanece detenida. No reproducir los valores encontrados en chats, issues, capturas ni logs.

## Situación y respaldo

- `main` y `origin/main` apuntan al commit inicial heredado. Se mantiene la instrucción del propietario de no modificarlos ni unir desarrollo antes de terminar el proyecto.
- `respaldo/devlop-antes-de-saneamiento` es una referencia **local** al último commit de `devlop` anterior a la limpieza. Es un respaldo, contiene el historial original y no debe publicarse ni usarse como destino de un push. No incluye cambios sin confirmar: antes de reescribir se deben preservar también la edición de las notas y este documento. No usar `git push --all` ni `git push --mirror`, pues incluirían referencias de respaldo.
- Una ruta local personal se retiró del contenido actual de `docs/api/notas-contrato-h0.md`. Los commits anteriores de `devlop` aún pueden contener rutas locales; borrar la línea actual no modifica esos commits.
- En el legado ya publicado en `main` se detectaron literales de conexión Oracle en `src/connectionbd/ConnectionOracle.java`, una clave de mapa en `src/mapa/Mapa.java` y material de licencia JxBrowser. El propietario indicó que las credenciales de Oracle y mapa eran antiguas, de uso local, que ya no conserva la base y que no recuerda los accesos; autorizó retirar ambos valores. En el árbol actual de `devlop` se reemplazaron por configuración externa y se retiraron los dos `.class` que los contenían. **No se ha confirmado que las credenciales estén revocadas o sean inválidas**, y siguen en el commit histórico público. El material de JxBrowser es un asunto separado aún pendiente de revisar. Tratar los valores como expuestos, sin incluirlos aquí.
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

Recomendación para la parte local: reescribir de manera controlada **únicamente los commits no publicados de desarrollo**, conservando el respaldo local, después de identificar todas las rutas afectadas. Antes de finalizar, comprobar que `main` y `origin/main` siguen iguales, que el contenido de desarrollo y sus pruebas se preservan y que una revisión del historial nuevo no encuentra las rutas. El backup conserva deliberadamente los datos originales y no es evidencia de limpieza. No usar `push --force`.

Esta operación local **no resuelve por sí sola** las credenciales que ya forman parte de `main` ni garantiza que una nueva revisión automática autorice publicar. Para retirar información del historial público de `main` haría falta una decisión separada sobre reescritura del remoto, coordinación con copias existentes y tratamiento de referencias/cachés. Reescribir historia modifica hashes y puede afectar clones y PR; conservar `main` intacta y purgar su historial público son objetivos incompatibles.

## Puerta de publicación

1. Completar o registrar con precisión el estado de las credenciales y licencias heredadas; no asumir que están revocadas.
2. Revisar todo el historial de desarrollo que se pretenda publicar, incluidos datos personales y archivos binarios; verificar también las referencias ancestras y no incluir el respaldo local.
3. Revisar el diff final, ejecutar las pruebas disponibles y comprobar que `main` sigue intacta. El test PostGIS requiere un runtime Docker o el runner remoto y no se considera aprobado hasta ejecutarlo con éxito.
4. Solicitar nuevamente la operación de publicación únicamente cuando se haya mitigado el riesgo y el sistema de permisos lo permita. La autorización del propietario no anula una denegación automática; no intentar rutas alternativas para eludirla.

No se ha aplicado todavía una reescritura ni publicado el workflow. La elección de tratamiento del `main` público no queda resuelta por este documento.
