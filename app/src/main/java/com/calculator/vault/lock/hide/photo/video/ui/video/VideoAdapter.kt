//package com.calculator.vault.lock.hide.photo.video.ui.video
//
////import com.master.valultcalculator.ui.video.VideoFolderActivity.Companion.select_video_datas
//import android.app.Activity
//import android.content.Intent
//import android.media.MediaMetadataRetriever
//import android.net.Uri
//import android.os.Bundle
//import android.text.TextUtils
//import android.util.DisplayMetrics
//import android.view.View
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.request.RequestOptions
//import com.calculator.vault.lock.hide.photo.video.R
//import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
//import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
//import com.calculator.vault.lock.hide.photo.video.databinding.ItemVideoBinding
//import com.calculator.vault.lock.hide.photo.video.ui.video.VideosActivity.Companion.floating_btn_video
//import com.calculator.vault.lock.hide.photo.video.ui.video.VideosActivity.Companion.selected_hide_video_data
//import com.calculator.vault.lock.hide.photo.video.ui.video.VideosActivity.Companion.video_btn_lay
//import java.io.File
//import java.io.Serializable
//
//
//class VideoAdapter(var context: Activity) : BaseAdapter<ItemVideoBinding, Media_Data>(R.layout.item_video) {
//
//    //var media_datas: ArrayList<Media_Data>? = null
//    private var formatted: String? = null
//    var IsLongClick = false
//
//    override fun setClickableView(binding: ItemVideoBinding): List<View?> {
//        return listOf(binding.imaegs)
//    }
//
//    override fun onBind(
//        binding: ItemVideoBinding,
//        position: Int,
//        item: Media_Data,
//        payloads: MutableList<Any>?
//    ) {
//        binding.run {
//
//            val margin: Int = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
//            val displaymetrics = DisplayMetrics()
//            context.windowManager.defaultDisplay.getMetrics(displaymetrics)
//            val deviceheight = (displaymetrics.widthPixels - margin) / 3
//            imaegs.layoutParams.height = deviceheight
//            Glide.with(context).load(item.path)
//                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
//                .into(binding.imaegs)
//            //ivIcon.text = item
//
//            if (selected_hide_video_data.contains(item)) {
//                selected.visibility = View.VISIBLE
//            } else {
//                selected.visibility = View.GONE
//            }
//
//
//
//            imaegs.setOnClickListener {
//                if (IsLongClick) {
//                    addRemoveSelectionList(binding, position,item)
//                } else {
//                    val intent = Intent(context, VideoShowActivity::class.java)
//                    val bundle = Bundle()
//                    bundle.putSerializable("videodata", displayList as Serializable?)
//                    bundle.putInt("pos", position)
//                    bundle.putInt("type", 0)
//                    intent.putExtra("BUNDLE", bundle)
//                    context.startActivity(intent)
//
////                    intent.putExtra("pos", position)
////                    intent.putExtra("videodata",item)
////                    intent.putExtra("type", 0)
////                    context.startActivity(intent)
//                }
//
//            }
//            imaegs.setOnLongClickListener {
//                if (!IsLongClick) {
//                    IsLongClick = true
//                    addRemoveSelectionList(binding, position,item)
//                }
//                true
//            }
//
//            try {
//                val retriever = MediaMetadataRetriever()
//                //            FileInputStream input = new FileInputStream(media_datas.get(position).getPath());
//                val file = item.path?.let { File(it) }
//                if (file?.exists() == true) {
//                    retriever.setDataSource(context, Uri.parse(file.absolutePath))
//                    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                    if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
//                        val timeInMillisec = time!!.toInt()
//                        val duration = timeInMillisec / 1000
//                        val hours = duration / 3600
//                        val minutes = duration / 60 - hours * 60
//                        val seconds = duration - hours * 3600 - minutes * 60
//                        if (hours == 0) {
//                            formatted = String.format("%02d:%02d", minutes, seconds)
//                        } else {
//                            formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
//                        }
//                    } else {
//                        formatted = String.format("%02d:%02d", 0, 0)
//                    }
//                    retriever.release()
//                    duration.text = formatted
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    fun addRemoveSelectionList(holder: ItemVideoBinding, position: Int,item: Media_Data) {
//        try {
//            holder.selected.visibility = View.VISIBLE
//            floating_btn_video?.visibility = View.GONE
//            video_btn_lay?.visibility = View.VISIBLE
//            VideosActivity.selecter?.isVisible = true
//            if (selected_hide_video_data.contains(item)) {
//                selected_hide_video_data.remove(item)
//                holder.selected.visibility = View.GONE
//            } else {
//                selected_hide_video_data.add(item)
//                holder.selected.visibility = View.VISIBLE
//            }
//            if (selected_hide_video_data.size == 0) {
//                IsLongClick = false
//                floating_btn_video!!.visibility = View.VISIBLE
//                video_btn_lay!!.visibility = View.GONE
//                notifyItemChanged(position)
//                VideosActivity.IsSelectAll = false
//                VideosActivity.selecter?.isVisible = false
//                VideosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
//            }
//            if (selected_hide_video_data.size == displayList.size) {
//                VideosActivity.IsSelectAll = true
//                VideosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_select)
//            } else {
//                VideosActivity.IsSelectAll = false
//                VideosActivity.selecter?.icon = context.resources.getDrawable(R.drawable.iv_unselect)
//            }
//            if (selected_hide_video_data.size == 0) {
//                context.title = "Videos"
//            } else {
//                context.title = "Videos(" + selected_hide_video_data.size.toString() + ")"
//            }
//        } catch (e: java.lang.Exception) {
//        }
//    }
//
//
//}
