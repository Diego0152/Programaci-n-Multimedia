package com.example.proyectoevaltema1

import android.content.Intent
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
             val alarma = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
             val hora = alarma.getString("alarm_hour", "") ?: ""
             val min = alarma.getString("alarm_min", "") ?: ""

             val horaToInt = hora.toInt()
             val minToInt = min.toInt()

                 createAlarm(horaToInt, minToInt)
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
    fun createAlarm(hour: Int, minutes: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Levantarse")
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        }
        startActivity(intent)
    }
}


