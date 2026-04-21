package com.example.iffah.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relapses")
data class RelapseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trigger: String,
    val timestamp: Long = System.currentTimeMillis()
)