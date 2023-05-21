package com.calculator.vault.lock.hide.photo.video.common.data.network.repository

import com.calculator.vault.lock.hide.photo.video.common.data.network.api.IApiService1

class Api1Repository(private val apiService: IApiService1) : BaseRepository() {

    suspend fun getUsers() = callApi { apiService.getUsers() }

    suspend fun getUsers2() = callApi { apiService.getUsers() }

    companion object {
        @Volatile
        private var instance: Api1Repository? = null

        fun getInstance(): Api1Repository {

            return instance ?: synchronized(this) {
                instance ?: Api1Repository(IApiService1.getService(false))
                    .also {
                        instance = it
                    }
            }
        }
    }
}