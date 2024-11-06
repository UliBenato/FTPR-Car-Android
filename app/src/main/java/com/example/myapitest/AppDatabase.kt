package com.example.myapitest

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapitest.dao.CarLocationDao
import com.example.myapitest.database.converters.DateConverter
import com.example.myapitest.database.model.CarLocation


@Database(entities = [CarLocation::class], version = 2, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userLocationDoa(): CarLocationDao

}