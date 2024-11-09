package com.example.carApi.model

data class CarValue(
    val id: String,
    val value: Car
)


data class Car(
    val id: String,
    val imageUrl: String,
    val year : String,
    val name: String,
    val licence: String,
    val place: CarLocation?
)

data class CarLocation(
    val lat:Double,
    val long: Double
)