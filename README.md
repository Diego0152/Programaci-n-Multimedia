# MI PROYECTO: Aplicación Multifuncional

Este documento describe la arquitectura, la funcionalidad de **Aplicación Multifuncional**, una aplicación Android desarrollada en Kotlin con el objetivo de hacer funcionar 4 botones, que realizarán una función.

---

## I. Arquitectura y Flujo de Navegación

La estructura del proyecto se basa en tres actividades principales que gestionan el flujo de datos y la interacción del usuario:

1.  **`MainActivity` (Panel principal)**: Contiene la interfaz de las cuatro intent y la entrada a la configuración.
2.  **`ConfActivity` (Configuración)**: Administra la lectura y escritura de todos los parámetros persistentes y los setea guardándolos.
3.  **`PhoneActivity` (Llamada)**: Controla la gestión del permiso `CALL_PHONE` y la ejecución de la llamada directa.

El flujo de datos de configuración funciona a través de **`SharedPreferences`** con el nombre: `"mis_preferencias"`.

---

## II. Funcionalidades y Mecanismos de Ejecución

### 1. Gestión de Persistencia (`ConfActivity`)

* **Carga de Datos:** Al iniciar `ConfActivity`, se consultan las `SharedPreferences` para cargar los valores guardados (`phone`, `url`, `alarm`, `gmail`) y rellenar los campos de texto (`EditText`).
* **Guardado de Datos:** El evento `btnConfig.setOnClickListener` recoge los valores actuales de los `EditText` y los escribe en `SharedPreferences` utilizando un editor (`preference.edit().apply{...}`). El uso de `.apply()` asegura que la operación de escritura se realiza de forma asíncrona para que se ejecute cuando reciba los datos a guardar.

### 2. Acciones en `MainActivity`

Todas las acciones se ejecutan solo si el valor correspondiente en `SharedPreferences` no está vacío, mostrando un `Toast` de error si en la configuración se encuentra vacío.

* **Llamada a Teléfono:** Lanza un `Intent` explícito para abrir la actividad `PhoneActivity`, donde se gestionará la solicitud del permiso de llamada.
* **Abrir URL:** Utiliza la Intención implícita **`Intent.ACTION_VIEW`** con el URI de la URL configurada (`url.toUri()`).
* **Enviar Correo (Gmail):** Utiliza la Intención **`Intent.ACTION_SENDTO`** con el esquema `mailto:` y la dirección de correo configurada (`"mailto:$gmail".toUri()`).

### 3. Mecanismo de Alarma

La alarma se gestiona mediante dos métodos en `MainActivity`:

* **`configurarAlarma()`:**
    * **Propósito:** Validar el formato y rango de la hora de la alarma.
    * **Proceso:** Divide el String de la alarma (`"HH:MM"`) por el delimitador `:` y utiliza un bloque `try-catch` para intentar la conversión a enteros (`toInt()`).
    * **Validación:** Verifica que la hora esté en el rango `0..23` y el minuto en `0..59`. Si la validación falla (formato o rango), devuelve una lista vacía.
* **`createAlarm(hour: Int, minutes: Int)`:**
    * **Propósito:** Iniciar la aplicación de alarma del sistema.
    * **Proceso:** Crea un `Intent` con la acción **`AlarmClock.ACTION_SET_ALARM`**.
    * **Seguridad:** Utiliza **`intent.resolveActivity(packageManager) != null`** para comprobar si el sistema tiene alguna aplicación capaz de manejar esta acción, notificando al usuario si no se encuentra un receptor.

### 4. Gestión de Permiso (`PhoneActivity`)

* **Verificación y Solicitud:** Al iniciar, se comprueba el permiso `CALL_PHONE`. Si no se tiene, se utiliza un **`ActivityResultLauncher`** (`registerForActivityResult`) para solicitar el permiso al usuario.
* **Llamada:** El método `call()` crea un **Intent Implícito** (`Intent.ACTION_CALL`) con el URI `tel:`.

---

## IV. Dificultades Encontradas

Durante el desarrollo de este proyecto, se encontraron las siguientes dificultades operacionales y de configuración:

* **Fallo en la Activación de la Alarma:** El intent `AlarmClock.ACTION_SET_ALARM` frecuentemente fallaba (`resolveActivity` devolvía nulo) porque el dispositivo de prueba no detectaba una aplicación de alarma compatible, incluso tras instalar varias.
* **Permiso de Alarma:** Para el correcto funcionamiento de la alarma, fue necesario incluir el permiso específico en el `AndroidManifest.xml`:
    ```xml
    <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
    ```
* **Gestión de IDs:** En varias ocasiones, se detectaron errores de referencias debido a que las IDs de los botones y vistas en el código Kotlin no se correspondían correctamente con las IDs definidas en los layouts XML.
* **Diseño de Interfaz:** Se encontraron problemas de posicionamiento y alineación de elementos al trabajar con el `ConstraintLayout` para la distribución de las diferentes vistas.

