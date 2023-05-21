package com.calculator.vault.lock.hide.photo.video.common.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.SharedPref
import com.calculator.vault.lock.hide.photo.video.common.utils.AppLoader
import com.google.android.material.snackbar.Snackbar
import com.calculator.vault.lock.hide.photo.video.common.data.database.daos.AppDao
import com.calculator.vault.lock.hide.photo.video.App
import kotlinx.coroutines.channels.ReceiveChannel

abstract class BaseDialogFragment<VB : ViewDataBinding>(@LayoutRes val layoutRes: Int) :
    DialogFragment() {

    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    protected lateinit var binding: VB
    protected val listSubscription = ArrayList<ReceiveChannel<*>>()

    val pref: SharedPref by lazy { App.getInstance().getPref() }
    val dao: AppDao by lazy { App.getInstance().getDao() }
    private val appLoader: AppLoader by lazy { AppLoader(requireActivity()) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val lp = this.dialog?.window?.attributes
        lp?.copyFrom(this.dialog?.window?.attributes)
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = WindowManager.LayoutParams.MATCH_PARENT
        this.dialog?.window?.attributes = lp
        lp?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        listSubscription.forEach { it.cancel() }
    }

    protected fun updateLoaderUI(isShow: Boolean) {
        if (isShow) appLoader.show() else appLoader.dismiss()
    }

    protected open fun showSoftwareKeyboard(showKeyboard: Boolean) {
        val inputManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            if (showKeyboard) InputMethodManager.SHOW_FORCED else InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    open fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    open fun showError(errorMessage: String) {
        showMessage(errorMessage)
    }
}