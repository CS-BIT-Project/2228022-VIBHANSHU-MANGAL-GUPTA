package com.example.plan_your_day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyConverterFragment : Fragment() {

    private lateinit var currencyApiService: CurrencyApiService
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var etAmount: EditText
    private lateinit var tvConvertedAmount: TextView
    private lateinit var tvExchangeRate: TextView
    private lateinit var tvLastUpdated: TextView
    private val currencies = arrayOf("USD", "INR", "NPR", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currency_converter, container, false)

        // Initialize UI elements
        spinnerFrom = view.findViewById(R.id.spinnerFrom)
        spinnerTo = view.findViewById(R.id.spinnerTo)
        etAmount = view.findViewById(R.id.etAmount)
        tvConvertedAmount = view.findViewById(R.id.tvConvertedAmount)
        tvExchangeRate = view.findViewById(R.id.tvExchangeRate)
        tvLastUpdated = view.findViewById(R.id.tvLastUpdated)
        val btnSwap: ImageButton = view.findViewById(R.id.btnSwap)

        // Initialize spinners
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/90ea68e86ec7f945a340899a/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        currencyApiService = retrofit.create(CurrencyApiService::class.java)

        // Set up spinner listeners
        spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                convertCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Set up amount input listener
        etAmount.setOnEditorActionListener { _, _, _ ->
            convertCurrency()
            false
        }

        // Swap currencies when button is clicked
        btnSwap.setOnClickListener {
            val fromPosition = spinnerFrom.selectedItemPosition
            val toPosition = spinnerTo.selectedItemPosition
            spinnerFrom.setSelection(toPosition)
            spinnerTo.setSelection(fromPosition)
            convertCurrency()
        }

        return view
    }

    private fun convertCurrency() {
        val fromCurrency = spinnerFrom.selectedItem.toString()
        val toCurrency = spinnerTo.selectedItem.toString()
        val amountStr = etAmount.text.toString()

        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Make API call using Retrofit
        currencyApiService.getLatestRates(fromCurrency)
            .enqueue(object : Callback<CurrencyResponse> {
                override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val rate = response.body()!!.conversion_rates[toCurrency] ?: 0.0
                        if (rate != 0.0) {
                            val convertedAmount = amount * rate

                            tvConvertedAmount.text = String.format("%.2f %s", convertedAmount, toCurrency)
                            tvExchangeRate.text = String.format("1 %s = %.2f %s", fromCurrency, rate, toCurrency)
                            tvLastUpdated.text = "Last updated: ${response.body()!!.time_last_update_utc}"
                        } else {
                            Toast.makeText(requireContext(), "Invalid conversion rate", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error fetching rates", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to fetch data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
