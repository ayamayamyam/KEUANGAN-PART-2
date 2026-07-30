package com.student.finance.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, currencyCode: String = "IDR"): String {
        return when (currencyCode) {
            "IDR" -> {
                val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
                "Rp${formatter.format(amount)}"
            }
            "USD" -> NumberFormat.getCurrencyInstance(Locale.US).format(amount)
            else -> {
                val formatter = NumberFormat.getNumberInstance(Locale.US)
                "$currencyCode ${formatter.format(amount)}"
            }
        }
    }
}
