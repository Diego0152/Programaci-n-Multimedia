# Aplicación Multifuncional

Este documento describe la arquitectura, la funcionalidad y los mecanismos de validación de la **Aplicación Multifuncional**, una aplicación Android desarrollada en Kotlin cuyo objetivo es ofrecer acceso rápido a múltiples funciones a través de botones, con integración de configuración, llamadas, alarmas, juegos y asistente de voz.

---

## I. Arquitectura y Flujo de Navegación

La estructura del proyecto se basa en varias actividades principales que gestionan el flujo de datos y la interacción del usuario:

1. **`MainActivity` (Panel Principal):** Contiene la interfaz de las funciones principales (Intents implícitos y navegación hacia otras actividades como DadosActivity y ChistesActivity) y acceso a configuración.  
2. **`ConfActivity` (Configuración):** Administra la lectura y escritura de todos los parámetros persistentes en **SharedPreferences**, incluyendo teléfono, URL, correo, fecha, tiempo de espera y modo SOS.  
3. **`PhoneActivity` (Llamada):** Controla la gestión del permiso `CALL_PHONE` y la ejecución de la llamada directa, aplicando la lógica del CheckBox SOS.  
4. **`DadosActivity` (Juego de Dados y Carta Aleatoria):** Permite simular la tirada de 3 dados y seleccionar una carta de la baraja española.  
5. **`ChistesActivity` (Asistente de Voz):** Reproduce aleatoriamente uno de los 10 chistes personalizados a través del asistente de voz.

El flujo de datos de configuración se gestiona a través de **SharedPreferences** con el nombre: `"misPreferencias"`.

---

## II. Configuración en `ConfActivity`

**Campos y controles:**

1. **EditText:** Para teléfono, URL y correo.  
2. **DatePicker:** Permite seleccionar una fecha que se guardará en **SharedPreferences** y se mostrará en `MainActivity`.  
3. **Spinner:** Permite elegir un valor de tiempo en segundos, utilizado para controlar la duración de un **ProgressBar** que simula un proceso.  
4. **CheckBox “SOS / Llamar a Diego”:** Si está activado, el número de teléfono que se usará en `PhoneActivity` será **el de Diego (622871690)**, independientemente del número introducido.  

**Validaciones y guardado:**

- Se comprueba que ningún campo esté vacío antes de guardar.  
- La fecha del DatePicker y el tiempo del Spinner se guardan en SharedPreferences (`fecha` y `tiempo_tiradas`).  
- El CheckBox SOS guarda un booleano `sos` que determina si se usará el teléfono de Diego en `PhoneActivity`.  
- Los cambios se aplican con `.apply()` de forma asíncrona.

---

## III. Funcionalidades y Mecanismos de Ejecución en `MainActivity`

Todas las acciones se ejecutan solo si los valores correspondientes en SharedPreferences cumplen con las validaciones básicas.  

| Funcionalidad | Intent / Acción | Validación y Ejecución |
| :--- | :--- | :--- |
| **Llamada a Teléfono** | Intent explícito a `PhoneActivity` | Verifica longitud de 9 dígitos y formato numérico. Si SOS está activado, llama al número de Diego. |
| **Abrir URL** | `Intent.ACTION_VIEW` | Verifica que la URL no esté vacía y maneja excepciones si la URL es incorrecta. |
| **Establecer Alarma** | `AlarmClock.ACTION_SET_ALARM` | Comprueba que la hora y los minutos sean válidos y se encuentren dentro del rango adecuado. Se encapsula en `try-catch`. |
| **Enviar Correo (Gmail)** | `Intent.ACTION_SENDTO` con `mailto:` | Comprueba que el correo no esté vacío y tenga formato básico con `@` y `.`. |
| **Ir a DadosActivity** | Intent explícito | Navega a la actividad de simulación de dados y carta aleatoria. |
| **Ir a ChistesActivity** | Intent explícito | Navega a la actividad de chistes con asistente de voz. |

---

## IV. Lógica de `PhoneActivity` y Permiso `CALL_PHONE`

- **Verificación y Solicitud de Permiso:** Se comprueba `CALL_PHONE` y se solicita al usuario si no está concedido mediante `ActivityResultLauncher`.  
- **Manejo de Permiso Denegado:** Se muestra un `Toast` y se ofrece un enlace directo a la configuración de la app.  
- **Ejecución de Llamada:** `call()` valida longitud, formato numérico y aplica la lógica del CheckBox SOS para usar el número de Diego si está activado.

---

## V. Funcionalidad de `DadosActivity`

- Contiene una **imagen interactiva** que simula la tirada de 3 dados con números aleatorios.  
- Cuando termina la simulación, calcula la suma total de los dados.  
- Posteriormente, selecciona **una carta aleatoria de la baraja española** recorriendo un array predefinido de cartas.  
- Se muestra visualmente tanto el resultado de los dados como la carta seleccionada.

---

## VI. Funcionalidad de `ChistesActivity`

- Contiene **10 chistes personalizados** almacenados en `strings.xml`.  
- Al activar la acción, se selecciona aleatoriamente uno de los chistes y se reproduce mediante el **asistente de voz** (Text-to-Speech).

---

## VII. Dificultades Encontradas y Soluciones Implementadas

1. **Activación de la Alarma:** Se encontraron fallos ocasionales donde la `Intent AlarmClock.ACTION_SET_ALARM` no encontraba una aplicación compatible.  
    * **Solución:** Toda la lógica de ejecución del Intent de alarma se encapsuló en un bloque `try-catch` para notificar al usuario en caso de un fallo.  
2. **Permiso de Alarma:** Para asegurar la compatibilidad con todas las versiones de Android, fue necesario incluir el permiso específico en el `AndroidManifest.xml`: `<uses-permission android:name="com.android.alarm.permission.SET_ALARM" />`.  
3. **Gestión de IDs y Diseño:** Se dedicó un esfuerzo considerable a la correcta vinculación de las IDs del layout XML con las referencias en el código Kotlin, así como al diseño y posicionamiento de los elementos en `ConstraintLayout`.  
4. **Integración de nuevas funcionalidades:** Se añadieron controles como DatePicker, Spinner, y CheckBox para SOS en `ConfActivity`, así como navegación hacia `DadosActivity` y `ChistesActivity`, con sus lógicas específicas para simulación de dados y reproducción de chistes mediante TTS.  
5. **Checkbox que setea el número del EditText:** Al activar el CheckBox SOS, el número de teléfono se guarda automáticamente en SharedPreferences y **se refleja también en el EditText de configuración**, lo que puede sobrescribir temporalmente el número introducido por el usuario. Esto se manejó asegurando que la lógica de SOS solo afecte al número de llamada, manteniendo control sobre la UI y la consistencia de los datos.
