package com.example.carApi.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.16:3000/" //10.0.2.2 ou ip 192.168.1.45
    //É o endereço usado para acessar o localhost no emulador android - arrumar no xml tb - arquivo de permissão

    /*private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        //.addInterceptor(GeoLocationInterceptor(DatabaseBuilder.getInstance().userLocationDoa()))
        .build()*/

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // converte o json para o firebase
            .build()
    }

    val apiService = instance.create(ApiService::class.java)
}