
package com.example.paytrack.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DateRange(val from: Long?, val to: Long?)

enum class DatePreset { ALL, LAST_MONTH, LAST_YEAR, CUSTOM }

fun now(): Long = System.currentTimeMillis()

fun lastMonthRange(): DateRange {
    val c = Calendar.getInstance()
    c.add(Calendar.MONTH, -1)
    val from = c.timeInMillis
    return DateRange(from, null)
}

fun lastYearRange(): DateRange {
    val c = Calendar.getInstance()
    c.add(Calendar.YEAR, -1)
    val from = c.timeInMillis
    return DateRange(from, null)
}

fun formatDate(epoch: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(epoch))
