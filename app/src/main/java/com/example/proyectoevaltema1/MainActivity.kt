package com.example.proyectoevaltema1

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private lateinit var iwConfig: ImageView
    private lateinit var btnLlamada: ImageButton
    private lateinit var btnUrl: ImageButton
    private lateinit var btnAlarma: ImageButton
    private lateinit var btnGmail: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iwConfig = findViewById(R.id.iv_change_phone)

        iwConfig.setOnClickListener {
            val intent = Intent(this, ConfActivity::class.java)
            startActivity(intent)
        }

        btnLlamada = findViewById(R.id.btnLlamada)
        btnUrl = findViewById(R.id.btnUrl)
        btnAlarma = findViewById(R.id.btnAlarm)
        btnGmail = findViewById(R.id.btnGmail)

        funcionesBotones()
    }

    fun funcionesBotones() {

        val preferences = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
        btnLlamada.setOnClickListener {
            val numTel = preferences.getString("phone","") ?: ""

            if (!(numTel.isEmpty() && numTel.length != 9 && numTel.toLongOrNull() == null)) {
                val intent = Intent(this, PhoneActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Formato incorrecto.", Toast.LENGTH_SHORT).show()
            }
        }

        btnUrl.setOnClickListener {
            val url = preferences.getString("url","") ?: ""

            if (url.isEmpty()) {
                Toast.makeText(this, "Error: Url vacía.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
            } catch (e : Exception) {
                Toast.makeText(this, "Error: Formato incorrecto de la url.", Toast.LENGTH_SHORT).show()
            }

        }

         btnAlarma.setOnClickListener {
             val alarma = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
             val hora = alarma.getString("alarm_hour", "") ?: ""
             val min = alarma.getString("alarm_min", "") ?: ""

             val horaToInt = hora.toIntOrNull()
             val minToInt = min.toIntOrNull()

             if (horaToInt !in 0..23 && minToInt !in 0..59) {
                Toast.makeText(this, "Error: Formato de la alarma incorrecto.", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
             }

             createAlarm(horaToInt, minToInt)
         }

        btnGmail.setOnClickListener {
            val gmail = preferences.getString("gmail","") ?: ""

            if (gmail.isEmpty()) {
                Toast.makeText(this, "Error: correo vacío.", Toast.LENGTH_SHORT).show()
            }

            if (gmail.contains('@') || gmail.contains('.')) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:$gmail".toUri()
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Correo electrónico no encontrado.", Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun createAlarm(hour: Int?, minutes: Int?) {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "Levantarse")
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minutes)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: No se puedo crear la alarma.", Toast.LENGTH_SHORT).show()
        }
    }
}


