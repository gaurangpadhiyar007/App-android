
package com.example.paytrack.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.paytrack.data.PaymentRecord
import com.example.paytrack.data.Repository
import com.example.paytrack.pdf.PdfExporter
import com.example.paytrack.util.DatePreset
import com.example.paytrack.util.formatDate
import com.example.paytrack.util.lastMonthRange
import com.example.paytrack.util.lastYearRange
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ListScreen() {
    val context = LocalContext.current
    val repo = remember { Repository(context) }
    var all by remember { mutableStateOf(listOf<PaymentRecord>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        repo.getAll().collectLatest { all = it }
    }

    // filters
    var qVendor by remember { mutableStateOf("") }
    var minAmount by remember { mutableStateOf("") }
    var maxAmount by remember { mutableStateOf("") }
    var preset by remember { mutableStateOf(DatePreset.ALL) }
    var from by remember { mutableStateOf<Long?>(null) }
    var to by remember { mutableStateOf<Long?>(null) }

    val filtered = all.filter { r ->
        val vendorOK = qVendor.isBlank() || r.vendorName.contains(qVendor, ignoreCase = true)
        val minOK = minAmount.toDoubleOrNull()?.let { r.amount >= it } ?: true
        val maxOK = maxAmount.toDoubleOrNull()?.let { r.amount <= it } ?: true
        val dateOK = when (preset) {
            DatePreset.ALL -> true
            DatePreset.LAST_MONTH -> r.dateEpoch >= lastMonthRange().from!!
            DatePreset.LAST_YEAR -> r.dateEpoch >= lastYearRange().from!!
            DatePreset.CUSTOM -> {
                val lo = from ?: Long.MIN_VALUE
                val hi = to ?: Long.MAX_VALUE
                r.dateEpoch in lo..hi
            }
        }
        vendorOK && minOK && maxOK && dateOK
    }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(qVendor, { qVendor = it }, label = { Text("Search vendor") }, modifier = Modifier.weight(1f))
            OutlinedTextField(minAmount, { minAmount = it }, label = { Text("Min") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(100.dp))
            OutlinedTextField(maxAmount, { maxAmount = it }, label = { Text("Max") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.width(100.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { preset = DatePreset.ALL }, label = { Text("All") })
            AssistChip(onClick = { preset = DatePreset.LAST_MONTH }, label = { Text("Last month") })
            AssistChip(onClick = { preset = DatePreset.LAST_YEAR }, label = { Text("Last year") })
            AssistChip(onClick = { preset = DatePreset.CUSTOM }, label = { Text("Custom") })
        }
        if (preset == DatePreset.CUSTOM) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(from?.let { formatDate(it) } ?: "", {}, label = { Text("From (yyyy-mm-dd)") }, enabled = false, modifier = Modifier.weight(1f))
                OutlinedTextField(to?.let { formatDate(it) } ?: "", {}, label = { Text("To (yyyy-mm-dd)") }, enabled = false, modifier = Modifier.weight(1f))
            }
            // Simple buttons to set from/to using current time for demo; in a real app you'd open date pickers
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { from = System.currentTimeMillis() - 30L*24*3600*1000 }) { Text("From: 30 days ago") }
                Button(onClick = { to = System.currentTimeMillis() }) { Text("To: today") }
            }
        }

        Row {
            Button(
                onClick = {
                    val uri = PdfExporter.export(context, filtered) ?: return@Button
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share report PDF"))
                },
                enabled = filtered.isNotEmpty()
            ) { Text("Export PDF") }
        }

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(filtered, key = { it.id }) { r ->
                ListItem(
                    headlineContent = { Text(r.vendorName) },
                    supportingContent = { Text(r.reason) },
                    trailingContent = {
                        Column {
                            Text("${if (r.kind == "DEBIT") "-" else "+"}${"%.2f".format(r.amount)}")
                            Text(formatDate(r.dateEpoch), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        }
    }
}
