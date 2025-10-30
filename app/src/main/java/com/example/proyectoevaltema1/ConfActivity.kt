package com.example.proyectoevaltema1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoevaltema1.databinding.ActivityConfBinding

class ConfActivity : AppCompatActivity() {

    private lateinit var inflarViews: ActivityConfBinding
    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflarViews = ActivityConfBinding.inflate(layoutInflater)
        setContentView(inflarViews.root)

        preference = getSharedPreferences("mis_preferencias",MODE_PRIVATE)

        inflarViews.editPhone.setText(preference.getString("phone", ""))
        inflarViews.editUrl.setText(preference.getString("url", ""))
        inflarViews.editAlarm.setText(preference.getString("alarm", ""))
        inflarViews.editGmail.setText(preference.getString("gmail", ""))

        inflarViews.btnConfig.setOnClickListener {
            val textPhone = inflarViews.editPhone.text.toString()
            val textUrl = inflarViews.editUrl.text.toString()
            val textAlarm = inflarViews.editAlarm.text.toString()
            val textGmail = inflarViews.editGmail.text.toString()

            /*if (textPhone.isEmpty() || textUrl.isEmpty() || textAlarm.isEmpty() || textGmail.isEmpty()) {
                Toast.makeText(this, "Algun campo está vacío")
            }*/
            preference.edit().apply {
                putString("phone", textPhone)
                putString("url", textUrl)
                putString("alarm", textAlarm)
                putString("gmail", textGmail)
                apply()
                finish()
            }

            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}