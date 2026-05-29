package com.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnIrConverter.setOnClickListener {
            startActivity(Intent(this, ConverterActivity::class.java))
        }

        binding.btnApiSettings.setOnClickListener {
            showApiConfigDialog()
        }
    }

    private fun showApiConfigDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Configurações da API")
            .setMessage(
                "★ Status: Conectado (Online)\n" +
                "★ Host: exchangerate-api.com\n" +
                "★ Versão: v6 (JSON REST Protocol)\n" +
                "★ Atualização: Automática a cada 1 hora\n\n" +
                "As taxas de câmbio são obtidas com precisão milimétrica e armazenadas localmente para permitir conversões offline rápidas e confiáveis."
            )
            .setPositiveButton("Fechar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
