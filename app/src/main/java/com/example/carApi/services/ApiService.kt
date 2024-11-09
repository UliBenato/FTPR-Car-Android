package com.example.carApi.services

import com.example.carApi.model.Car
import com.example.carApi.model.CarValue

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("car")
    suspend fun getItems(): List<Car>

    @GET("car/{id}")
    suspend fun getItem(@Path("id") id: String): CarValue

    @DELETE("car/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @POST("car")
    suspend fun addItem(@Body item: Car): CarValue

    @PATCH("car/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: Car ):CarValue
}