package com.calculator.vault.lock.hide.photo.video.ui.home

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.calculator.vault.lock.hide.photo.video.App.Companion.getInstance
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.utils.CloseAdDialog
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.Constants
import com.calculator.vault.lock.hide.photo.video.common.utils.DummyClass
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityHomeBinding
import com.calculator.vault.lock.hide.photo.video.ui.cloud.CloudActivity
import com.calculator.vault.lock.hide.photo.video.ui.contact.ContactActivity
import com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie.Intruder_Activity
import com.calculator.vault.lock.hide.photo.video.ui.note.SecretNoteActivity
import com.calculator.vault.lock.hide.photo.video.ui.passwords.PasswordsActivity
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.Recyclebin_Activity
import com.calculator.vault.lock.hide.photo.video.ui.settings.SettingsActivity

import com.calculator.vault.lock.hide.photo.video.ui.video.Videos_Activity
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home),
    View.OnClickListener {

    private var adapter: HomeAdapter? = null
    private var camera_per = 0
    private var mUri: Uri? = null
    private var mUri_video: Uri? = null
    private var camera_dialog: Dialog? = null
    private var storageDir: File? = null
    private var mCurrentPhotoPath: String? = null
    private var image: File? = null
    private var temp = -1
    var exitDialog: Dialog? = null
    var isNativeLoad = false

    var rating_count = 0f

    var home_module_name = arrayOf(
        "Videos",
        "Photos",
        "Camera",
        "Secret Note",
        "Contact",
        "Password",
        "Intruder Selfie",
        "Recycle Bin"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        onClickListener()
        loadBanner()
        exitDialog()
    }

    fun exitDialog() {
        exitDialog = Dialog(this, R.style.WideDialogSecondExit)
        exitDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exitDialog?.setContentView(R.layout.bottom_navigation_exit)
        exitDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = exitDialog?.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        window?.attributes = wlp
        loadNativeAdExit()
        exitDialog?.setOnDismissListener { exitDialog() }
        exitDialog?.setOnCancelListener { exitDialog() }
    }

    private fun loadNativeAdExit() {

    }

    private fun loadBanner() {

    }


    override fun onBackPressed() {

            CloseAdDialog(
                this
            ) { finish() }.show()

    }

    private fun init() {
        adapter = HomeAdapter(this)
        binding.rvFiles.adapter = adapter
        adapter?.addAll(DummyClass.task)
    }

    private fun onClickListener() {
        binding.llCloudStorage.setOnClickListener {
            startActivity<CloudActivity>()
        }
        binding.ivMenu.setOnClickListener(this)

        adapter?.setItemClickListener { view, position, s ->
            when (position) {
                0 -> {
                    startActivity<Videos_Activity>()
                }
                1 -> {
                    startActivity<PhotosActivity>()
                }
                2 -> {
                    startActivity<SecretNoteActivity>()
                }
                3 -> {
                    startActivity<ContactActivity>()
                }
                4 -> {
                    startActivity<Intruder_Activity>()
                }
                5 -> {
                    startActivity<Recyclebin_Activity>()
                }
                6 -> {
                    startActivity<PasswordsActivity>()
                }
                7 -> {
                    startActivity<CloudActivity>()
                }
            }
        }
    }


    private fun initExitDialog() {
        val delete_dialog = AlertDialog.Builder(this, R.style.AlertDialogCustom1)
            .setTitle(Html.fromHtml(resources.getString(R.string.exit_string)))
            .setMessage(Html.fromHtml(resources.getString(R.string.exit_warning)))
            .setPositiveButton(Html.fromHtml(resources.getString(R.string.exit_yes)))
            { dialog: DialogInterface, whichButton: Int ->
                dialog.dismiss()
                Constants.isSplashScreen = true
                finishAffinity()
            }
            .setNegativeButton(
                Html.fromHtml(resources.getString(R.string.exit_cancel))
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
        delete_dialog.show()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1111 -> {
                val intent1111 = Intent(this, Intruder_Activity::class.java)
                startActivity(intent1111)
            }
            110 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initDialog()
                } else {
                    Toast.makeText(
                        this,
                        "Please allow audio permission...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Please allow camera permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            111 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initDialog()
            } else {
                Toast.makeText(
                    this,
                    "Please allow camera permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            112 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initDialog()
            } else {
                Toast.makeText(
                    this,
                    "Please allow audio permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            0 -> if (checkPermission()) {
                val intent = Intent(this, Videos_Activity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Please allow storage permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            1 -> if (checkPermission()) {
                val intent1 = Intent(this, PhotosActivity::class.java)
                startActivity(intent1)
            } else {
                Toast.makeText(
                    this,
                    "Please allow storage permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            6 -> if (checkPermission()) {
                camera_per = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                if (camera_per != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        1111
                    )
                } else {
                    val intent6 = Intent(this, Intruder_Activity::class.java)
                    startActivity(intent6)
                }
            } else {
                Toast.makeText(
                    this,
                    "Please allow storage permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            7 -> if (checkPermission()) {
                val intent7 = Intent(this, Recyclebin_Activity::class.java)
                startActivity(intent7)
            } else {
                Toast.makeText(
                    this,
                    "Please allow storage permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initDialog() {
        temp = -1
        camera_dialog = Dialog(this, R.style.WideDialog)
        camera_dialog?.setContentView(R.layout.dialog_camera)
        camera_dialog?.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        val close = camera_dialog?.findViewById<ImageView>(R.id.close)
        val dialog_camera = camera_dialog?.findViewById<LinearLayout>(R.id.dialog_camera)
        val dialog_video = camera_dialog?.findViewById<LinearLayout>(R.id.dialog_video)
        val done = camera_dialog?.findViewById<Button>(R.id.done)
        close?.setOnClickListener { v: View? -> camera_dialog?.dismiss() }
        dialog_camera?.setOnClickListener { v: View? ->
            dialog_camera.background = resources.getDrawable(R.drawable.edit_border)
            dialog_video?.background = null
            temp = 0
        }
        dialog_video?.setOnClickListener { v: View? ->
            dialog_video.background = resources.getDrawable(R.drawable.edit_border)
            dialog_camera?.background = null
            temp = 1
        }
        done?.setOnClickListener { v: View? ->
            if (!pref.hiddenPermission) {
                camera_dialog?.dismiss()
                val i = Intent(this, PhotosActivity::class.java)
                startActivity(i)
            } else {
                if (temp != -1) {
                    initCamera(temp)
                    camera_dialog?.dismiss()
                } else {
                    Toast.makeText(this, "Select any option for both...", Toast.LENGTH_SHORT).show()
                }
            }
        }
        camera_dialog?.show()
        camera_dialog?.setCancelable(false)
    }


    private fun initCamera(temp: Int) {
        if (temp == 0) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var file: File? = null
            try {
                file = createImageFile(0)
                mUri = FileProvider.getUriForFile(
                    application.applicationContext, "$packageName.provider",
                    file!!
                )
                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    mUri = Uri.parse(pref.hiddenUri.toString() + File.separator + file.name)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
                startActivityForResult(takePictureIntent, 100)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (temp == 1) {
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            var filevideo: File? = null
            try {
                filevideo = createImageFile(1)
                mUri_video = FileProvider.getUriForFile(
                    application.applicationContext, "$packageName.provider",
                    filevideo!!
                )
                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    mUri_video = Uri.parse(
                        pref.hiddenUri.toString() + File.separator + filevideo.name
                    )
                }
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri_video)
                startActivityForResult(takeVideoIntent, 101)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(type: Int): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        if (type == 0) {
            val imageFileName = "Image_$timeStamp"
            storageDir = File(Constant.photos_path)
            if (!storageDir?.parentFile?.exists()!!) storageDir?.parentFile?.mkdirs()
            if (!storageDir?.exists()!!) {
                storageDir?.mkdir()
            }
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
            pref.clickImage = image?.name
        } else if (type == 1) {
            val imageFileName = "Video_$timeStamp"
            storageDir = File(Constant.videos_path)
            if (!storageDir?.parentFile?.exists()!!) storageDir?.parentFile?.mkdirs()
            if (!storageDir?.exists()!!) {
                storageDir?.mkdir()
            }
            image = File.createTempFile(imageFileName, ".mp4", storageDir)
            pref.recordVideo = image?.name
        }
        mCurrentPhotoPath = image!!.absolutePath
        Log.e("TAG", "file is crete")
        Log.e("TAG", "mCurrentPhotoPath $mCurrentPhotoPath")
        return image
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivMenu -> {
                startActivity<SettingsActivity>()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> if (resultCode == RESULT_OK) {
                val intent = Intent(this, PhotosActivity::class.java)
                startActivity(intent)
            } else {
                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    val sourcePath: String = Constant.home_path.toString() + "/" + pref.clickImage
                    val source = File(sourcePath)
                    if (source.exists()) {
                        source.delete()
                    }
                } else {
                    val sourcePath: String =
                        Constant.photos_path.toString() + "/" + pref.clickImage
                    val source = File(sourcePath)
                    if (source.exists()) {
                        source.delete()
                    }
                }
            }
            101 -> if (resultCode == RESULT_OK) {
                val intent = Intent(this, Videos_Activity::class.java)
                startActivity(intent)
            } else {
                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    val sourcePath: String =
                        Constant.home_path.toString() + "/" + pref.clickImage
                    val source = File(sourcePath)
                    if (source.exists()) {
                        source.delete()
                    }
                } else {
                    val sourcePath: String = Constant.videos_path.toString() + "/" + pref.clickImage
                    val source = File(sourcePath)
                    source.delete()
                }
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt("activeIndex", binding.navView.getSelectedIndex())
//        super.onSaveInstanceState(outState)
//    }
}