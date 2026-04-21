package com.example.iffah.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RelapseEntity::class], version = 1, exportSchema = false)
abstract class IffahDatabase : RoomDatabase() {

    abstract fun relapseDao(): RelapseDao

    companion object {
        @Volatile
        private var INSTANCE: IffahDatabase? = null

        fun getDatabase(context: Context): IffahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IffahDatabase::class.java,
                    "iffah_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}