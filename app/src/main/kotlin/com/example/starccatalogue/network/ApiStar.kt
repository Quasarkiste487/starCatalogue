package com.example.starccatalogue.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlin.jvm.java

private val okHttpClient = OkHttpClient.Builder()
    .addNetworkInterceptor(
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    )
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl("https://jsonplaceholder.typicode.com/")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val starsApi = retrofit.create(StarsApi::class.java)

interface StarsApi{
    @GET("stars")
    suspend fun getStars(): Response<List<ApiStar>>
}