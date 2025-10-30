package com.example.proyectoevaltema1

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectoevaltema1.databinding.ActivityPhoneBinding
import androidx.core.net.toUri
import androidx.core.content.edit

class PhoneActivity : AppCompatActivity() {
    private lateinit var mainBinding : ActivityPhoneBinding
    private var stringPhoneCall : String? = null
    private var permisoLlamar = false
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        mainBinding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(mainBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        initEventCall()

    }

    override fun onResume() {
        super.onResume()
        permisoLlamar = isPermissionCall()

        val preference = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
        stringPhoneCall = preference.getString("phone", "")
        mainBinding.txtPhone.text = stringPhoneCall
    }

    private fun init(){
        registerLauncher()
        if (!isPermissionCall())
            requestPermissionLauncher.launch(android.Manifest.permission. CALL_PHONE)

        mainBinding.ivChangePhone.setOnClickListener {
            val sharedPref = getString(R.string.mi_preferencia)
            val sharedPhone = getString(R.string.nombreTelefono)
            val preference = getSharedPreferences(sharedPref, MODE_PRIVATE)
            preference.edit {
                remove(sharedPhone)
            }
            val intent = Intent(this, ConfActivity::class.java )
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("back", true)
                }
            startActivity(intent)
        }

    }
    private fun registerLauncher(){
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
                isGranted->
            if (isGranted) {
                permisoLlamar = true
            }
            else {
                Toast.makeText( this, "Necesitas habilitar los permisos",
                    Toast.LENGTH_LONG).show()
                goToConfiguracionApp()
            }
        }
    }

    private fun initEventCall() {
        mainBinding.btnPhone.setOnClickListener {
            permisoLlamar=isPermissionCall()
            if (permisoLlamar)
                call()
            else
                requestPermissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
        }
    }
    private fun isPermissionCall():Boolean{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true
        else
            return isPermissionToUser()
    }

    private fun isPermissionToUser() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

    private fun call() {

        if (stringPhoneCall.isNullOrEmpty()) {
            Toast.makeText(this, "Debe configurar un número antes de llamar.", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = ("tel:" + stringPhoneCall!!).toUri()
        }
        startActivity(intent)
    }
    private fun goToConfiguracionApp(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }

        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}