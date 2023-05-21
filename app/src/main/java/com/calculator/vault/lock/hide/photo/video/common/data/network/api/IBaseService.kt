package com.calculator.vault.lock.hide.photo.video.common.data.network.api

import com.calculator.vault.lock.hide.photo.video.BuildConfig
import com.calculator.vault.lock.hide.photo.video.common.data.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

interface IBaseService {
    companion object {

        const val Authorization = "Authorization"
        const val IS_REFRESH_TOKEN = "isRefreshToken"
        const val Accept = "Accept"
        const val DeviceType = "device-type"
        private const val TIME_OUT = 120L

        fun getOkHttpClient(needEncrypt: Boolean): OkHttpClient {
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS)
            httpClient.writeTimeout(TIME_OUT, TimeUnit.SECONDS)

            if (needEncrypt) {
//                httpClient.addInterceptor(EncryptionInterceptor())
//                httpClient.addInterceptor(DecryptionInterceptor())
            }
            httpClient.addInterceptor(AuthInterceptor())

//            httpClient.addInterceptor { chain ->
//                val original = chain.request()
//                val requestBuilder = original.newBuilder()
//                    .method(original.method, original.body)
//                    .header(Accept, "application/json")
//                    .header(DeviceType, "A")
//
//                if (!pref.authToken.isNullOrBlank()) {
//                    requestBuilder.header(Authorization, "Bearer ${pref.authToken}")
//                }
//
//                return@addInterceptor chain.proceed(requestBuilder.build())
//            }

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }
                httpClient.addInterceptor(logging)
            }
            return httpClient.build()
        }
    }
}