package com.calculator.vault.lock.hide.photo.video.ui.recyclebin

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ItemRecyclerBinding
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.selected_recycle_data
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.selecter
import java.io.File


class RecyclerAdapter(val context: Activity) : BaseAdapter<ItemRecyclerBinding, Media_Data>(R.layout.item_recycler) {

  //  var activity: Activity? = null
    //var media_datas: ArrayList<Media_Data>? = null
    var inflater: LayoutInflater? = null
    var IsLongClick = false
    private val formatted: String? = null

    override fun setClickableView(binding: ItemRecyclerBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(
        binding: ItemRecyclerBinding,
        position: Int,
        item: Media_Data,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            val margin = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            val displaymetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displaymetrics)
            val deviceheight = (displaymetrics.widthPixels - margin) / 3
            imaegs.layoutParams.height = deviceheight
            Glide.with(context).load(item.path).into(binding.imaegs)

            if (selected_recycle_data.contains(item)) {
                selected.visibility = View.VISIBLE
            } else {
                selected.visibility = View.GONE
            }

            imaegs.setOnLongClickListener {
                if (!IsLongClick) {
                    IsLongClick = true
                    addRemoveSelectionList(binding, position,item)
                }
                true
            }

            imaegs.setOnClickListener {
                if (IsLongClick) {
                    addRemoveSelectionList(binding, position,item)
                } else {

                    val intent = Intent(context, RecyclebinShowActivity::class.java)
                    intent.putExtra("pos", position)
                    intent.putExtra("videodata", displayList)
                    context.startActivity(intent)

                }
            }

            var isImage = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val file = DocumentFile.fromSingleUri(context, Uri.parse(item.path))
                try {
                    isImage = file?.uri?.let { isImage(it) } == true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                isImage = isImage(File(item.path))
            }

            //ivIcon.text = item
        }
    }

    fun isImage(file: Uri): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return options.outWidth != -1 && options.outHeight != -1
    }
    fun isImage(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return false
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return options.outWidth != -1 && options.outHeight != -1
    }

    fun addRemoveSelectionList(holder:ItemRecyclerBinding , position: Int,item: Media_Data) {
        try {
            holder.selected.setVisibility(View.VISIBLE)
            selecter?.setVisible(true)
            RecycleBinActivity.btn_lay?.setVisibility(View.VISIBLE)
            if (selected_recycle_data.contains(item)) {
                selected_recycle_data.remove(item)
                holder.selected.setVisibility(View.GONE)
            } else {
                selected_recycle_data.add(item)
                holder.selected.setVisibility(View.VISIBLE)
            }
            if (selected_recycle_data.size == 0) {
                IsLongClick = false
                notifyItemChanged(position)
                RecycleBinActivity.selecter?.setVisible(false)
                RecycleBinActivity.IsSelectAll = false
                RecycleBinActivity.selecter?.setIcon(context.getResources().getDrawable(R.drawable.iv_unselect))
                RecycleBinActivity.btn_lay?.setVisibility(View.GONE)
            }
            if (selected_recycle_data.size == displayList.size) {
                RecycleBinActivity.IsSelectAll = true
                RecycleBinActivity.selecter?.setIcon(context.getResources().getDrawable(R.drawable.iv_select)
                )
            } else {
                RecycleBinActivity.IsSelectAll = false
                RecycleBinActivity.selecter?.setIcon(context.getResources().getDrawable(R.drawable.iv_unselect)
                )
            }
        } catch (e: java.lang.Exception) {
        }
    }

}
