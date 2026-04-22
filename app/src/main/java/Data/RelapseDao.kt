package com.example.iffah.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseDao {
    @Insert
    suspend fun insertRelapse(relapse: RelapseEntity)

    @Query("SELECT * FROM relapses ORDER BY timestamp DESC")
    fun getAllRelapses(): Flow<List<RelapseEntity>>

    @Query("DELETE FROM relapses")
    suspend fun deleteAllRelapses()
}