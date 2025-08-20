package com.talhasari.growlistapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.talhasari.growlistapp.data.local.db.entity.Plant

@Database(entities = [Plant::class], version = 4, exportSchema = false)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"

                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}