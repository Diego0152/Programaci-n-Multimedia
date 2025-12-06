package com.example.proyectoevaltema1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri



class MainActivity : AppCompatActivity() {

        private lateinit var btnLlamada: ImageButton
        private lateinit var btnUrl: ImageButton
        private lateinit var btnAlarma: ImageButton
        private lateinit var btnGmail: ImageButton
        private lateinit var btnDados: ImageButton
        private lateinit var btnChistes: ImageButton
        private lateinit var btnGoConfig: ImageButton
        private lateinit var preferences: SharedPreferences
        private lateinit var tvFecha: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            preferences = getSharedPreferences("mis_preferencias", MODE_PRIVATE)

            // Inicializar botones
            btnLlamada = findViewById(R.id.btnLlamada)
            btnUrl = findViewById(R.id.btnUrl)
            btnAlarma = findViewById(R.id.btnAlarm)
            btnGmail = findViewById(R.id.btnGmail)
            btnDados = findViewById(R.id.btnDados)
            btnChistes = findViewById(R.id.btnChistes)
            btnGoConfig = findViewById(R.id.iv_change_phone)

            // Inicializar DatePicker y TextView
            tvFecha = findViewById(R.id.tvFecha)
            val fecha = preferences.getString("fecha", "No definida")
            tvFecha.text = "$fecha"

            // Asignar eventos a botones
            setupButtons()
        }

        private fun setupButtons() {
            btnLlamada.setOnClickListener {
                val intent = Intent(this, PhoneActivity::class.java)
                startActivity(intent)
            }

            btnUrl.setOnClickListener {
                val url = preferences.getString("url", "") ?: ""
                if (url.isNotEmpty()) {
                    val safeUrl =
                        if (url.startsWith("http://") || url.startsWith("https://")) url else "http://$url"
                    val intent = Intent(Intent.ACTION_VIEW, safeUrl.toUri())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "URL no configurada.", Toast.LENGTH_SHORT).show()
                }
            }

            btnAlarma.setOnClickListener {
                // Valores de ejemplo, puedes cambiar a preferencias
                val hora = 10
                val min = 30
                createAlarm(hora, min)
            }

            btnGmail.setOnClickListener {
                val gmail = preferences.getString("gmail", "") ?: ""
                if (gmail.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(gmail)
                        .matches()
                ) {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:$gmail".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Asunto por defecto")
                    }
                    startActivity(Intent.createChooser(intent, "Enviar correo con..."))
                } else {
                    Toast.makeText(this, "Correo no válido o no configurado.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            btnDados.setOnClickListener {
                val intent = Intent(this, DadoActivity::class.java)
                startActivity(intent)
            }

            btnChistes.setOnClickListener {
                val intent = Intent(this, ChistesActivity::class.java)
                startActivity(intent)
            }
            btnGoConfig.setOnClickListener {
                val intent = Intent(this, ConfActivity::class.java)
                startActivity(intent)
            }


        }

        private fun createAlarm(hour: Int, minutes: Int) {
            try {
                val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                    putExtra(AlarmClock.EXTRA_HOUR, hour)
                    putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                    putExtra(AlarmClock.EXTRA_MESSAGE, "Alarma desde App")
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo crear la alarma.", Toast.LENGTH_SHORT).show()
            }
        }
    }
