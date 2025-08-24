
package com.example.paytrack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class Repository(context: Context) {
    private val dao = AppDatabase.get(context).paymentDao()
    fun getAll(): Flow<List<PaymentRecord>> = dao.getAll()
    suspend fun save(record: PaymentRecord) = dao.upsert(record)
    suspend fun delete(record: PaymentRecord) = dao.delete(record)
}
