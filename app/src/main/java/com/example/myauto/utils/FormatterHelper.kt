package com.example.myauto.utils

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatterHelper {
    private val numberFormat = DecimalFormat("#.##").apply {
        decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        isDecimalSeparatorAlwaysShown = false
    }

    private val dateTimeFormat by lazy { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    private val dateTimeFormatShort by lazy { SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault()) }
    private val dateFormat by lazy { SimpleDateFormat("dd.MM.yy", Locale.getDefault()) }
    private val dateShortFormat by lazy { SimpleDateFormat("dd MMM", Locale.getDefault()) }

    fun formatCurrency(value: Float): String = numberFormat.format(value)

    fun formatDateTime(date: Date): String = dateTimeFormat.format(date)
    fun formatDateTimeShort(date: Date): String = dateTimeFormatShort.format(date)
    fun formatDate(date: Date): String = dateFormat.format(date)
    fun formatDateShort(date: Date): String = dateShortFormat.format(date)
}