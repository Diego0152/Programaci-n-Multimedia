package com.example.proyectoevaltema1

import android.content.Context
import android.content.Intent
import android.icu.number.FormattedNumberRange
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoevaltema1.databinding.ActivityConfBinding
import java.lang.NumberFormatException
import kotlin.math.min

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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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

                 val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                     putExtra(AlarmClock.EXTRA_HOUR, horas)
                     putExtra(AlarmClock.EXTRA_MINUTES, minutos)
                 }
                 startActivity(intent)
             } else {
                 Toast.makeText(this, "Error: Error en el formato: HH:MM ", Toast.LENGTH_SHORT).show()
             }
         }
        btnGmail.setOnClickListener {
            val gmail = preferences.getString("gmail","") ?: ""

            if (!(gmail.isEmpty())) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$gmail")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: correo vacío.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun configurarAlarma(): List<Int> {
        val preference = getSharedPreferences("mis_preferencias", MODE_PRIVATE)

        val alarma = preference.getString("alarm","") ?: ""
        val formatoAlarma: List<String> = alarma.split(":")

        if(formatoAlarma.size == 2) {
            val alarmHora = formatoAlarma[0]
            val alarmMinuto = formatoAlarma[1]

            try {
                val alarmHoraInt = alarmHora.toInt()
                val alarmMinutoInt = alarmMinuto.toInt()

                return listOf(alarmHoraInt, alarmMinutoInt)
            } catch (e: NumberFormatException) {
                println("Error: Formato incorrecto.")
            }
        }
        return emptyList()
    }
}


