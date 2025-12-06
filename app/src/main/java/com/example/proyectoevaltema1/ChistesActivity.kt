package com.example.proyectoevaltema1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoevaltema1.R
import com.example.proyectoevaltema1.databinding.ActivityChistesBinding
import java.util.Locale
import kotlin.random.Random

class ChistesActivity : AppCompatActivity() {

    // Cambiamos a ActivityMainChistesBinding ya que el layout se ha renombrado para evitar conflictos
    private lateinit var binding : ActivityChistesBinding
    private lateinit var textToSpeech: TextToSpeech
    private val TOUCH_MAX_TIME = 500 // en milisegundos
    private var touchLastTime: Long = 0
    private val MYTAG = "TTS_LOG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflamos el layout de chistes
        binding = ActivityChistesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureTextToSpeech()
        initHander()
        initEvent()
    }

    private fun initHander() {
        // Ejecutamos en el hilo principal (UI Thread) después de un retardo
        binding.chistProgessBar.visibility = View.VISIBLE
        binding.btnExample.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.chistProgessBar.visibility = View.GONE
            // Usamos las cadenas de strings.xml
            val description = getString(R.string.describe)
            speakMeDescription(description)
            Log.i(MYTAG,"Se finaliza la carga y se muestra el botón")
            binding.btnExample.visibility = View.VISIBLE
        }, 3000)
    }

    private fun configureTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if(status != TextToSpeech.ERROR){
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(MYTAG, "El idioma no es compatible o faltan datos.")
                } else {
                    Log.i(MYTAG,"TextToSpeech configurado correctamente.")
                }
            } else {
                Log.e(MYTAG,"Error en la configuración TextToSpeech")
            }
        }
    }

    private fun initEvent() {
        binding.btnGoConfig.setOnClickListener {
            val intent = Intent(this, ConfActivity::class.java);
            startActivity(intent)
        }

        // Usamos las cadenas de strings.xml
        val chistes = intArrayOf(
            R.string.chiste_1, R.string.chiste_2, R.string.chiste_3, R.string.chiste_4, R.string.chiste_5,
            R.string.chiste_6, R.string.chiste_7, R.string.chiste_8, R.string.chiste_9, R.string.chiste_10)

        binding.btnExample.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            val numRandom = Random.nextInt(chistes.size)
            // Comprobamos si el margen entre pulsación da lugar a una doble pulsación.
            if (currentTime - touchLastTime < TOUCH_MAX_TIME){
                executorDoubleTouch(resources.getString(chistes.get(numRandom)))  // Doble toque: lanzamos el chiste.
                Log.i(MYTAG,"Detectado Doble Toque")
            }
            else{
                // Un solo toque: Describimos el botón
                Log.i(MYTAG,"Detectado Toque Simple")
                speakMeDescription("Botón para escuchar un chiste")
            }
            touchLastTime = currentTime
        }
    }

    private fun speakMeDescription(s: String) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun executorDoubleTouch(chiste: String) {
        speakMeDescription(chiste)
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized){
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}