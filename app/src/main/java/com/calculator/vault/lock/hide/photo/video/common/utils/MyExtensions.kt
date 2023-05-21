package com.calculator.vault.lock.hide.photo.video.common.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import java.io.File

fun View.closeSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun RecyclerView.setAdapter(adapter: RecyclerView.Adapter<*>?, emptyView: View?) {
    val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty(emptyView)
            super.onChanged()
        }


        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty(emptyView)
            super.onItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty(emptyView)
            super.onItemRangeRemoved(positionStart, itemCount)
        }
    }

    this.adapter?.unregisterAdapterDataObserver(observer)
    this.adapter = adapter
    if (adapter is BaseAdapter<*, *>) {
        adapter.setEmptyView(emptyView)
    }
    adapter?.registerAdapterDataObserver(observer)
//    checkIfEmpty(emptyView)
}

fun RecyclerView.checkIfEmpty(emptyView: View?) {
    if (emptyView != null && adapter != null) {
        val emptyViewVisible = adapter?.itemCount == 0
        emptyView.isVisible = emptyViewVisible
        isInvisible = emptyViewVisible
    }
}

private fun RecyclerView.checkIfEmpty(emptyView: View?, goneView: View?) {
    if (emptyView != null && adapter != null) {
        val emptyViewVisible = adapter?.itemCount == 0
        emptyView.isVisible = emptyViewVisible
        goneView?.isVisible = false
        isVisible = !emptyViewVisible
    }
}

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this)
    }
}


fun TextView.makeLinks(
    @ColorInt linkColor: Int = 0,
    vararg links: Pair<String, ((view: View) -> Unit)>
) {
    try {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {

                override fun updateDrawState(textPaint: TextPaint) {
                    // use this to change the link color
                    if (linkColor == 0)
                        textPaint.color = textPaint.linkColor
                    else textPaint.color = linkColor
                    // toggle below value to enable/disable
                    // the underline shown below the clickable text
                    textPaint.isUnderlineText = true
                }

                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first, ignoreCase = true)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    fun Any?.isNull() = this == null

    fun Any?.isNotNull() = this != null

    /*how to used
       context.getCompatColor(R.color.green_500)
    * */
    fun Context.getCompatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

    /*how to used
      context.getCompatDrawable(R.drawable.background_positive_chart)
   * */

    fun Context.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

    fun Context.darkModeEnabled(): Boolean =
        (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) == Configuration.UI_MODE_NIGHT_YES

    fun ImageView.drawableRes(@DrawableRes drawableRes: Int?) {
        if ((drawableRes.isNotNull()) and (drawableRes != Resources.ID_NULL)) {
            setImageDrawable(context.getCompatDrawable(drawableRes!!))
        }
    }

}