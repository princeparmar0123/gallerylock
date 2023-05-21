package com.calculator.vault.lock.hide.photo.video.ui.video

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.utils.*
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityFolderVideoBinding
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.ACCESS_HIDDEN_FOLDER
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.CREATE_HIDDEN_FOLDER
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnSuccessListener
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel


class VideoFolderActivity :
    BaseActivity<ActivityFolderVideoBinding>(R.layout.activity_folder_video), View.OnClickListener {

    private var adapter: VideoFolderAdapter? = null
    var media_datas = ArrayList<Media_Data>()
    var mUris = ArrayList<Uri>()
    private var database: Database? = null

    var folder = ArrayList<String?>()
    var isDialogAllow = false
    var resumeCount = 0
    var mSource: File? = null
    var msourcePath: String? = null

    companion object {
        var folder_media_datas = ArrayList<Media_Data>()
        var select_video_datas = ArrayList<Media_Data>()
        var selecter_fvideo: MenuItem? = null
        var IsSelectAll = false
        var video_hide: Button? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.videoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.progress.visibility = View.VISIBLE
        database = Database(this);
        initListener()
        clickListener()
        Thread { getVideo() }.start()
        video_hide = binding.videoHide
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (binding.progress.visibility != View.VISIBLE) {
            when (item.itemId) {
                android.R.id.home -> onBackPressed()
                R.id.select -> {
                    if (!IsSelectAll) {
                        IsSelectAll = true
                        item.icon = resources.getDrawable(R.drawable.iv_select)
                        if (binding.navigationSpinner.selectedItemPosition == 0) {
                            select_video_datas.clear()
                            select_video_datas.addAll(media_datas)
                        } else {
                            select_video_datas.clear()
                            select_video_datas.addAll(folder_media_datas)
                        }
                    } else {
                        IsSelectAll = false
                        item.icon = resources.getDrawable(R.drawable.iv_unselect)
                        select_video_datas.clear()
                        selecter_fvideo?.isVisible = false
                    }
                    binding.videoHide.text = "Hide (" + select_video_datas.size + ")"
                    adapter?.notifyDataSetChanged()
                }
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_select, menu)
        selecter_fvideo = menu.findItem(R.id.select)
        selecter_fvideo?.isVisible = false
        return true
    }

    private fun initListener() {
        binding.navigationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    if (position != 0) {
                        folder_media_datas.clear()
                        for (i in media_datas.indices) {
                            if (media_datas.get(i).folder != null) {
                                if (media_datas.get(i).folder.equals(folder.get(position))) {
                                    folder_media_datas.add(media_datas.get(i))
                                }
                            }
                        }

                        initAdapter(folder_media_datas)
                        //adapter?.addAll(folder_media_datas)
                    } else {

                        initAdapter(media_datas)
                        // adapter?.addAll(media_datas)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        video_hide?.setOnClickListener(this@VideoFolderActivity)
    }


    @SuppressLint("Range")
    fun getVideo() {
        media_datas.clear()
        folder.clear()
        folder.add("All Videos")
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC"
        )
        if (cursor != null) {
//            var file: File? = null
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(projection[0]))
                var title = cursor.getString(cursor.getColumnIndex(projection[1]))
                if (title != null) {
                } else {
                    title = ""
                }
                val modified_date = cursor.getString(cursor.getColumnIndex(projection[2]))
                val bucketName = cursor.getString(cursor.getColumnIndex(projection[3]))
                val size = cursor.getLong(4)
                val added_date = cursor.getString(cursor.getColumnIndex(projection[5]))
//                try {
//                    file = File(path)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
                if (!folder.contains(bucketName)) {
                    if (bucketName != null) {
                        folder.add(bucketName)
                    }
                }
//                if (file != null && file.exists()) {
                val media_data = Media_Data()
                media_data.name = title
                media_data.path = path
                media_data.folder = bucketName
                media_data.length = size.toString()
                media_data.addeddate = added_date
                media_data.modifieddate = modified_date
                media_datas.add(media_data)
                media_datas.reverse()
                //media_datas.asReversed()
//                }
            }
            cursor.close()
            runOnUiThread {
                binding.progress.visibility = View.GONE
                initSpinner()
                initAdapter(media_datas)
                // adapter?.addAll(media_datas)
            }
        }
    }

    private fun initSpinner() {
        if (media_datas.size > 0) {
            binding.noVideoFolder.visibility = View.GONE
            binding.navigationSpinner.visibility = View.VISIBLE
            binding.videoHide.isVisible = true
            /* val spinnerAdapter =
                 SpinnerAdapter(
                     this,
                     folder
                 )*/
            val spinnerAdapter = object : ArrayAdapter<String?>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                folder
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val tv = super.getView(position, convertView, parent) as TextView
                    tv.setTextColor(Color.WHITE)
                    return tv
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup,
                ): View {
                    val tv = super.getDropDownView(position, convertView, parent) as TextView
                    tv.setTextColor(Color.BLACK)
                    return tv
                }
            }
            binding.navigationSpinner.adapter = spinnerAdapter
        } else {
            binding.navigationSpinner.visibility = View.GONE
            binding.videoHide.isVisible = false
            binding.noVideoFolder.visibility = View.VISIBLE
        }
    }

    private fun initAdapter(data: ArrayList<Media_Data>) {
        adapter = VideoFolderAdapter(this)
        binding.localVideoRecycler.adapter = adapter
        binding.localVideoRecycler.run {
            layoutManager =
                GridLayoutManager(this@VideoFolderActivity, 2, RecyclerView.VERTICAL, false)
            setItemViewCacheSize(data.size)
            setHasFixedSize(true)
        }

        adapter?.addAll(data)
        adapter?.setItemClickListener { view, _, _ ->
            when (view.id) {
                R.id.imaegs -> {

                }
            }
        }
    }

    private fun clickListener() {
        binding.videoHide.setOnClickListener(this)
    }

    @Throws(IOException::class)
    private fun moveFileonAboveR(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        val source_channel: FileChannel? = null
        val destination_channel: FileChannel? = null
        try {
            val hide_data = Hide_Data()
            hide_data.name = destination.name
            hide_data.path = source.path
            database!!.addHide(hide_data)
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'")) {
//                    getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    val contentResolver = contentResolver
                    val rootUri = Uri.parse(pref.hiddenUri)
                    contentResolver.takePersistableUriPermission(
                        rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
                    val createFile = CreateFile(
                        this,
                        contentResolver,
                        File.separator + source.name,
                        rootUri,
                        rootDocumentId,
                        true,
                        source.absolutePath,
                        true,
                        Utils.getMimeType(source.name)
                    )
                    val isFileCreated: Boolean = createFile.createNewFile(false, true)
                }
                source.delete()
                MediaScannerConnection.scanFile(
                    this, arrayOf(source.path), null
                ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
                MediaScannerConnection.scanFile(
                    this, arrayOf(destination.path), null
                ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
            }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
    }

    private fun hideAboveR() {
        video_hide?.isEnabled = false
        binding.progress.visibility = View.VISIBLE
        binding.bgL.visibility = View.VISIBLE
        binding.count.visibility = View.VISIBLE
        binding.navigationSpinner.isEnabled = false
        binding.count.text = "00/" + select_video_datas.size
        Thread {
            for (i in select_video_datas.indices) {
                val sourcePath: String = select_video_datas[i].path
                val source = File(sourcePath)
                val id = database!!.id
                var destinationPath =
                    Constant.videos_path.toString() + "/" + id + "_" + source.name
                var destination = File(destinationPath)
                try {
                    destinationPath = Constant.videos_path
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                            .path.toString() + File.separator +
//                                ".Calculator Lock"
                    destination = File(destinationPath)
                    moveFileonAboveR(
                        source,
                        File(destination.toString() + File.separator + source.name)
                    )
                    getVideoUriFromFile(sourcePath, this)?.let { mUris.add(it) }
                    if (pref.syncOn) {
                        googleSync(destination)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val finalI: Int = i
                runOnUiThread {
                    binding.count.text =
                        (if (finalI < 10) "0" else "") + finalI + "/" + select_video_datas.size
                }
            }
            requestDeletePermission(mUris)
            runOnUiThread {
                video_hide?.isEnabled = true
                binding.progress.visibility = View.GONE
                binding.bgL.visibility = View.GONE
                binding.navigationSpinner.isEnabled = true
                binding.count.visibility = View.GONE
                Toast.makeText(this, "Hide successfully", Toast.LENGTH_SHORT)
                    .show()
                select_video_datas.clear()
                finish()
            }
        }.start()
    }

    fun getVideoUriFromFile(path: String, context: Context): Uri? {
        val resolver = context.contentResolver
        val filecursor = resolver.query(
            MediaStore.Video.Media.getContentUri("external"),
            arrayOf(BaseColumns._ID),
            MediaStore.Video.VideoColumns.DATA + " = ?",
            arrayOf(path),
            MediaStore.Video.VideoColumns.DATE_ADDED + " desc"
        )
        filecursor?.moveToFirst()
        return if (filecursor?.isAfterLast == true) {
            filecursor.close()
            val values = ContentValues()
            values.put(MediaStore.Video.VideoColumns.DATA, path)
            resolver.insert(MediaStore.Video.Media.getContentUri("external"), values)
        } else {
            val videoId = filecursor!!.getInt(filecursor.getColumnIndex(BaseColumns._ID))
            val uri = MediaStore.Video.Media.getContentUri("external").buildUpon().appendPath(
                videoId.toString()
            ).build()
            filecursor.close()
            uri
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video_hide -> {
                binding.run {
//                 if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                     hideAboveR()
//                 } else {
                    progress.visibility = View.VISIBLE
                    bgL.visibility = View.VISIBLE
                    count.visibility = View.VISIBLE
                    navigationSpinner.isEnabled = false
                    count.text = "00/" + select_video_datas.size
                    mUris.clear()
                    Thread {
                        for (i in select_video_datas.indices) {
                            val sourcePath: String = select_video_datas.get(i).path
                            val source = File(sourcePath)
                            val id: Int = database?.getID() ?: 0
                            val destinationPath: String =
                                Constant.videos_path.toString() + "/" + id + "_" + source.name
                            val destination = File(destinationPath)
                            try {
                                moveFile(source, destination)
                                if (pref.syncOn) {
                                    googleSync(destination)
                                }
                                getVideoUriFromFile(sourcePath, this@VideoFolderActivity)?.let { mUris.add(it) }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val finalI: Int = i
                            runOnUiThread { binding.count.text = (if (finalI < 10) "0" else "") + finalI + "/" + select_video_datas.size }
                        }

                        requestDeletePermission(mUris)
                        runOnUiThread {
                            Toast.makeText(this@VideoFolderActivity,
                                "Hide successfully",
                                Toast.LENGTH_SHORT).show()
                            select_video_datas.clear()
                            finish()
                        }
                    }.start()

                }
//             }
            }
        }
    }


    override fun onPostResume() {
        Log.e("ONNPAUSEEE", "POST RESUME")
        if (isDialogAllow) {
            if (resumeCount == 1) {
                resumeCount = 0
                isDialogAllow = false
                Constants.isSplashScreen = false
            }
            resumeCount += 1
        }
        super.onPostResume()
    }


    private fun requestDeletePermission(uriList: List<Uri>) {
        isDialogAllow = true
        resumeCount = 0
        for (i in uriList.indices) {
            println("AFTER HIDE : " + uriList[i])
        }
        try {
            var pi: PendingIntent? = null
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pi = MediaStore.createDeleteRequest(contentResolver, uriList)
            }
            Log.e("TAG123", "onActivityResult: 0")
            startIntentSenderForResult(
                pi!!.intentSender, 1001, null, 0, 0,
                0, null
            )
        } catch (e: java.lang.Exception) {
            println("AFTER HIDE : " + e.message)
        }
        getVideo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG123", "onActivityResult: wfqwfq")
        if (requestCode == 1001) {
            Handler().postDelayed({ Constants.isSplashScreen = false }, 500)
        } else if (requestCode == CREATE_HIDDEN_FOLDER) {
            Log.e("TAG123", "onActivityResult: 2")
        } else if (requestCode == ACCESS_HIDDEN_FOLDER) {
            pref.hiddenPermission = true
            val rootUri1 = data!!.data
            pref.hiddenUri = rootUri1.toString()
            val destinationPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.toString() + File.separator +
                        ".Calculator Lock" + File.separator +
                        ".hideMedia"
            val destination = File(destinationPath)
            try {
                mSource?.let {
                    moveFileonAboveR(it,
                        File(destination.toString() + File.separator + mSource?.name))
                }
                mUris.add(msourcePath?.let { getVideoUriFromFile(it, this@VideoFolderActivity) }!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun moveFile(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        var source_channel: FileChannel? = null
        var destination_channel: FileChannel? = null
        try {
            val hide_data = Hide_Data()
            hide_data.name = destination.name
            hide_data.path = source.path
            database!!.addHide(hide_data)
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'")) {
                    contentResolver.delete(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Video.Media.DATA + "='" + source.path + "'",
                        null
                    )
                }
            } else {
                if (!destination.exists()) {
                    destination.createNewFile()
                }
                source_channel = FileInputStream(source).channel
                destination_channel = FileOutputStream(destination).channel
                destination_channel.transferFrom(source_channel, 0, source_channel.size())
                source_channel.close()
                source.delete()
                MediaScannerConnection.scanFile(
                    this, arrayOf(source.path), null
                ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
                MediaScannerConnection.scanFile(
                    this, arrayOf(destination.path), null
                ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
            }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
    }

    private fun googleSync(destination: File) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val googleAccountCredential = GoogleAccountCredential.usingOAuth2(
            this,
            setOf(DriveScopes.DRIVE_FILE)
        )
        googleAccountCredential.selectedAccount = googleSignInAccount!!.account
        val drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            googleAccountCredential
        )
            .setApplicationName(resources.getString(R.string.app_name))
            .build()
        val driveServiceHelper = DriveServiceHelper(drive)
        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (pref.syncWifi) {
            if (wifi!!.isConnectedOrConnecting) {
                driveServiceHelper.checkImage(destination.name)
                    .addOnSuccessListener(OnSuccessListener<Boolean?> { aBoolean ->
                        if (!aBoolean!!) {
                            Log.d("Data", "onSuccess: ====> Upload " + destination.name)
                            driveServiceHelper.insertImageFile(
                                destination.name,
                                destination.path,
                                pref.imageFolder
                            )
                        }
                    })
            }
        } else {
            driveServiceHelper.checkImage(destination.name)
                .addOnSuccessListener(OnSuccessListener<Boolean?> { aBoolean ->
                    if (!aBoolean!!) {
                        Log.d("Data", "onSuccess: ====> Upload " + destination.name)
                        driveServiceHelper.insertImageFile(
                            destination.name,
                            destination.path,
                            pref.imageFolder
                        )
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        IsSelectAll = false
    }

    override fun onBackPressed() {
        if (binding.progress.visibility != View.VISIBLE) {
            if (select_video_datas.size != 0) {
                IsSelectAll = false
                selecter_fvideo?.icon = resources.getDrawable(R.drawable.iv_unselect)
                select_video_datas.clear()
                selecter_fvideo?.isVisible = false
                video_hide?.text = "Hide (" + select_video_datas.size + ")"
                adapter?.notifyDataSetChanged()
            } else {
                super.onBackPressed()
            }
        }
    }

}