package com.calculator.vault.lock.hide.photo.video.common.data.network.api

import com.calculator.vault.lock.hide.photo.video.common.data.network.api.IBaseService.Companion.getOkHttpClient
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.UserInfo
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface IApiService1 : IBaseService {

    @GET("api/users?page=2")
    suspend fun getUsers(): Response<List<UserInfo>>

    companion object {
        fun getService(needEncrypt: Boolean): IApiService1 {
            return Retrofit.Builder()
                .baseUrl("")
                .client(getOkHttpClient(needEncrypt))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create(IApiService1::class.java)
        }
    }
}
