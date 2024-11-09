package com.example.carApi

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.carApi.dao.CarLocationDao
import com.example.carApi.database.converters.DateConverter
import com.example.carApi.database.model.CarLocation


@Database(entities = [CarLocation::class], version = 2, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userLocationDoa(): CarLocationDao

}