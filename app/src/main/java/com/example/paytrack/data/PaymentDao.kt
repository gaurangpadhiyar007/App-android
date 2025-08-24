
package com.example.paytrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: PaymentRecord)

    @Delete
    suspend fun delete(record: PaymentRecord)

    @Query("SELECT * FROM payments ORDER BY dateEpoch DESC")
    fun getAll(): Flow<List<PaymentRecord>>
}
