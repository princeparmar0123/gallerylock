package com.common.data.network.api

import com.calculator.vault.lock.hide.photo.video.common.data.network.model.ResponseUser
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.request.ReqLogin
import com.google.gson.GsonBuilder
import com.calculator.vault.lock.hide.photo.video.common.data.network.api.IBaseService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IApiService2 : IBaseService {

    @POST("login")
    suspend fun login(@Body reqLogin: ReqLogin): Response<ResponseUser>

    @GET("dummy")
    suspend fun dummy(): Response<Any>

    companion object {
        fun getService(needEncrypt: Boolean): IApiService2 {
            return Retrofit.Builder()
                .baseUrl("")
                .client(IBaseService.getOkHttpClient(needEncrypt))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(IApiService2::class.java)
        }
    }
}