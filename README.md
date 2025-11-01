# Aplicación Multifuncional

Este documento describe la arquitectura, la funcionalidad y los mecanismos de validación de la **Aplicación Multifuncional**, una aplicación Android desarrollada en Kotlin cuyo objetivo es ofrecer acceso rápido a cuatro funciones principales a través de botones.

---

## I. Arquitectura y Flujo de Navegación

La estructura del proyecto se basa en tres actividades principales que gestionan el flujo de datos y la interacción del usuario, siguiendo un diseño de aplicación de panel principal:

1.  **`MainActivity` (Panel Principal):** Contiene la interfaz de las cuatro funciones (Intents implícitos) y la navegación hacia la configuración.
2.  **`ConfActivity` (Configuración):** Administra la lectura y escritura de todos los parámetros persistentes (teléfono, URL, alarma y correo) en **SharedPreferences**.
3.  **`PhoneActivity` (Llamada):** Controla la gestión del permiso `CALL_PHONE` y la ejecución de la llamada directa.

El flujo de datos de configuración se gestiona a través de **SharedPreferences** con el nombre: `"mis_preferencias"`.

---

## II. Gestión de Persistencia (`ConfActivity`)

* **Carga de Datos:** Al iniciar `ConfActivity`, se consultan las `SharedPreferences` para cargar los valores guardados (`phone`, `url`, `alarm_hour`, `alarm_min`, `gmail`) y rellenar los campos de texto (`EditText`).
* **Validación de Campos Vacíos:** Al intentar guardar, se comprueba que **ningún campo de configuración esté vacío**. Si se detecta algún campo sin completar, se muestra un mensaje `Toast` de error, y los datos no son guardados.
* **Guardado de Datos:** Al pulsar el botón de configuración, los valores actuales se escriben en `SharedPreferences`. Se utiliza `.apply()` para realizar la operación de escritura de forma asíncrona. Tras un guardado exitoso, la actividad finaliza, regresando a `MainActivity`.

---

## III. Funcionalidades y Mecanismos de Ejecución en `MainActivity`

Todas las acciones se ejecutan solo si el valor correspondiente en `SharedPreferences` no está vacío y cumple con el formato básico esperado. Si no se cumplen las condiciones, se muestra un `Toast` de error.

| Funcionalidad | Intent Utilizado | Validación de Formato y Errores |
| :--- | :--- | :--- |
| **Llamada a Teléfono** | `Intent` explícito a `PhoneActivity`. | Se verifica que el valor en `SharedPreferences` no esté vacío, tenga una longitud de **9 dígitos** y pueda ser convertido a un número. Si es válido, se abre `PhoneActivity`. |
| **Abrir URL** | `Intent.ACTION_VIEW` (Implícito). | Se verifica que la URL no esté vacía. La ejecución está envuelta en un bloque `try-catch` para manejar excepciones que puedan surgir si el formato de la URL es incorrecto. |
| **Establecer Alarma** | `AlarmClock.ACTION_SET_ALARM` (Implícito). | Se verifica que la hora y los minutos no estén vacíos y puedan ser convertidos a enteros. Además, se valida el rango: **hora** ($0..23$) y **minutos** ($0..59$). El uso del `Intent` está encapsulado en un `try-catch` para gestionar fallos al intentar crear la alarma. |
| **Enviar Correo (Gmail)** | `Intent.ACTION_SENDTO` con esquema `mailto:` (Implícito). | Se verifica que el correo no esté vacío y contenga tanto el carácter **@** como el carácter **.** para una validación de formato básica. |

---

## IV. Gestión de Permiso (`PhoneActivity`)

`PhoneActivity` es la responsable de ejecutar la llamada directa, para lo cual necesita el permiso `CALL_PHONE`.

* **Verificación y Solicitud:** Al iniciar, se comprueba el permiso `CALL_PHONE`. Si no se tiene, se utiliza un `ActivityResultLauncher` (`registerForActivityResult`) para solicitarlo al usuario.
* **Manejo de Permiso Denegado:** Si el usuario deniega el permiso, se muestra un `Toast` y se proporciona un enlace directo a la configuración de la aplicación para que el usuario pueda habilitarlo manualmente.
* **Ejecución de Llamada:** El método `call()` realiza la validación final del número (9 dígitos, formato numérico) y crea un `Intent.ACTION_CALL` con el URI `tel:`.

---

## V. Dificultades Encontradas y Soluciones Implementadas

1.  **Activación de la Alarma:** Se encontraron fallos ocasionales donde la `Intent AlarmClock.ACTION_SET_ALARM` no encontraba una aplicación compatible.
    * **Solución:** Toda la lógica de ejecución del Intent de alarma se encapsuló en un bloque `try-catch` para notificar al usuario en caso de un fallo en la creación de la alarma.
2.  **Permiso de Alarma:** Para asegurar la compatibilidad con todas las versiones de Android, fue necesario incluir el permiso específico en el `AndroidManifest.xml`: `<uses-permission android:name="com.android.alarm.permission.SET_ALARM" />`.
3.  **Gestión de IDs y Diseño:** Se dedicó un esfuerzo considerable a la correcta vinculación de las IDs del layout XML con las referencias en el código Kotlin, así como al diseño y posicionamiento de los elementos en `ConstraintLayout`.
