package com.calculator.vault.lock.hide.photo.video.network

import com.calculator.vault.lock.hide.photo.video.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object ApiClient {
    const val BASE_URL = "https://indigenoustechnology.in/app/api/"
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(if (BuildConfig.BUILD_TYPE == "debug") HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
            val client: OkHttpClient.Builder = OkHttpClient.Builder()
            client.connectTimeout(30, TimeUnit.SECONDS)
            client.readTimeout(10, TimeUnit.MINUTES)
            client.addInterceptor(interceptor)
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}