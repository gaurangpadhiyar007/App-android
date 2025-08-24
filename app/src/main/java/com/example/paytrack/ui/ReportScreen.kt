
package com.example.paytrack.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import com.example.paytrack.data.PaymentRecord
import com.example.paytrack.data.Repository
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat
import java.util.Calendar

@Composable
fun ReportScreen() {
    val context = LocalContext.current
    val repo = remember { Repository(context) }
    var all by remember { mutableStateOf(listOf<PaymentRecord>()) }

    LaunchedEffect(Unit) {
        repo.getAll().collectLatest { all = it }
    }

    val monthly = remember(all) { groupByMonth(all) }
    val yearly  = remember(all) { groupByYear(all) }

    Column(Modifier.padding(16.dp)) {
        Text("Per Month", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        ReportList(monthly)
        Divider(Modifier.padding(vertical = 8.dp))
        Text("Per Year", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        ReportList(yearly)
    }
}

private fun groupByMonth(items: List<PaymentRecord>): Map<String, Pair<Double, Double>> {
    val df = DecimalFormat("#.##")
    val map = linkedMapOf<String, Pair<Double, Double>>()
    val cal = Calendar.getInstance()
    items.forEach { r ->
        cal.timeInMillis = r.dateEpoch
        val key = "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1)
        val current = map[key] ?: 0.0 to 0.0
        map[key] = if (r.kind == "DEBIT") (current.first + r.amount) to current.second
                   else current.first to (current.second + r.amount)
    }
    return map.toMap()
}

private fun groupByYear(items: List<PaymentRecord>): Map<String, Pair<Double, Double>> {
    val map = linkedMapOf<String, Pair<Double, Double>>()
    val cal = Calendar.getInstance()
    items.forEach { r ->
        cal.timeInMillis = r.dateEpoch
        val key = "%04d".format(cal.get(Calendar.YEAR))
        val current = map[key] ?: 0.0 to 0.0
        map[key] = if (r.kind == "DEBIT") (current.first + r.amount) to current.second
                   else current.first to (current.second + r.amount)
    }
    return map.toMap()
}

@Composable
private fun ReportList(data: Map<String, Pair<Double, Double>>) {
    LazyColumn {
        items(data.entries.toList(), key = { it.key }) { (k, v) ->
            val (debit, credit) = v
            Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(k, style = MaterialTheme.typography.titleMedium)
                Text("Debit: ${"%.2f".format(debit)}   Credit: ${"%.2f".format(credit)}",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
