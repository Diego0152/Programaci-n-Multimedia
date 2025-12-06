package com.example.proyectoevaltema1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.putString
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoevaltema1.databinding.ActivityConfBinding

class ConfActivity : AppCompatActivity() {

    private lateinit var inflarViews: ActivityConfBinding
    private lateinit var preference: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        preference = getSharedPreferences("mis_preferencias", MODE_PRIVATE)

        super.onCreate(savedInstanceState)

        inflarViews = ActivityConfBinding.inflate(layoutInflater)
        setContentView(inflarViews.root)

        progressBar = inflarViews.root.findViewById(R.id.progressBar)

        // CARGAR DATOS GUARDADOS
        inflarViews.editPhone.setText(preference.getString("phone", ""))
        inflarViews.editUrl.setText(preference.getString("url", ""))
        inflarViews.editGmail.setText(preference.getString("gmail", ""))

        // SPINNER
        val opcionesTiempo = listOf("1 seg", "2 seg", "3 seg", "5 seg")
        inflarViews.spinnerTiempo.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesTiempo)

        // CHECKBOX
        inflarViews.chkOpcional.isChecked = preference.getBoolean("sos", false)

        // DATEPICKER
        preference.getString("fecha", null)?.split("/")?.takeIf { it.size == 3 }?.let { (d, m, y) ->
            inflarViews.datepickerFecha.updateDate(y.toInt(), m.toInt() - 1, d.toInt())
        }

        // Boton para guardar
        inflarViews.btnConfig.setOnClickListener {

            val textPhone = inflarViews.editPhone.text.toString().trim()
            val textUrl = inflarViews.editUrl.text.toString().trim()
            val textGmail = inflarViews.editGmail.text.toString().trim()

            if (textPhone.isEmpty() || textUrl.isEmpty() || textGmail.isEmpty()) {
                Toast.makeText(this, "Algún campo obligatorio está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.WEB_URL.matcher(textUrl).matches()) {
                Toast.makeText(this, "URL no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(textGmail).matches()) {
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            val tiempoSpinner = inflarViews.spinnerTiempo.selectedItem.toString()
            val tiempoValor = tiempoSpinner.split(" ")[0].toLong()
            val milisegundos = tiempoValor * 1000

            Handler(Looper.getMainLooper()).postDelayed({

                val tiempoSeleccionado = inflarViews.spinnerTiempo.selectedItem.toString()
                val sonido = inflarViews.chkOpcional.isChecked

                // Guardar preferencias incluyendo la fecha del DatePicker
                val day = inflarViews.datepickerFecha.dayOfMonth
                val month = inflarViews.datepickerFecha.month + 1
                val year = inflarViews.datepickerFecha.year
                val fechaStr = "$day/$month/$year"

                val numeroAGuardar = if (inflarViews.chkOpcional.isChecked) {
                    "622871690" // número de socorro / Diego
                } else {
                    textPhone // número que escribió el usuario
                }


                preference.edit().apply {
                    putString("phone", numeroAGuardar)
                    putString("url", textUrl)
                    putString("gmail", textGmail)
                    putString("tiempo_tiradas", tiempoSeleccionado)
                    putString("fecha", fechaStr) // <-- Aquí se guarda la fecha
                }.apply()

                progressBar.visibility = View.GONE
                Toast.makeText(this, "Configuración guardada correctamente", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, milisegundos)
        }
    }
}