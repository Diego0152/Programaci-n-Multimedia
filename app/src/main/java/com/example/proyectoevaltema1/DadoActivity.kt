package com.example.proyectoevaltema1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoevaltema1.databinding.ActivityDadoBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class DadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDadoBinding
    private var sum: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    // Array de cartas españolas + joker
    private val cartas = arrayOf(
        R.drawable.espada12, R.drawable.espada11, R.drawable.espada10, R.drawable.oro12,
        R.drawable.oro11, R.drawable.oro10, R.drawable.copas12, R.drawable.copas11,
        R.drawable.copas10, R.drawable.bastos12, R.drawable.bastos11, R.drawable.bastos10,
        R.drawable.joker
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEvent()
    }

    private fun initEvent() {
        binding.btnGoConfig.setOnClickListener {
            val intent = Intent(this, ConfActivity::class.java)
            startActivity(intent)
        }

        binding.txtResultado.visibility = View.INVISIBLE
        binding.imageViewCarta.visibility = View.INVISIBLE  // ocultar carta inicialmente

        binding.imageButton.setOnClickListener {
            binding.txtResultado.visibility = View.VISIBLE
            binding.imageViewCarta.visibility = View.VISIBLE
            game()
        }
    }

    private fun game() {
        scheduleRun()
    }

    private fun scheduleRun() {
        val schedulerExecutor = Executors.newSingleThreadScheduledExecutor()
        val msec = 1000

        // Animación de lanzamiento de dados
        for (i in 1..5) {
            schedulerExecutor.schedule({
                val data = throwDadoInTime()
                handler.post { updateDiceImages(data) }
            }, msec * i.toLong(), TimeUnit.MILLISECONDS)
        }

        // Mostrar resultado final y carta
        schedulerExecutor.schedule({
            handler.post { viewResultAndCard() }
        }, msec * 7L, TimeUnit.MILLISECONDS)

        schedulerExecutor.shutdown()
    }

    private fun throwDadoInTime(): IntArray {
        val numDados = IntArray(3) { Random.nextInt(1, 7) }  // 3 dados
        sum = numDados.sum()
        return numDados
    }

    private fun updateDiceImages(values: IntArray) {
        val imgViews = arrayOf(
            binding.imagviewDado1,
            binding.imagviewDado2,
            binding.imagviewDado3
        )
        for (i in 0..2) selectView(imgViews[i], values[i])
    }

    private fun selectView(imgV: ImageView, v: Int) {
        imgV.setImageResource(
            when (v) {
                1 -> R.drawable.dado1
                2 -> R.drawable.dado2
                3 -> R.drawable.dado3
                4 -> R.drawable.dado4
                5 -> R.drawable.dado5
                else -> R.drawable.dado6
            }
        )
    }

    // Mostrar suma y carta correspondiente
    private fun viewResultAndCard() {
        binding.txtResultado.text = sum.toString()

        // Obtener la carta según la suma
        val drawableCarta = obtenerCartaPorSuma(sum)
        binding.imageViewCarta.setImageResource(drawableCarta)
        binding.imageViewCarta.visibility = View.VISIBLE
    }

    private fun obtenerCartaPorSuma(suma: Int): Int {
        // Sumas 3–14 → cartas 0–11
        // Sumas 15–18 → joker
        val posCarta = if (suma <= 14) suma - 3 else cartas.size
        return cartas[posCarta]
    }
}