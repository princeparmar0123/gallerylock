package com.calculator.vault.lock.hide.photo.video.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseAdapter
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.utils.HomeTask
import com.calculator.vault.lock.hide.photo.video.databinding.LayoutItemHomeBinding
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity
import com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.Intruder_Activity
import com.calculator.vault.lock.hide.photo.video.ui.note.SecretNoteActivity
import com.calculator.vault.lock.hide.photo.video.ui.passwords.PasswordsActivity
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.Recyclebin_Activity
import com.calculator.vault.lock.hide.photo.video.ui.video.Videos_Activity


class HomeAdapter(var activity: Activity) :
    BaseAdapter<LayoutItemHomeBinding, HomeTask>(R.layout.layout_item_home) {

    private var database: Database? = null
    var inflater: LayoutInflater? = null
    private var camera_per = 0
    var temp = -1

    override fun setClickableView(binding: LayoutItemHomeBinding): List<View?> {
        return listOf(binding.main)
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                requestCode
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                requestCode
            )
        }
    }

    override fun onBind(
        binding: LayoutItemHomeBinding,
        position: Int,
        item: HomeTask,
        payloads: MutableList<Any>?
    ) {
        binding.run {
            database = Database(activity)
            tvTitle.text = item.name
            Glide.with(activity).load(item.image).into(binding.ivIcon)
            tvItemCount.isVisible = false

            root.setOnClickListener {
                when (position) {
                    0 -> if (checkPermission()) {
                        val intent1 = Intent(activity, PhotosActivity::class.java)
                        activity.startActivity(intent1)
                    } else {
                        requestPermission(1)
                    }
                    1 -> if (checkPermission()) {
                        val intent = Intent(activity, Videos_Activity::class.java)
                        activity.startActivity(intent)
                    } else {
                        requestPermission(0)
                    }

                    2 -> if (checkPermission()) {
                        camera_per =
                            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        if (camera_per != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.CAMERA),
                                1111
                            )
                        } else {
                            val intent6 = Intent(activity, Intruder_Activity::class.java)
                            activity.startActivity(intent6)
                        }
                    } else {
                        requestPermission(6)
                    }

                    3 -> if (checkPermission()) {
                        camera_per =
                            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        if (camera_per != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(Manifest.permission.CAMERA),
                                1111
                            )
                        } else {
                            val intent6 = Intent(activity, Intruder_Activity::class.java)
                            activity.startActivity(intent6)
                        }
                    } else {
                        requestPermission(6)
                    }

                    7 -> {
                        val intent8 = Intent(activity, PasswordsActivity::class.java)
                        activity.startActivity(intent8)
                    }
                    4 -> {
                        val intent4 = Intent(activity, SecretNoteActivity::class.java)
                        activity.startActivity(intent4)
                    }
                    5 -> {
                        val intent5 = Intent(activity, ContactActivity::class.java)
                        activity.startActivity(intent5)
                    }

                    6 -> if (checkPermission()) {
                        val intent7 = Intent(activity, Recyclebin_Activity::class.java)
                        activity.startActivity(intent7)
                    } else {
                        requestPermission(7)
                    }

                    7 -> if (checkPermission()) {
                        val intent7 = Intent(activity, Recyclebin_Activity::class.java)
                        activity.startActivity(intent7)
                    } else {
                        requestPermission(7)
                    }

                    8 -> if (checkPermission()) {
                        val intent7 = Intent(activity, Recyclebin_Activity::class.java)
                        activity.startActivity(intent7)
                    } else {
                        requestPermission(7)
                    }
                }
            }
        }
    }

}
