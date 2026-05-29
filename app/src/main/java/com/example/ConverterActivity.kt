package com.example

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.databinding.ActivityConverterBinding

class ConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConverterBinding
    private lateinit var viewModel: MainViewModel

    private val currencies = listOf(
        "USD", "BRL", "EUR", "GBP", "ARS",
        "JPY", "CAD", "CHF", "AUD", "CLP",
        "MXN", "UYU", "CNY", "INR", "BTC"
    )

    private val currencyLabels = listOf(
        "USD — Dólar Americano", "BRL — Real Brasileiro",
        "EUR — Euro", "GBP — Libra Esterlina",
        "ARS — Peso Argentino", "JPY — Iene Japonês",
        "CAD — Dólar Canadense", "CHF — Franco Suíço",
        "AUD — Dólar Australiano", "CLP — Peso Chileno",
        "MXN — Peso Mexicano", "UYU — Peso Uruguaio",
        "CNY — Yuan Chinês", "INR — Rúpia Indiana",
        "BTC — Bitcoin"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Star Coin Convert"

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupSpinners()
        observeViewModel()
        setupButtons()
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, currencyLabels
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerFrom.adapter = adapter
        binding.spinnerTo.adapter = adapter
        binding.spinnerTo.setSelection(1)

        binding.spinnerFrom.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p: AdapterView<*>?, v: View?, pos: Int, id: Long
                ) {
                    viewModel.fetchRates(currencies[pos])
                    binding.tvResult.text = "—"
                    binding.tvRate.text = ""
                }

                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility =
                if (loading) View.VISIBLE else View.GONE
        }

        viewModel.lastUpdate.observe(this) { date ->
            binding.tvStatus.text = "Atualizado: $date"
        }

        viewModel.error.observe(this) { err ->
            if (!err.isNullOrEmpty()) {
                binding.tvStatus.text = "Erro: $err"
            }
        }
    }

    private fun setupButtons() {
        binding.btnConvert.setOnClickListener {
            hideKeyboard()
            val amount = binding.etAmount.text.toString()
                .replace(",", ".").toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.etAmount.error = "Digite um valor válido"
                return@setOnClickListener
            }

            val from = currencies[binding.spinnerFrom.selectedItemPosition]
            val to = currencies[binding.spinnerTo.selectedItemPosition]
            val result = viewModel.convert(amount, to)
            val rate = viewModel.getRate(to)

            if (result == 0.0) {
                Toast.makeText(
                    this, "Aguarde as taxas",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            binding.tvResult.text = "%.2f %s".format(result, to)
            binding.tvRate.text = "1 $from = %.4f $to".format(rate)
        }

        binding.btnSwap.setOnClickListener {
            hideKeyboard()
            val fromPos = binding.spinnerFrom.selectedItemPosition
            val toPos = binding.spinnerTo.selectedItemPosition
            binding.spinnerFrom.setSelection(toPos)
            binding.spinnerTo.setSelection(fromPos)
            binding.tvResult.text = "—"
            binding.tvRate.text = ""
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus ?: binding.root
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        binding.etAmount.clearFocus()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
