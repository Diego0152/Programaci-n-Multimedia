package com.example.proyectoevaltema1

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.NumberFormatException
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

            if (!(numTel.isEmpty())) {
                val intent = Intent(this, PhoneActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: teléfono vacío.", Toast.LENGTH_SHORT).show()
            }
        }

        btnUrl.setOnClickListener {
            val url = preferences.getString("url","") ?: ""

            if (!(url.isEmpty())) {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Url vacía.", Toast.LENGTH_SHORT).show()
            }
        }

         btnAlarma.setOnClickListener {
             val alarma = configurarAlarma()

             if (alarma.size == 2) {
                 val horas = alarma[0]
                 val minutos = alarma[1]

                 createAlarm(horas, minutos)
             } else {
                 Toast.makeText(this, "Error: Error en el formato: HH:MM ", Toast.LENGTH_SHORT).show()
             }
         }
        btnGmail.setOnClickListener {
            val gmail = preferences.getString("gmail","") ?: ""

            if (!(gmail.isEmpty())) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:$gmail".toUri()
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: correo vacío.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun configurarAlarma(): List<Int> {
        val preference = getSharedPreferences("mis_preferencias", MODE_PRIVATE)

        val alarma = preference.getString("alarm","")?.trim() ?: ""
        val formatoAlarma: List<String> = alarma.split(":")

        if(formatoAlarma.size == 2) {
            val alarmHora = formatoAlarma[0].trim()
            val alarmMinuto = formatoAlarma[1].trim()

            try {
                val alarmHoraInt = alarmHora.toInt()
                val alarmMinutoInt = alarmMinuto.toInt()

                if (alarmHoraInt in 0..23 && alarmMinutoInt in 0..59) {
                    return listOf(alarmHoraInt, alarmMinutoInt)
                }

            } catch (e: NumberFormatException) {
                println("Error: Formato incorrecto.")
            }
        }
        return emptyList()
    }

    fun createAlarm(hour: Int, minutes: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Levantarse")
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error: No se encuentra una app para abrir la alarma.", Toast.LENGTH_SHORT).show()
        }
    }
}


