package com.example.carApi.database

import android.content.Context
import androidx.room.Room
import com.example.carApi.AppDatabase

object DatabaseBuilder {
    private var instance: AppDatabase? = null

    fun getInstance(context: Context? = null): AppDatabase {
        return instance ?: synchronized(this) {
            if (context == null) {
                throw Exception("DatabaseBuilder.getInstance(context) deve ser inicializado antes de ser utilizado")
            }
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .build()
            instance = newInstance
            newInstance
        }
    }
}