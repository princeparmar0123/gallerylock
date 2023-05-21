package com.calculator.vault.lock.hide.photo.video.ui.photos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.databinding.ItemPhotoBinding
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity.*


class PhotosFolderAdapter(var context: Context, var activity: Activity) : BaseAdapter<ItemPhotoBinding, Media_Data>(
    R.layout.item_photo) {

    //var media_datas: ArrayList<Media_Data>? = null
    private var formatted: String? = null
    var IsLongClick = false

    override fun setClickableView(binding: ItemPhotoBinding): List<View?> {
        return listOf(binding.imaegs)
    }

    override fun onBind(
        binding: ItemPhotoBinding,
        position: Int,
        item: Media_Data,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            val margin: Int = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            val displaymetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displaymetrics)
            val deviceheight = (displaymetrics.widthPixels - margin) / 3
            imaegs.layoutParams.height = deviceheight
            Glide.with(context).load(item.path)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.imaegs)
            //ivIcon.text = item


            if (selected_hide_media_data.contains(item)) {
                selected.visibility = View.VISIBLE
            } else {
                selected.visibility = View.GONE
            }

//            if (select_video_datas.contains(item)) {
//                selected.visibility = View.VISIBLE
//            } else {
//                selected.visibility = View.GONE
//            }

            imaegs.setOnLongClickListener {
                if(!IsLongClick){
                    IsLongClick = true
                    addRemoveSelectionList(binding,position,item)
                }
                return@setOnLongClickListener true
            }
            imaegs.setOnClickListener {
                if(IsLongClick){
                    addRemoveSelectionList(binding,position,item)
                }else{
                    PhotosActivity.hide_media_data = ArrayList()
                    PhotosActivity.hide_media_data.addAll(displayList)
                    val intent = Intent(activity, ImageShowActivity::class.java)
                    intent.putExtra("pos", position)
                    intent.putExtra("type", 0)
//                    intent.putExtra("photodata", media_datas);
                    //                    intent.putExtra("photodata", media_datas);
                    activity.startActivity(intent)
                }
            }

        }
    }

   private fun addRemoveSelectionList(holder: ItemPhotoBinding, position: Int,item: Media_Data) {
       try {
           holder.selected.visibility = View.VISIBLE
           floating_btn_photo?.visibility = View.GONE
           photo_btn_lay?.visibility = View.VISIBLE
           PhotosActivity.selecter?.isVisible = true
           if (selected_hide_media_data.contains(item)) {
               selected_hide_media_data.remove(item)
               holder.selected.visibility = View.GONE
           } else {
               selected_hide_media_data.add(item)
               holder.selected.visibility = View.VISIBLE
           }
           if (selected_hide_media_data.size == 0) {
               floating_btn_photo?.visibility = View.VISIBLE
               photo_btn_lay?.visibility = View.GONE
               IsLongClick = false
               notifyItemChanged(position)
               IsSelectAll = false
               PhotosActivity.selecter?.isVisible = false
               PhotosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
           }
           if (selected_hide_media_data.size == displayList.size) {
               IsSelectAll = true
               PhotosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_select)
           } else {
               IsSelectAll = false
               PhotosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
           }
           if (selected_hide_media_data.size == 0) {
               activity.title = "Photos"
           } else {
               activity.title = "Photos"
//               activity.title = "Photos(" + selected_hide_media_data.size.toString() + ")"
           }
       } catch (e: java.lang.Exception) {
           e.printStackTrace()
       }
    }


}
