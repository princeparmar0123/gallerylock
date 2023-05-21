package com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie

import android.app.Activity
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ItemPhotoBinding
import com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.IntruderSelfieActivity.Companion.floating_btn_intruder
import com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.IntruderSelfieActivity.Companion.selected_intruder_data
import com.calculator.vault.lock.hide.photo.video.ui.photos.ImageShowActivity
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity

class IntruderSelfieAdapter(val context: Activity) : BaseAdapter<ItemPhotoBinding, Media_Data>(R.layout.item_photo) {

    var inflater: LayoutInflater? = null
    var IsLongClick = false

    override fun setClickableView(binding: ItemPhotoBinding): List<View?> {
        return listOf(binding.root)
    }

    override fun onBind(binding: ItemPhotoBinding, position: Int, item: Media_Data, payloads: MutableList<Any>?) {
        binding.run {
            val margin = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            val displaymetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displaymetrics)
            val deviceheight = (displaymetrics.widthPixels - margin) / 3
            imaegs.layoutParams.height = deviceheight
            Glide.with(context).load(item.path).override(1080, 600)
                .into(this.imaegs)
            if (selected_intruder_data.contains(item)) {
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
                    PhotosActivity.hide_media_data = ArrayList()
                    PhotosActivity.hide_media_data.addAll(displayList)
                    val intent = Intent(context, ImageShowActivity::class.java)
                    intent.putExtra("pos", position)
                    intent.putExtra("type", 1)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun addRemoveSelectionList(holder: ItemPhotoBinding, position: Int ,item: Media_Data) {
        try {
            holder.selected.visibility = View.VISIBLE
            floating_btn_intruder?.visibility = View.VISIBLE
            IntruderSelfieActivity.selecter?.isVisible = true
            if (selected_intruder_data.contains(item)) {
                selected_intruder_data.remove(item)
                holder.selected.visibility = View.GONE
            } else {
                selected_intruder_data.add(item)
                holder.selected.visibility = View.VISIBLE
            }
            if (selected_intruder_data.size == 0) {
                IsLongClick = false
                floating_btn_intruder?.visibility = View.GONE
                notifyItemChanged(position)
                IntruderSelfieActivity.selecter?.isVisible = false
                IntruderSelfieActivity.IsSelectAll = false
                IntruderSelfieActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
            }
            if (selected_intruder_data.size == displayList.size) {
                IntruderSelfieActivity.IsSelectAll = true
                IntruderSelfieActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_select)
            } else {
                IntruderSelfieActivity.IsSelectAll = false
                IntruderSelfieActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
            }
        } catch (e: Exception) {
        }
    }
}