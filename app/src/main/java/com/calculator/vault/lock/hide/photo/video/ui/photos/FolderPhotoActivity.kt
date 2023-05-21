package com.calculator.vault.lock.hide.photo.video.ui.photos

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
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.App.Companion.getInstance
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper
import com.calculator.vault.lock.hide.photo.video.common.utils.EventBus.publish
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils
import com.calculator.vault.lock.hide.photo.video.ui.video.SpinnerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
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

class FolderPhotoActivity : AppCompatActivity(), View.OnClickListener {
    private var navigationSpinner: Spinner? = null
    private var photo_toolbar: Toolbar? = null
    var media_datas = ArrayList<Media_Data>()
    var folder_media_datas = ArrayList<Media_Data>()
    var folder = ArrayList<String?>()
    private var local_photo_recycler: RecyclerView? = null
    private var photos_adapter: PhotosFolderAdapter? = null
    private var database: Database? = null
    private var no_photo_folder: LinearLayout? = null
    private var progress: ProgressBar? = null
    var mBG: View? = null
    var mCount: TextView? = null
    var CREATE_HIDDEN_FOLDER = 123
    var ACCESS_HIDDEN_FOLDER = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_photo)
        initView()
        initListener()
    }

    private fun initListener() {
        navigationSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    folder_media_datas.clear()
                    for (i in media_datas.indices) {
                        if (media_datas[i].folder != null) {
                            if (media_datas[i].folder == folder[position]) {
                                folder_media_datas.add(media_datas[i])
                            }
                        }
                    }
                    initAdapter(folder_media_datas)
                } else {
                    initAdapter(media_datas)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        photo_hide?.setOnClickListener(this)
    }

    private fun initView() {
        database = Database(this@FolderPhotoActivity)
        photo_toolbar = findViewById(R.id.photo_toolbar)
        navigationSpinner = photo_toolbar?.findViewById(R.id.navigationSpinner)
        local_photo_recycler = findViewById(R.id.local_photo_recycler)
        no_photo_folder = findViewById(R.id.no_photo_folder)
        progress = findViewById(R.id.progress)
        mBG = findViewById(R.id.bg_l)
        mCount = findViewById(R.id.count)
        photo_hide = findViewById(R.id.photo_hide)
        setSupportActionBar(photo_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        progress?.visibility = View.VISIBLE
        Thread { images }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (progress!!.visibility != View.VISIBLE) {
            when (item.itemId) {
                android.R.id.home -> onBackPressed()
                R.id.select -> {
                    if (!IsSelectAll) {
                        IsSelectAll = true
                        item.icon = resources.getDrawable(R.drawable.iv_select)
                        if (navigationSpinner!!.selectedItemPosition == 0) {
                            select_photo_datas.clear()
                            select_photo_datas.addAll(media_datas)
                        } else {
                            select_photo_datas.clear()
                            select_photo_datas.addAll(folder_media_datas)
                        }
                    } else {
                        IsSelectAll = false
                        item.icon = resources.getDrawable(R.drawable.iv_unselect)
                        select_photo_datas.clear()
                        selecter_fphoto!!.isVisible = false
                    }
                    photo_hide!!.text = "" + select_photo_datas.size + " Items Selected"
                    photos_adapter!!.notifyDataSetChanged()
                }
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_select, menu)
        selecter_fphoto = menu.findItem(R.id.select)
        selecter_fphoto?.isVisible = false
        return true
    }

    val images: Unit
        get() {
            media_datas.clear()
            folder.clear()
            folder.add("Photo")
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED
            )
            val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            )
            if (cursor != null) {
                var file: File? = null
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") val path = cursor.getString(cursor.getColumnIndex(projection[0]))
                    @SuppressLint("Range") var title = cursor.getString(cursor.getColumnIndex(projection[1]))
                    if (title != null) {
                    } else {
                        title = ""
                    }
                    @SuppressLint("Range") val modified_date = cursor.getString(
                        cursor.getColumnIndex(
                            projection[2]
                        )
                    )
                    @SuppressLint("Range") val bucketName = cursor.getString(
                        cursor.getColumnIndex(
                            projection[3]
                        )
                    )
                    val size = cursor.getLong(4)
                    @SuppressLint("Range") val added_date = cursor.getString(
                        cursor.getColumnIndex(
                            projection[5]
                        )
                    )
                    try {
                        file = File(path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (!folder.contains(bucketName)) {
                        if (bucketName != null) {
                            folder.add(bucketName)
                        }
                    }
                    if (file != null && file.exists()) {
                        val media_data = Media_Data()
                        media_data.name = title
                        media_data.path = path
                        media_data.folder = bucketName
                        media_data.length = size.toString()
                        media_data.addeddate = added_date
                        media_data.modifieddate = modified_date
                        media_datas.add(media_data)
                    }
                }
                cursor.close()
                runOnUiThread {
                    progress!!.visibility = View.GONE
                    initSpinner()
                    initAdapter(media_datas)
                }
            }
        }

    private fun initAdapter(data: ArrayList<Media_Data>) {
        photos_adapter = PhotosFolderAdapter(this@FolderPhotoActivity, this)
        local_photo_recycler!!.layoutManager = GridLayoutManager(this@FolderPhotoActivity, 2, RecyclerView.VERTICAL, false)
        local_photo_recycler!!.adapter = photos_adapter
        local_photo_recycler!!.setItemViewCacheSize(data.size)
        local_photo_recycler!!.setHasFixedSize(true)
        local_photo_recycler!!.isDrawingCacheEnabled = true
        local_photo_recycler!!.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    private fun initSpinner() {
        if (media_datas.size > 0) {
            navigationSpinner!!.visibility = View.VISIBLE
            photo_hide!!.visibility = View.VISIBLE
            no_photo_folder!!.visibility = View.GONE
            /*val spinner_adapter = SpinnerAdapter(this@FolderPhotoActivity, folder)
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
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
                    parent: ViewGroup
                ): View {
                    val tv = super.getDropDownView(position, convertView, parent) as TextView
                    tv.setTextColor(Color.BLACK)
                    return tv
                }
            }
            navigationSpinner!!.adapter = spinnerAdapter
        } else {
            navigationSpinner!!.visibility = View.GONE
            photo_hide!!.visibility = View.GONE
            no_photo_folder!!.visibility = View.VISIBLE
        }
    }

    var mUris = ArrayList<Uri?>()
    override fun onClick(v: View) {
        when (v.id) {
            R.id.photo_hide -> if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                hideAboveR()
            } else {
                photo_hide!!.isEnabled = false
                progress!!.visibility = View.VISIBLE
                mBG!!.visibility = View.VISIBLE
                mCount!!.visibility = View.VISIBLE
                navigationSpinner!!.isEnabled = false
                mCount!!.text = "00/" + select_photo_datas.size
                Thread {
                    var i = 0
                    while (i < select_photo_datas.size) {
                        val sourcePath = select_photo_datas[i].path
                        val source = File(sourcePath)
                        val id = database!!.id
                        val destinationPath = Constant.photos_path + "/" + id + "_" + source.name
                        val destination = File(destinationPath)
                        try {
                            moveFile(source, destination)
                            if (getInstance().getPref().syncOn) {
                                googleSync(destination)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val finalI = i
                        runOnUiThread {
                            mCount!!.text =
                                (if (finalI < 10) "0" else "") + finalI + "/" + select_photo_datas.size
                        }
                        i++
                    }
                    runOnUiThread {
                        photo_hide!!.isEnabled = true
                        progress!!.visibility = View.GONE
                        mBG!!.visibility = View.GONE
                        navigationSpinner!!.isEnabled = true
                        mCount!!.visibility = View.GONE
                        Toast.makeText(
                            this@FolderPhotoActivity,
                            "Hide successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        select_photo_datas.clear()
                        finish()
                        publish("finish")
                    }
                }.start()
            }
        }
    }

    private fun googleSync(destination: File) {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this@FolderPhotoActivity)
        val googleAccountCredential = GoogleAccountCredential.usingOAuth2(
            this@FolderPhotoActivity,
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
        if (getInstance().getPref().syncWifi) {
            if (wifi!!.isConnectedOrConnecting) {
                driveServiceHelper.checkImage(destination.name).addOnSuccessListener { aBoolean ->
                    if (!aBoolean!!) {
                        Log.d("Data", "onSuccess: ====> Upload " + destination.name)
                        driveServiceHelper.insertImageFile(
                            destination.name,
                            destination.path,
                            getInstance().getPref().imageFolder
                        )
                    }
                }
            }
        } else {
            driveServiceHelper.checkImage(destination.name).addOnSuccessListener { aBoolean ->
                if (!aBoolean!!) {
                    Log.d("Data", "onSuccess: ====> Upload " + destination.name)
                    driveServiceHelper.insertImageFile(
                        destination.name,
                        destination.path,
                        getInstance().getPref().imageFolder
                    )
                }
            }
        }
    }

    private fun hideAboveR() {
        photo_hide!!.isEnabled = false
        progress!!.visibility = View.VISIBLE
        mBG!!.visibility = View.VISIBLE
        mCount!!.visibility = View.VISIBLE
        navigationSpinner!!.isEnabled = false
        mCount!!.text = "00/" + select_photo_datas.size
        Thread {
            for (i in select_photo_datas.indices) {
                val sourcePath = select_photo_datas[i].path
                val source = File(sourcePath)
                val id = database!!.id
                var destinationPath = Constant.photos_path + "/" + id + "_" + source.name
                var destination = File(destinationPath)
                try {
                    destinationPath =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.toString() + File.separator +
                                ".Calculator Lock"
                    destination = File(destinationPath)
                    moveFileonAboveR(
                        source,
                        File(destination.toString() + File.separator + source.name)
                    )
                    mUris.add(getImageUriFromFile(sourcePath, this))
                    if (getInstance().getPref().syncOn) {
                        googleSync(destination)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                runOnUiThread {
                    mCount?.text =
                        (if (i < 10) "0" else "") + i + "/" + select_photo_datas.size
                }
            }
            requestDeletePermission(mUris)
            runOnUiThread {
                photo_hide?.isEnabled = true
                progress?.visibility = View.GONE
                mBG?.visibility = View.GONE
                navigationSpinner?.isEnabled = true
                mCount?.visibility = View.GONE
                Toast.makeText(this@FolderPhotoActivity, "Hide successfully", Toast.LENGTH_SHORT).show()
                select_photo_datas.clear()
                finish()
                publish("finish")
            }
        }.start()
    }

    fun getImageUriFromFile(path: String, context: Context): Uri? {
        val resolver = context.contentResolver
        val filecursor = resolver.query(
            MediaStore.Images.Media.getContentUri("external"),
            arrayOf(BaseColumns._ID),
            MediaStore.Images.ImageColumns.DATA + " = ?",
            arrayOf(path),
            MediaStore.Images.ImageColumns.DATE_ADDED + " desc"
        )
        filecursor!!.moveToFirst()
        return if (filecursor.isAfterLast) {
            filecursor.close()
            val values = ContentValues()
            values.put(MediaStore.Images.ImageColumns.DATA, path)
            resolver.insert(MediaStore.Images.Media.getContentUri("external"), values)
        } else {
            @SuppressLint("Range") val imageId =
                filecursor.getInt(filecursor.getColumnIndex(BaseColumns._ID))
            val uri = MediaStore.Images.Media.getContentUri("external").buildUpon().appendPath(
                Integer.toString(imageId)
            ).build()
            filecursor.close()
            uri
        }
    }

    private fun requestDeletePermission(uriList: List<Uri?>) {
        for (i in uriList.indices) {
            println("AFTER HIDE : " + uriList[i])
        }
        try {
            var pi: PendingIntent? = null
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pi = MediaStore.createDeleteRequest(contentResolver, uriList)
            }
            startIntentSenderForResult(
                pi!!.intentSender, 1001, null, 0, 0,
                0, null
            )
        } catch (e: Exception) {
            println("AFTER HIDE : " + e.message)
        }
        images
    }

    @Throws(IOException::class)
    private fun moveFile(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        var source_channel: FileChannel? = null
        var destination_channel: FileChannel? = null
        try {
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'")) {
                    contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA + "='" + source.path + "'",
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
            }
            MediaScannerConnection.scanFile(
                this,
                arrayOf(source.path),
                null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
            MediaScannerConnection.scanFile(
                this,
                arrayOf(destination.path),
                null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
        val hide_data = Hide_Data()
        hide_data.name = destination.name
        hide_data.path = source.path
        database!!.addHide(hide_data)
    }

    @Throws(IOException::class)
    private fun moveFileonAboveR(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        val source_channel: FileChannel? = null
        val destination_channel: FileChannel? = null
        try {
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'")) {
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    val contentResolver = contentResolver
                    val rootUri = Uri.parse(getInstance().getPref().hiddenUri)
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
                    val isFileCreated = createFile.createNewFile(false, true)
                }
                source.delete()
            }
            MediaScannerConnection.scanFile(
                this,
                arrayOf(source.path),
                null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
            MediaScannerConnection.scanFile(
                this,
                arrayOf(destination.path),
                null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
        val hide_data = Hide_Data()
        hide_data.name = destination.name
        hide_data.path = source.path
        database!!.addHide(hide_data)
    }

    override fun onDestroy() {
        super.onDestroy()
        IsSelectAll = false
    }

    override fun onBackPressed() {
        if (progress!!.visibility != View.VISIBLE) {
            if (select_photo_datas.size != 0) {
                IsSelectAll = false
                selecter_fphoto!!.icon = resources.getDrawable(R.drawable.iv_unselect)
                select_photo_datas.clear()
                selecter_fphoto!!.isVisible = false
                photo_hide!!.text = "Hide (" + select_photo_datas.size + ")"
                photos_adapter!!.notifyDataSetChanged()
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        @JvmField
        var select_photo_datas = ArrayList<Media_Data>()

        @JvmField
        var photo_hide: Button? = null

        @JvmField
        var IsSelectAll = false

        @JvmField
        var selecter_fphoto: MenuItem? = null
    }
}