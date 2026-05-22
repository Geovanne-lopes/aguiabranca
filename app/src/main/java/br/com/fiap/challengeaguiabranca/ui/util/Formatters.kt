package br.com.fiap.challengeaguiabranca.ui.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun formatCurrency(value: Double): String = currencyFormat.format(value)

fun formatDate(epochMillis: Long?): String {
    if (epochMillis == null) return "—"
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(dateFormat)
}
