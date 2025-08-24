
package com.example.paytrack.ui

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.paytrack.data.PaymentRecord
import com.example.paytrack.data.Repository
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun AddRecordScreen() {
    val context = LocalContext.current
    val repo = remember { Repository(context) }
    val scope = rememberCoroutineScope()

    var vendor by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf("DEBIT") }
    var dateEpoch by remember { mutableStateOf(System.currentTimeMillis()) }
    var txnId by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var screenshotUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> screenshotUri = uri }
    )

    val cal = Calendar.getInstance().apply { timeInMillis = dateEpoch }
    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            cal.set(y, m, d, 0, 0, 0)
            dateEpoch = cal.timeInMillis
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(vendor, { vendor = it }, label = { Text("Vendor name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            amount, { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SegmentedButton(kind, onChange = { kind = it })
            Button(onClick = { datePicker.show() }) { Text("Pick date") }
        }
        OutlinedTextField(txnId, { txnId = it }, label = { Text("Transaction ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(reason, { reason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(if (screenshotUri == null) "Select screenshot" else "Change screenshot") }
            if (screenshotUri != null) {
                Text("Selected")
                TextButton(onClick = { screenshotUri = null }) { Text("Remove") }
            }
        }
        Button(
            onClick = {
                val amt = amount.toDoubleOrNull() ?: 0.0
                val record = PaymentRecord(
                    vendorName = vendor.trim(),
                    amount = amt,
                    kind = kind,
                    dateEpoch = dateEpoch,
                    transactionId = txnId,
                    reason = reason,
                    screenshotUri = screenshotUri?.toString()
                )
                scope.launch {
                    repo.save(record)
                    vendor = ""; amount = ""; kind = "DEBIT"; dateEpoch = System.currentTimeMillis(); txnId = ""; reason = ""; screenshotUri = null
                }
            },
            enabled = vendor.isNotBlank() && amount.toDoubleOrNull() != null
        ) { Text("Save record") }
    }
}

@Composable
private fun SegmentedButton(selected: String, onChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected == "DEBIT", onClick = { onChange("DEBIT") }, label = { Text("Debit") })
        FilterChip(selected == "CREDIT", onClick = { onChange("CREDIT") }, label = { Text("Credit") })
    }
}
