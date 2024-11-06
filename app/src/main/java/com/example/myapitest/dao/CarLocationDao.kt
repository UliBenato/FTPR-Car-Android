package com.example.myapitest.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapitest.database.model.CarLocation

@Dao
interface CarLocationDao {
    @Insert
    suspend fun  insert(carLocation: CarLocation)

    @Query("SELECT  * FROM CAR_LOCATION_TABLE")
    suspend fun getAllLocations(): List<CarLocation>

    @Query("SELECT * FROM CAR_LOCATION_TABLE ORDER BY id DESC LIMIT 1")
    suspend fun getLastLocation(): CarLocation?
}