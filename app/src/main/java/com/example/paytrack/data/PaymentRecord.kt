
package com.example.paytrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vendorName: String,
    val amount: Double,
    val kind: String, // "DEBIT" or "CREDIT"
    val dateEpoch: Long, // UTC millis
    val transactionId: String,
    val reason: String,
    val screenshotUri: String? // persisted as String
)
