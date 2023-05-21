package com.calculator.vault.lock.hide.photo.video.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calculator.vault.lock.hide.photo.video.common.data.ApiError
import com.calculator.vault.lock.hide.photo.video.common.data.ApiEvent
import com.calculator.vault.lock.hide.photo.video.common.data.ApiSuccess
import com.calculator.vault.lock.hide.photo.video.common.data.Event
import com.calculator.vault.lock.hide.photo.video.common.data.network.repository.Api1Repository
import com.calculator.vault.lock.hide.photo.video.common.data.network.repository.Api2Repository
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.SharedPref
import com.calculator.vault.lock.hide.photo.video.common.data.database.daos.AppDao
import com.calculator.vault.lock.hide.photo.video.App
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


open class BaseViewModel : ViewModel(), CoroutineScope {

    val sharedPref: SharedPref by lazy { App.getInstance().getPref() }
    protected val api1Repository: Api1Repository by lazy { Api1Repository.getInstance() }
    protected val api2Repository: Api2Repository by lazy { Api2Repository.getInstance() }
    protected val dao: AppDao by lazy { App.getInstance().getDao() }

    private val errorHandler = CoroutineExceptionHandler { _, throwable -> Timber.w(throwable) }
    private val vmJob = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + vmJob + errorHandler

    private val _apiErrors = MutableLiveData<Throwable>()
    val apiErrors: LiveData<Throwable> get() = _apiErrors

    private val _appLoader = MutableLiveData<Boolean>()
    val appLoader: LiveData<Boolean> get() = _appLoader


    protected inline fun <T> processDataEvent(
        result: Event<T>,
        onError: (value: Throwable) -> Unit = {},
        onSuccess: (value: T) -> Unit
    ) {
        when (result) {
            is ApiEvent -> handleApiEvent(result, onSuccess, onError)
        }
    }

    protected inline fun <T> handleApiEvent(
        result: ApiEvent<T>,
        onSuccess: (value: T) -> Unit,
        onError: (value: Throwable) -> Unit
    ) {
        try {
            when (result) {
                is ApiError -> {
                    dismissLoader()
                    showError(result.error)
                    onError(result.error)
                }
                is ApiSuccess -> {
                    dismissLoader()
                    result.response?.let { onSuccess(it) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected open fun showError(error: Throwable) {
        _apiErrors.value = error
    }

    private var loaderCount = 0

    fun dismissLoader() {
        loaderCount--
        if (loaderCount <= 0) {
            loaderCount = 0
            _appLoader.value = false
        }
    }

    fun displayLoader() {
        loaderCount++
        if (loaderCount == 1) _appLoader.value = true
    }
}