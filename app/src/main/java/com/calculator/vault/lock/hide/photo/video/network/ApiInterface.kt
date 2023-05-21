package com.calculator.vault.lock.hide.photo.video.network

import com.calculator.vault.lock.hide.photo.video.model.AdsData
import com.calculator.vault.lock.hide.photo.video.model.InstallResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {
    @Multipart
    @POST("app/ads")
    fun getAdIds(@Part("package_name") ipackage_named: RequestBody?): Call<AdsData?>?

    @Multipart
    @POST("app/appInstall")
    fun newInstall(@Part("package_name") ipackage_named: RequestBody?): Call<InstallResponse?>?
}