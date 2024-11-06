package com.example.myapitest.services


import com.example.myapitest.dao.CarLocationDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class GeoLocationInterceptor(private val userLocationDao: CarLocationDao): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val carLastLocation =  runBlocking {
            userLocationDao.getLastLocation()
        }
        val originalRequest: Request = chain.request()
        val newRequest = carLastLocation?.let {
            originalRequest.newBuilder()
                .addHeader("x-data-latitude", carLastLocation.latitude.toString())
                .addHeader("x-data-longitude", carLastLocation.longitude.toString())
                .build()
        } ?: originalRequest
        return  chain.proceed(newRequest)

    }
}