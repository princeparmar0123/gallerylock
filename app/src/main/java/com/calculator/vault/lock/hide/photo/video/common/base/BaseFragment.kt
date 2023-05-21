package com.calculator.vault.lock.hide.photo.video.common.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.calculator.vault.lock.hide.photo.video.common.data.database.daos.AppDao
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.SharedPref
import com.calculator.vault.lock.hide.photo.video.common.utils.AppLoader
import com.google.android.material.snackbar.Snackbar
import com.calculator.vault.lock.hide.photo.video.App
import kotlinx.coroutines.channels.ReceiveChannel


abstract class BaseFragment<VB : ViewDataBinding>(@LayoutRes val layoutRes: Int) : Fragment() {

    protected lateinit var binding: VB
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()
    private val appLoader: AppLoader by lazy { AppLoader(requireActivity()) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val pref: SharedPref by lazy { App.getInstance().getPref() }
    val dao: AppDao by lazy { App.getInstance().getDao() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    open fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    open fun showError(errorMessage: String) {
        showMessage(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        listSubscription.forEach { it.cancel() }
    }

    protected fun handleError(it: Throwable) {
        val activity = requireActivity()
        if (activity is BaseActivity<*>) activity.handleError(it)
    }
}

