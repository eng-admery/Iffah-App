package com.example.iffah.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    // اليوميات
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): kotlinx.coroutines.flow.Flow<List<JournalEntry>>

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntry)
}