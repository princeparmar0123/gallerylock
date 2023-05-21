package com.calculator.vault.lock.hide.photo.video.common.data.network.repository

import com.common.data.network.api.IApiService2
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.request.ReqLogin


class Api2Repository(
    private val apiService: IApiService2
) : BaseRepository() {

    suspend fun login(reqLogin: ReqLogin) = callApi { apiService.login(reqLogin) }
    suspend fun dummy() = callApi { apiService.dummy() }

    companion object {
        @Volatile
        private var instance: Api2Repository? = null

        fun getInstance(): Api2Repository {
            return instance ?: synchronized(this) {
                instance ?: Api2Repository(IApiService2.getService(false)).also { instance = it }
            }
        }
    }
}