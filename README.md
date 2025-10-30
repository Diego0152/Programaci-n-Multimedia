# Documentación del Proyecto: Aplicación Multifuncional

Este documento describe la arquitectura, la funcionalidad y los mecanismos de persistencia de la **Aplicación Multifuncional**, una aplicación Android desarrollada en Kotlin destinada a centralizar y ejecutar acciones rápidas del sistema basadas en una configuración previa.

---

## I. Arquitectura y Flujo de Navegación

El proyecto se compone de tres actividades principales que gestionan el flujo de datos y la interacción del usuario:

1.  **`MainActivity` (Punto de Partida)**: Contiene la interfaz de las cuatro acciones rápidas y la entrada a la configuración.
2.  **`ConfActivity` (Configuración)**: Administra la lectura y escritura de todos los parámetros persistentes.
3.  **`PhoneActivity` (Llamada)**: Controla la gestión del permiso `CALL_PHONE` y la ejecución de la llamada directa.

El flujo de datos de configuración es manejado de manera uniforme a través de **`SharedPreferences`** bajo el nombre de archivo `"mis_preferencias"`.

---

## II. Funcionalidades y Mecanismos de Ejecución

### 1. Gestión de Persistencia (`ConfActivity`)

* **Carga de Datos:** Al iniciar `ConfActivity`, se consultan las `SharedPreferences` para cargar los valores guardados (`phone`, `url`, `alarm`, `gmail`) y rellenar los campos de texto (`EditText`).
* **Guardado de Datos:** El evento `btnConfig.setOnClickListener` recoge los valores actuales de los `EditText` y los escribe en `SharedPreferences` utilizando un editor (`preference.edit().apply{...}`). El uso de `.apply()` asegura que la operación de escritura se realiza de forma asíncrona.

### 2. Acciones en `MainActivity`

Todas las acciones se ejecutan solo si el valor correspondiente en `SharedPreferences` no está vacío, mostrando un `Toast` de error si la configuración falta.

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
    * **Proceso:** Crea un `Intent` con la acción **`AlarmClock.ACTION_SET_ALARM`** y le adjunta los extras `EXTRA_HOUR`, `EXTRA_MINUTES` y `EXTRA_MESSAGE`.
    * **Seguridad:** Utiliza **`intent.resolveActivity(packageManager) != null`** para comprobar si el sistema tiene alguna aplicación capaz de manejar esta acción, notificando al usuario si no se encuentra un receptor.

### 4. Gestión de Permiso (`PhoneActivity`)

* **Verificación y Solicitud:** Al iniciar, se comprueba el permiso `CALL_PHONE` mediante `ContextCompat.checkSelfPermission`. Si no se tiene, se utiliza un **`ActivityResultLauncher`** (`registerForActivityResult`) para solicitar el permiso al usuario.
* **Llamada:** El método `call()` crea un `Intent.ACTION_CALL` con el URI `tel:`. Este Intent solo se lanza si el permiso ha sido concedido previamente.

---

## III. Repositorio del Proyecto

El código fuente completo de la **Aplicación Multifuncional** está disponible en el siguiente repositorio de GitHub:

**[https://github.com/Diego0152/Programaci-n-Multimedia](https://github.com/Diego0152/Programaci-n-Multimedia)**
