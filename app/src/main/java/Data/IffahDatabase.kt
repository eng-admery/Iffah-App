package com.example.iffah.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RelapseEntity::class, JournalEntry::class], version = 2, exportSchema = false)
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
                )
                    // هذا هو السطر السحري الذي يصلح المشكلة: يسمح بتدمير البيانات القديمة مؤقتاً عند تغيير نسخة قاعدة البيانات
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}