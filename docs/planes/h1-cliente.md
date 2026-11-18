# H1 — Plan técnico del cliente de TransporMap

> **Estado:** H1 en curso. Existe un módulo parcial de preparación de alertas y pruebas locales en `frontend/`; aún no hay interfaz, transporte, persistencia ni cliente completo. Desarrollar mediante subagentes con revisión independiente. La Fase 4 está aprobada; las puertas de H0 y de despliegue siguen vigentes.

**Objetivo:** permitir que una persona con cuenta inicie sesión, cree una alerta de demostración y la vea con la misma identidad en mapa y publicaciones, sobre un cliente accesible desde móvil y escritorio.

**Arquitectura:** React/TypeScript como PWA adaptable, separando sesión, transporte HTTP, alerta y adaptador de mapa. Spring Boot sigue siendo la autoridad de acceso y de datos; el cliente no obtiene permisos por mostrar una pantalla. El proveedor de identidad y el envío de correo aún no están validados.

**Stack propuesto, aún no instalado:** React, compilador TypeScript y navegador de pruebas que se fijen mediante versiones compatibles y revisadas en H0. Existe código TypeScript parcial, probado con la ejecución experimental de Node sin comprobación estática de tipos. Para las pruebas se propone Vitest + Testing Library y Playwright, sujetos a aprobación técnica y a comprobación de soporte/costo; sus nombres no implican que estén instalados.

**Fuentes:** [requisitos aprobados](../01-requisitos.md), [arquitectura](../02-stack-y-arquitectura.md), [roadmap](../03-plan-y-roadmap.md), [OpenAPI de H0](../api/openapi-propuesta.yaml), [notas de contrato](../api/notas-contrato-h0.md), [preflight de demo](../operacion/00-preflight-demo.md) y [trazabilidad del legado](../descubrimiento/04-trazabilidad-legado.md). Cuando un ejemplo de este plan y el contrato final difieran, corregir el plan y las pruebas antes de implementar, sin alterar requisitos aprobados unilateralmente.

## Restricciones y puertas

- H1 es el **esqueleto andante** de la demo: login real, creación real y visualización consistente en mapa/lista, backend real, migración, CI y despliegue público controlado. Una versión con proveedor simulado o sin despliegue es un resultado parcial, no H1 terminado.
- El registro público y la recuperación requieren confirmar identidad/correo: el SMTP predeterminado de Supabase no cubre visitantes arbitrarios. No afirmar que login, registro o recuperación remotos funcionan por usar dobles locales.
- La interfaz y los datos sintéticos deben señalar que se trata de una demo y que no certifican condiciones viales. Las alertas, coordenadas detalladas y fotos son accesibles solo con sesión aceptada; una URL compartida o acceso directo sin sesión no puede revelar el contenido.
- La arquitectura de identidad (proveedor, ciclo de sesión y recuperación), proveedor de mapas, control de cuota/cargos y condiciones de cuenta requieren verificación de H0. No crear cuentas o activar facturación por el hecho de aprobar este microplan. El backend valida cuenta y autorización por operación, aunque el cliente o proveedor muestren sesión válida.
- H1 no incluye como terminado el flujo completo de cola offline, fotos, cursor incremental o los siete días de autorización local: pertenecen a H5 y sus puertas. Una instalación PWA no equivale automáticamente a soporte offline. No guardar información de alertas o credenciales en caché compartida ni abrir datos tras cerrar sesión. No afirmar compatibilidad offline en H1.
- La creación H1 incluye `clientReportId` y `Idempotency-Key` estables para un mismo intento, y `photoIntent: none` hasta incorporar fotos. **Decisión aceptada:** si se desconoce si un envío antiguo ya se publicó, conservar borrador y foto, no publicar de nuevo automáticamente y solicitar revisión manual explicando la incertidumbre. La detección verificable de ese caso sigue siendo puerta para H5; no se promete idempotencia indefinida. El servidor distingue hora observada de recepción; el cliente no corrige la hora silenciosamente.
- Idioma español; teclado donde corresponda, etiquetas, foco visible, mensajes claros y estados no comunicados solo por color. No se define aquí identidad visual: antes de construir pantallas se debe acordar una dirección coherente con el legado y aplicar las guías de interfaz/accesibilidad pertinentes.
- Se conservan como referencia `Login`, `Signup`, `Main`, `PanelMapa`, `DenunciaView` y `PanelNovedades`; el legado no insertaba la denuncia ni preservaba el título. No reutilizar lógica de contraseñas en texto ni duplicar el comportamiento defectuoso.

## Mapa de archivos previstos

Todos estos archivos son **rutas candidatas inexistentes**; no sobrescriben `src/`, `nbproject` ni recursos heredados. Ajustar junto con el plan backend antes de crear una estructura definitiva; no cambiar unilateralmente el contrato aceptado.

| Ruta futura | Responsabilidad |
|---|---|
| `frontend/package.json`, `frontend/package-lock.json`, `frontend/tsconfig.json` | Scripts y dependencias/versiones fijadas tras preflight; reproducir instalación sin credenciales. |
| `frontend/src/main.tsx`, `frontend/src/App.tsx` | Entrada y composición; control de acceso antes de cargar vistas protegidas. |
| `frontend/src/auth/session.ts` y `frontend/src/auth/SessionProvider.tsx` | Interfaz de identidad independiente del proveedor, estado de sesión y cierre. |
| `frontend/src/api/client.ts`, `frontend/src/api/types.ts` | Cliente HTTP y tipos compatibles con OpenAPI; no incorporar secretos o claves administrativas. |
| `frontend/src/alerts/CreateAlertForm.tsx` | Captura y validación de alerta sin foto en H1; mostrar errores recuperables. |
| `frontend/src/alerts/AlertList.tsx`, `frontend/src/alerts/AlertMap.tsx` | Lista y adaptador de mapa desde una misma colección de alertas autorizadas. |
| `frontend/src/alerts/AlertsScreen.tsx` | Coordinar carga, creación, lista y mapa; estados de carga, vacío y falla. |
| `frontend/src/map/mapAdapter.ts` | Interfaz del mapa y punto de integración sustituible; atribución y fallos del proveedor. |
| `frontend/src/**/*.test.ts(x)` | Pruebas unitarias y de componentes junto a la capacidad; transporte real simulado a nivel de red. |
| `frontend/e2e/h1.spec.ts` | E2E desde login hasta publicación en lista y mapa, más acceso anónimo denegado. |
| `frontend/public/manifest.webmanifest`, configuración de service worker | Instalación y recursos estáticos de la PWA, tras elegir herramienta; no precachear respuestas privadas ni afirmar que la cola H5 existe. |

## Contratos entre tareas

Los siguientes tipos son interfaces de planificación, no implementación actual. Resolver compatibilidad con el proveedor elegido y OpenAPI validado antes del primer PR:

```typescript
type Coordinates = { latitude: number; longitude: number };
type AuthSession = { subject: string; accessToken: string };
type AuthResult = { kind: 'authenticated'; session: AuthSession } | { kind: 'signedOut' };
interface IdentityPort {
  current(): Promise<AuthResult>;
  signIn(email: string, password: string): Promise<AuthResult>;
  signOut(): Promise<void>;
}
interface AlertRepository {
  list(): Promise<AlertPage>;
  create(input: CreateAlert, idempotencyKey: string): Promise<Alert>;
}
interface MapPort {
  setAlerts(alerts: readonly Alert[]): void;
  setOnLocationSelected(handler: (point: Coordinates) => void): void;
  destroy(): void;
}
```

`CreateAlert`, `Alert` y `AlertPage` corresponden a los esquemas del OpenAPI, incluyendo `observedAt`, `clientReportId` y `photoIntent`. `Alert` no expone `clientReportId` ni correo del autor. El código específico de acceso a tokens no se cierra aquí; no conservar contraseñas ni inventar una sesión local de siete días. Los formularios de recuperación/registro se integrarán según el proveedor validado; si no existe aún un endpoint o correo funcional, indicar capacidad pendiente y no simular éxito.

**Cuenta real de prueba H1:** una identidad externa de prueba, activa y autorizada, deberá estar vinculada o provisionada en el dominio mediante el mecanismo validado con backend, con rol ordinario y privilegios mínimos. El cliente no asigna ni infiere el rol; acceso administrativo siempre es independiente y rechazado para esta cuenta. Registrar en evidencia solo alias de prueba no sensible y resultado, nunca contraseñas/tokens. La creación de esa identidad exige cuenta/proveedor y correo verificados dentro de los límites aprobados; si no existe, se usa doble solo para ensayos locales y la demo real de H1 permanece parcial. Esto no implementa ni adelanta el registro o recuperación completos de H4 y no se dará por demostrado que funcionan.

**Contratos de transporte propuestos:** `GET /v1/me` y `GET /v1/alerts` exigen sesión; `POST /v1/alerts` envía `Idempotency-Key`, `clientReportId`, título, descripción, categoría, impacto, severidad, coordenadas, hora observada y `photoIntent: none`. Un `201` o `200` válido devuelve `Alert`; `400`, `401`, `403`, `409`, `429` y fallo de red generan estados diferenciados. La URL base de ejemplo del OpenAPI no es un servidor ejecutable; los destinos se inyectarán por configuración pública y no deben contener secretos.

## Foco de revisión independiente

1. **Cuenta ausente, expirada o suspendida:** ni la vista ni una petición directa muestran alertas protegidas; el servidor es responsable de la denegación.
2. **Respuesta perdida tras publicación:** el mismo intento reutiliza cuerpo, identificador y clave; un cambio de contenido no reutiliza la operación silenciosamente. Si un envío antiguo carece de evidencia suficiente para saber si se publicó, conservar borrador/foto, detener publicación automática y pedir revisión manual; detección y pruebas completas corresponden a H5, sin promesa de idempotencia indefinida.
3. **Mapa sin proveedor, red o permiso:** la lista muestra los datos autorizados disponibles o explica el fallo; permitir punto manual y no declarar «sin alertas» cuando no se pudo consultar.
4. **Usuario sin interacción de ratón o con pantalla pequeña:** campos etiquetados, foco visible, errores anunciados y mapa con alternativa de lista; comprobar en portátil y Tecno Android 14.
5. **Recursos públicos y privados:** archivo estático público no contiene credenciales, tokens, snapshots de alertas o fotos; la demo etiqueta claramente datos sintéticos y solo presenta información tras sesión.

## Tarea 1 — Base y sesiones

**Archivos previstos:** `frontend/package.json`, `frontend/tsconfig.json`, `frontend/src/auth/session.ts`, `frontend/src/auth/SessionProvider.tsx`, `frontend/src/App.tsx`, `frontend/src/auth/SessionProvider.test.tsx`.

**Consume:** contrato `IdentityPort` anterior y proveedor de identidad cuya configuración se haya validado en H0. **Produce:** `useSession(): AuthResult | { kind: 'loading' }` y rutas/vistas protegidas que no ejecutan `list()` hasta identidad confirmada.

- [ ] Antes de instalar, revisar versiones, licencias y ausencia de proyecto existente; fijar versión de Node y herramientas. Las dependencias y scripts futuros no existen todavía.
- [ ] Escribir prueba de componente `does_not_load_alerts_without_session`: usuario anónimo ve acceso y la petición protegida no sale; prueba `clears_protected_view_after_signout`: tras cierre no quedan datos en pantalla ni en caché compartida.
- [ ] Con las herramientas creadas en este paso, ejecutar el test y documentar el fallo esperado por ausencia de la funcionalidad. **Comando futuro propuesto:** `npm.cmd --prefix frontend run test -- SessionProvider`.
- [ ] Implementar `IdentityPort` mediante adaptador configurado y `SessionProvider`; mostrar carga, error de servicio y denegación sin mensajes que revelen existencia de cuentas. Resolver sesión/cierre real sin grabar contraseñas. Integrar con backend la identidad externa de prueba provisionada como usuario ordinario; comprobar en servidor que no recibe permisos de administrador por autenticarse y rechazar su acceso administrativo directo.
- [ ] Repetir el test, ejecutar el conjunto del cliente y analizar el diff; no declarar éxito sin salida real. Confirmar que `GET /v1/alerts` sin sesión se rechaza también en el backend mediante contrato/integración.
- [ ] Solicitar revisión independiente, atender hallazgos y registrar PR/commit convencional conforme al roadmap.

## Tarea 2 — Transporte y lista protegida

**Archivos previstos:** `frontend/src/api/client.ts`, `frontend/src/api/types.ts`, `frontend/src/alerts/AlertList.tsx`, `frontend/src/alerts/AlertsScreen.tsx`, pruebas homónimas.

**Consume:** sesión del proveedor y `AlertRepository`. **Produce:** lista de alertas reales con el `id` devuelto por el backend; los estados de vacío y error son distinguibles.

- [ ] Escribir `shows_loading_empty_and_service_error_without_fabricating_alerts`: tres respuestas diferentes producen carga, vacío o fallo, sin datos inventados; `forbidden_or_expired_session_hides_previously_loaded_alerts`: `401/403` no conserva detalle visible para una cuenta no autorizada.
- [ ] Ejecutar el test y confirmar fallo atribuible a código ausente. **Comando futuro propuesto:** `npm.cmd --prefix frontend run test -- AlertsScreen`.
- [ ] Implementar `AlertRepository.list()` contra `GET /v1/alerts`, leyendo solo páginas autorizadas; interpretar errores sin imprimir respuestas que contengan información personal. No usar datos de ejemplo como fallback de backend caído.
- [ ] Comprobar que título, descripción, categoría, hora y alias se presentan desde la misma `Alert`; usar un conjunto sintético de pruebas, identificado como tal.
- [ ] Repetir pruebas y suite, revisar acceso directo con backend real y solicitar revisión independiente antes de integrar.

## Tarea 3 — Crear alerta con identidad estable

**Archivos previstos:** `frontend/src/alerts/CreateAlertForm.tsx`, `frontend/src/alerts/CreateAlertForm.test.tsx`, `frontend/src/api/client.ts`, `frontend/src/alerts/AlertsScreen.tsx`.

**Consume:** `AlertRepository.create(input, idempotencyKey)`; campo de ubicación manual o suministrado por `MapPort`. **Produce:** una alerta confirmada por servidor que se añade a la fuente compartida de mapa y lista.

- [ ] Escribir `submits_complete_synthetic_alert_once_and_uses_server_id`: comprobar título 5–100 y descripción 10–500 como valores propuestos por requisitos, categoría, impacto, severidad, fecha observada, punto y `photoIntent: none`; confirmar resultado `201` por ID del servidor. Comprobar que una segunda respuesta `200` del mismo intento no duplica la lista.
- [ ] Escribir `retains_input_on_network_and_future_timestamp_errors`: error de red o `400` por hora futura preserva valores y muestra explicación; no alterar la hora ni decir que ya se publicó. No almacenar credenciales en el borrador.
- [ ] Ejecutar esas pruebas y confirmar su fallo inicial. **Comando futuro propuesto:** `npm.cmd --prefix frontend run test -- CreateAlertForm`.
- [ ] Implementar captura accesible con punto obligatorio y carretera/tramo opcionales; generar `clientReportId` y clave estables por intento. Si hay un reintento explícito del mismo contenido reutilizar ambas; si se cambia contenido, definir un nuevo intento sin tratar una respuesta incierta como segura. Cuando el servidor indique que un envío antiguo es incierto, conservar borrador y foto si existe, detener el reenvío automático y explicar la revisión manual; no recrear silenciosamente. El mecanismo que identifica ese caso se implementa y valida en H5 tras resolver la evidencia de H0.
- [ ] Solo incorporar a mapa/lista la respuesta aceptada; el `201/200` con mismo `id` reemplaza o conserva la entrada existente, nunca crea duplicado visual. Conservar formularios tras error y mostrar aviso de uso detenido.
- [ ] Ejecutar pruebas y suite, revisión independiente y contrato integrado de servidor; no declarar terminado H1 si todavía se emplea solamente un doble.

## Tarea 4 — Mapa sustituible y verificación móvil

**Archivos previstos:** `frontend/src/map/mapAdapter.ts`, `frontend/src/alerts/AlertMap.tsx`, `frontend/src/alerts/AlertList.tsx`, `frontend/src/alerts/AlertMap.test.tsx`, `frontend/e2e/h1.spec.ts`.

**Consume:** `readonly Alert[]` autorizadas y `MapPort`. **Produce:** marcadores de las mismas alertas mostradas en lista, con selección manual accesible y comportamiento claro si el proveedor falla.

- [ ] Escribir `list_and_map_use_same_alert_ids`: con dos alertas sintéticas se ven los mismos identificadores en ambas vistas; no crear marcadores de ubicaciones huérfanas del legado. `map_failure_keeps_authorized_list_and_explains_failure`: la falla del mapa no inventa datos ni oculta la lista cargada; si la lista falla, indica error y no vacío.
- [ ] Ejecutar tests y documentar fallo inicial. **Comando futuro propuesto:** `npm.cmd --prefix frontend run test -- AlertMap`.
- [ ] Implementar adaptador de mapa intercambiable después de verificar cuota, atribución y política de claves del proveedor. No enviar secretos de servidor al cliente ni inferir aptitud de una carretera. No descargar teselas de regiones para offline.
- [ ] Escribir E2E `authenticated_user_publishes_and_sees_same_alert_on_map_and_list` y `anonymous_direct_api_read_is_denied`; ejecutar primero con dobles de red para automatización, después contra backend y proveedores reales de prueba sin exponer datos o consumir cuotas sin control.
- [ ] **Comando futuro propuesto:** `npm.cmd --prefix frontend run e2e -- h1.spec.ts`; solo será válido cuando el script y entorno existan. Probar además en Tecno Android 14 y portátil Windows, registrar navegadores, red, foco, teclado, contraste y estados.
- [ ] Revisar rutas, screenshot sin datos personales y documentación; solicitar revisión independiente. La captura no sustituye el E2E ni el despliegue.

## Tarea 5 — PWA, CI y demo del esqueleto

**Archivos previstos:** `frontend/public/manifest.webmanifest`, configuración del cliente elegida, `.github/workflows/`, documentación de ejecución y `docs/api/` si el contrato cambia. Los archivos exactos de compilación y service worker se fijan después de la decisión de herramientas.

**Consume:** tareas 1–4 y backend H1 integrado. **Produce:** cliente publicable con automatización y enlaces reales de una demo restringida por sesión, siempre tras comprobar proveedores y costo.

- [ ] Definir prueba `does_not_cache_private_alert_or_photo_responses`: inspeccionar el resultado del service worker y las cabeceras/caché con sesión; tras cerrar o cambiar cuenta no deben mostrarse contenidos anteriores. Como H5 aún no está construido, mostrar información clara de falta de red en lugar de prometer consulta offline.
- [ ] Preparar instalación PWA y cacheo exclusivo de recursos públicos de la aplicación de acuerdo con política del hosting y bibliotecas; etiquetas, iconos y assets solo si su licencia se acredita. No guardar secreto, alertas o fotos en precache común.
- [ ] Documentar scripts reales para build, test unitario, E2E y validación del contrato en CI; **comandos candidatos, no existentes:** `npm.cmd --prefix frontend ci`, `npm.cmd --prefix frontend run build`, `npm.cmd --prefix frontend run test`, `npm.cmd --prefix frontend run e2e`. Verificar que cada script existe antes de ejecutarlo o integrarlo.
- [ ] Para CI usar datos sintéticos y dobles, sin credenciales reales para PR no confiables; antes de activar CD comprobar coste cero sin cargos por excedentes, permisos y cuotas en la cuenta concreta. Si no se valida, detener publicación y documentar el resultado parcial local.
- [ ] En la demo real, probar sesión de una cuenta de prueba real provisionada o vinculada desde identidad externa al dominio como usuario ordinario, crear una alerta sintética y confirmar la misma identidad en mapa/lista y denegación anónima. Verificar que el usuario no puede acceder a administración. Nunca incluir claves en repositorio, capturas o vídeo; alta y recuperación de visitantes siguen siendo funcionalidad H4 que no debe presentarse como implementada por esta prueba.
- [ ] Registrar URL, versión, resultados de pruebas Android/Windows, rollback y limitaciones; solicitar revisión independiente y actualizar la documentación. H1 solo se cierra cuando el despliegue y criterios de H1 hayan pasado; la validación de backup completo corresponde al hito H7 y no se presume aquí.

## Dependencias y pendientes de coordinación

1. El adaptador de identidad, el formato concreto de tokens, los endpoints de alta/recuperación y la entrega de correo son una puerta compartida con backend y preflight. La identidad real de prueba debe vincularse o provisionarse en el dominio con rol ordinario, sin asumir administrador. No definir un falso login propio para salvar una integración incompleta ni presentar la provisión de prueba como registro H4 terminado.
2. El catálogo y mapeo de eventos del legado contiene ambigüedades: en H1 usar las siete categorías aprobadas y no forzar equivalencias dudosas. El nombre/posición del campo carretera y el tratamiento de la selección de tramo seguirán el contrato coordinado.
3. La selección de biblioteca de mapa debe comprobar cobertura, atribución, privacidad, límites y acceso sin costes. Si no se prueba el proveedor, la lista puede probarse localmente, pero mapa real y H1 siguen pendientes.
4. Instantánea, cursores y `410` están documentados en OpenAPI como diseño futuro y pertenecen a la sincronización completa H5; H1 puede leer la lista en línea, sin afirmar que cambios/retiradas offline funcionan. Un `410` nunca debe hacer que una copia anterior reaparezca públicamente. Si un envío antiguo es incierto, la decisión aceptada exige revisión manual y conservar borrador/foto; la detección y las pruebas de H5 siguen pendientes.
5. En caso de caída de servicios o bloqueo de preflight por cobros, mantener trabajo local revisable, marcar desplegado como bloqueado y comunicar al coordinador. No reducir alcance ni activar facturación para cerrar el hito.

## Autorrevisión del plan

- HU-01, HU-03 y HU-05 se cubren en Tareas 1–4; RNF-04/06/10/11/12 aparecen en puertas de tareas y demo. Confirmación, rutas completas, siete días offline, fotos, moderación y recuperación corresponden a hitos posteriores del roadmap: no se eliminan.
- Los cinco fallos de «Foco de revisión» se asocian a pruebas en Tareas 1–5. Las mismas interfaces `IdentityPort`, `AlertRepository`, `MapPort` y `Alert` se consumen consistentemente; su implementación real depende de verificar proveedor y contrato.
- Los nombres de archivos y comandos son propuestas para ejecución posterior, no resultados ni rutas presentes. El alcance es técnico; diseño visual pendiente de briefing. El plan no afirma que CI, tests, mapa, cuentas o despliegue existan.
