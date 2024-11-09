package com.example.carApi.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"
    //Professor Vagner, eu usei meu IP aqui na minha
    // máquina porque executo o app no celular, mas deixei 10.0.2.2 para você executar aí

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // converte o json para o firebase
            .build()
    }

    val apiService = instance.create(ApiService::class.java)
}