//package com.calculator.vault.lock.hide.photo.video.ui.video
//
//import android.app.PendingIntent
//import android.content.ContentValues
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.graphics.Color
//import android.media.MediaMetadataRetriever
//import android.media.MediaScannerConnection
//import android.net.ConnectivityManager
//import android.net.Uri
//import android.os.Build
//import android.os.Build.VERSION
//import android.os.Bundle
//import android.os.Environment
//import android.os.storage.StorageManager
//import android.provider.BaseColumns
//import android.provider.DocumentsContract
//import android.provider.MediaStore
//import android.text.TextUtils
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.documentfile.provider.DocumentFile
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.calculator.vault.lock.hide.photo.video.R
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.google.api.client.extensions.android.http.AndroidHttp
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
//import com.google.api.client.json.gson.GsonFactory
//import com.google.api.services.drive.Drive
//import com.google.api.services.drive.DriveScopes
//import com.calculator.vault.lock.hide.photo.video.common.adsData.AdmobAdManager
//import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
//import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
//import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data
//import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data
//import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
//import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
//import com.calculator.vault.lock.hide.photo.video.common.utils.*
//import com.calculator.vault.lock.hide.photo.video.databinding.ActivityVideosBinding
//import com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton
//import timber.log.Timber
//import java.io.*
//import java.nio.channels.FileChannel
//import java.text.ParseException
//import java.text.SimpleDateFormat
//import java.util.*
//
//class VideosActivity : BaseActivity<ActivityVideosBinding>(R.layout.activity_videos),
//    View.OnClickListener {
//
//   // var floating_btn_video: FloatingActionsMenu? = null
//    private var fab_gallery: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? = null
//    private var fab_folder: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? = null
//    var hideVideoData: ArrayList<Media_Data> = ArrayList()
//    private var adapter: VideoAdapter? = null
//    private var database: Database? = null
//    var temp = false
//    var isDialogAllow = false
//    var resumeCount = 0
//    private val retriever: MediaMetadataRetriever? = null
//    //private val progress: ProgressBar? = null
//
//    var CREATE_HIDDEN_FOLDER = 123
//    var ACCESS_HIDDEN_FOLDER = 12
//    var ACCESS_RECYCLE_FOLDER = 121
//    var mUris = ArrayList<Uri>()
//
//    var recycle_source: File? = null
//    var recycle_destination: File? = null
//    var recycle_name: String? = null
//
//    companion object {
//        var floating_btn_video: LinearLayout? = null
//        //var hide_video_data = ArrayList<Media_Data>()
//        var selected_hide_video_data = ArrayList<Media_Data>()
//        var IsSelectAll = false
//        var video_btn_lay: LinearLayout? = null
//        var selecter: MenuItem? = null
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding.toolbar.setTitleTextColor(Color.WHITE)
//        binding.toolbar.title = "Videos"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        setSupportActionBar(binding.toolbar)
//        database = Database(this)
//        video_btn_lay = binding.videoBtnLay
//        //fab_gallery = binding.fabGallery
//       // fab_folder = binding.fabFolder
//        floating_btn_video = binding.floatingBtnVideo
//
//        getVideos()
//        initAdapter()
//        setPermissionButton()
//        onClickListener()
//
//
//        floating_btn_video?.setOnClickListener {
//            bottomSheetDialog()
//        }
//
////        floating_btn_video?.setOnClickListener {
////            bottomSheetDialog()
////        }
//
//    }
//
//
//    private fun setPermissionButton() {
//        binding.run{
//        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            val destinationPath =
//                (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
//                        + File.separator +
//                        ".Calculator Lock")
//            val destination = File(destinationPath)
//            if (!destination.exists()) {
//                permissionBtn.setText(R.string.videos_folder_permission_btn)
//                permissionBtn.visibility = View.VISIBLE
//                permissionText.visibility = View.VISIBLE
//                floating_btn_video?.visibility = View.GONE
//            } else if (!pref.hiddenPermission) {
//                permissionBtn.setText(R.string.hidden_folder_permission_btn)
//                permissionBtn.visibility = View.VISIBLE
//                permissionText.visibility = View.VISIBLE
//                floating_btn_video?.visibility = View.GONE
//            } else {
//                permissionBtn.visibility = View.GONE
//                permissionText.visibility = View.GONE
//                floating_btn_video?.visibility = View.VISIBLE
//            }
//        }
//        }
//    }
//
//
//    private fun onClickListener() {
//        binding.run {
//            //fabFolder.setOnClickListener(this@VideosActivity)
//           // fabGallery.setOnClickListener(this@VideosActivity)
//            videoUnhide.setOnClickListener(this@VideosActivity)
//            videoDelete.setOnClickListener(this@VideosActivity)
//            permissionBtn.setOnClickListener(this@VideosActivity)
//        }
//    }
//
//    private fun initAdapter() {
//
//        if (hideVideoData.size > 0) {
//            binding.videoRecycler.visibility = View.VISIBLE
//            binding.noVideo.visibility = View.GONE
//            adapter = VideoAdapter(this)
//            binding.videoRecycler.layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
//            binding.videoRecycler.adapter = adapter
//            adapter?.addAll(hideVideoData)
//        } else {
//            binding.videoRecycler.visibility = View.GONE
//            binding.noVideo.visibility = View.VISIBLE
//        }
//
//        adapter?.setItemClickListener { view, i, mediaData ->
//
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (binding.progress.visibility != View.VISIBLE) {
//            when (item.itemId) {
//                android.R.id.home -> onBackPressed()
//                R.id.select -> if (adapter != null) {
//                    if (!IsSelectAll) {
//                        IsSelectAll = true
//                        adapter?.IsLongClick = true
//                        item.icon = resources.getDrawable(R.drawable.iv_select)
//                        selected_hide_video_data.clear()
//                        selected_hide_video_data.addAll(hideVideoData)
//                        floating_btn_video?.visibility = View.GONE
//                        video_btn_lay?.visibility = View.VISIBLE
//                            binding.toolbar.title = "Photos(" + selected_hide_video_data.size + ")"
//                    } else {
//                        IsSelectAll = false
//                        adapter?.IsLongClick = false
//                        floating_btn_video?.visibility = View.VISIBLE
//                        video_btn_lay?.visibility = View.GONE
//                        item.icon = resources.getDrawable(R.drawable.iv_unselect)
//                        selected_hide_video_data.clear()
//                        selecter?.isVisible = false
//                        binding.toolbar.title = "Videos"
//                    }
//                    adapter?.notifyDataSetChanged()
//                }
//                R.id.sort_adate -> {
//                    hideVideoData.sortWith { file1, file2 ->
//                        val format = SimpleDateFormat(
//                            "EEE MMM dd HH:mm:ss zzz yyyy",
//                            Locale.US
//                        )
//                        var k: Long = 0
//                        try {
//                            val date1 = format.parse(file1.addeddate)
//                            val date2 = format.parse(file2.addeddate)
//                            if (date1 != null && date2 != null) {
//                                k = date1.time - date2.time
//                            }
//                        } catch (e: ParseException) {
//                            e.printStackTrace()
//                        }
//                        if (k > 0) {
//                            1
//                        } else if (k == 0L) {
//                            0
//                        } else {
//                            -1
//                        }
//                    }
//                    hideVideoData.reverse()
//                    initAdapter()
//                }
//                R.id.sort_size -> {
//                    hideVideoData.sortWith { file1, file2 ->
//                        val k: Long = file1.length.toLong() - file2.length.toLong()
//                        if (k > 0) {
//                            1
//                        } else if (k == 0L) {
//                            0
//                        } else {
//                            -1
//                        }
//                    }
//                    hideVideoData.reverse()
//                    initAdapter()
//                }
//                R.id.sort_name -> {
//                    hideVideoData.sortWith { file1, file2 ->
//                        file1.name.compareTo(file2.name, true)
//                    }
//
//                    hideVideoData.reverse()
//                    initAdapter()
//                }
//            }
//        }
//        return false
//    }
//
//    @Throws(IOException::class)
//    private fun deleteFileOnAboveR(source: File, destination: File, filePath: String) {
//        if (!destination.parentFile.exists()) {
//            destination.parentFile.mkdirs()
//        }
//        val source_channel: FileChannel? = null
//        val destination_channel: FileChannel? = null
//        try {
//            val isRename = source.renameTo(destination)
//            if (isRename) {
//                if (!source.path.contains("'") && source.exists()) {
////                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
//                }
//            } else {
//                if (!destination.exists()) {
//                    val contentResolver = contentResolver
//                    val rootUri = Uri.parse(pref.hiddenUri)
//                    contentResolver.takePersistableUriPermission(
//                        rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    )
//                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
//                    val createFile = CreateFile(
//                        this,
//                        contentResolver,
//                        File.separator + source.name,
//                        rootUri,
//                        rootDocumentId,
//                        true,
//                        source.absolutePath,
//                        true,
//                        Utils.getMimeType(source.name)
//                    )
//                    val isFileCreated = createFile.createNewFile(false, true)
//                }
//                source.delete()
//            }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(source.path), null
//            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(destination.path), null
//            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
//        } finally {
//            source_channel?.close()
//            destination_channel?.close()
//        }
//    }
//
//
//    fun getImageUriFromFile(path: String, context: Context): Uri? {
//        val resolver = context.contentResolver
//        val filecursor = resolver.query(
//            MediaStore.Video.Media.getContentUri("external"),
//            arrayOf(BaseColumns._ID),
//            MediaStore.Video.VideoColumns.DATA + " = ?",
//            arrayOf(path),
//            MediaStore.Video.VideoColumns.DATE_ADDED + " desc"
//        )
//        filecursor!!.moveToFirst()
//        return if (filecursor.isAfterLast) {
//            filecursor.close()
//            val values = ContentValues()
//            values.put(MediaStore.Video.VideoColumns.DATA, path)
//            resolver.insert(MediaStore.Video.Media.getContentUri("external"), values)
//        } else {
//            val videoId = filecursor.getInt(filecursor.getColumnIndex(BaseColumns._ID))
//            val uri = MediaStore.Video.Media.getContentUri("external").buildUpon().appendPath(
//                Integer.toString(videoId)
//            ).build()
//            filecursor.close()
//            uri
//        }
//    }
//
//    private fun requestDeletePermission(uriList: List<Uri>) {
//        isDialogAllow = true
//        resumeCount = 0
//        for (i in uriList.indices) {
//            println("AFTER HIDE : " + uriList[i])
//        }
//        try {
//            var pi: PendingIntent? = null
//            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                pi = MediaStore.createDeleteRequest(contentResolver, uriList)
//            }
//            startIntentSenderForResult(
//                pi!!.intentSender, 1001, null, 0, 0,
//                0, null
//            )
//        } catch (e: java.lang.Exception) {
//            Log.e("TAG123", "requestDeletePermission: " + e.message)
//            println("AFTER HIDE : " + e.message)
//        }
//        getVideos()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                101 -> if (data != null) {
//                    val filePath: String = ImageFilePath.getPath(this@VideosActivity, data.data)
//                    val source = File(filePath)
//                    val id = database!!.id
//                    var destinationPath =
//                        Constant.videos_path.toString() + "/" + id + "_" + source.name
//                    var destination = File(destinationPath)
//                    try {
//                        val hide_data = Hide_Data()
//                        hide_data.name = destination.name
//                        hide_data.path = source.path
//                        database!!.addHide(hide_data)
//                        if (source != null && destination != null) {
//                            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                                destinationPath = (Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_PICTURES
//                                ).path
//                                        + File.separator +
//                                        ".Calculator Lock")
//                                destination = File(destinationPath)
//                                deleteFileOnAboveR(
//                                    source,
//                                    File(destination.toString() + File.separator + source.name),
//                                    filePath
//                                )
//                                getImageUriFromFile(filePath, this)?.let { mUris.add(it) }
//                                requestDeletePermission(mUris)
//                                mUris.clear()
//                            } else {
//                                deleteFile(source, destination)
//                            }
//                        }
//
//                        if (pref.syncOn) {
//                            val googleSignInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this@VideosActivity)
//                            val googleAccountCredential: GoogleAccountCredential =
//                                GoogleAccountCredential.usingOAuth2(
//                                    this@VideosActivity,
//                                    setOf(DriveScopes.DRIVE_FILE)
//                                )
//                            googleAccountCredential.selectedAccount = googleSignInAccount?.account
//                            val drive: Drive = Drive.Builder(
//                                AndroidHttp.newCompatibleTransport(),
//                                GsonFactory(),
//                                googleAccountCredential
//                            )
//                                .setApplicationName(resources.getString(R.string.app_name))
//                                .build()
//                            val driveServiceHelper = DriveServiceHelper(drive)
//                            val connMgr =
//                                getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//                            val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//                            if (pref.syncWifi) {
//                                if (wifi?.isConnectedOrConnecting == true) {
//                                    val finalDestination1 = destination
//                                    driveServiceHelper.checkVideo(destination.name)
//                                        .addOnSuccessListener { aBoolean ->
//                                            if (!aBoolean) {
//                                                Log.d(
//                                                    "Data",
//                                                    "onSuccess: ====> Upload " + finalDestination1.name
//                                                )
//                                                driveServiceHelper.insertVideoFile(
//                                                    finalDestination1.name,
//                                                    finalDestination1.path,
//                                                    pref.videoFolder
//                                                )
//                                            }
//                                        }
//                                }
//                            } else {
//                                val finalDestination = destination
//                                driveServiceHelper.checkVideo(destination.name)
//                                    .addOnSuccessListener { aBoolean ->
//                                        if (!aBoolean) {
//                                            Log.d(
//                                                "Data",
//                                                "onSuccess: ====> Upload " + finalDestination.name
//                                            )
//                                            driveServiceHelper.insertVideoFile(
//                                                finalDestination.name,
//                                                finalDestination.path,
//                                                pref.videoFolder
//                                            )
//                                        }
//                                    }
//                            }
//                        }
//                        Toast.makeText(this, "successfully Added", Toast.LENGTH_SHORT).show()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//                123 -> {
//                    val rootUri = data!!.data
//                    val contentResolver = this.contentResolver
//                    contentResolver.takePersistableUriPermission(
//                        rootUri!!,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    )
//                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
//                    val path = (File.separator + ".Calculator Lock"
//                            + File.separator + ".nomedia")
//                    val destinationPath =
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path +
//                                File.separator +
//                                ".Calculator Lock"
//                    val destination = File(destinationPath)
//                    val filePath: String = ImageFilePath.getPath(this, data.data)
//                    val source = File(filePath)
//                    val createFile = CreateFile(
//                        applicationContext, contentResolver, path,
//                        rootUri, rootDocumentId, false, "", false, "*/*"
//                    )
//                    val isFileCreated = createFile.createNewFile(true, false)
//                        binding.permissionBtn.setText(R.string.hidden_folder_permission_btn)
//                }
//                12 -> {
//                    val rootUri1 = data!!.data
//                    val contentResolver1 = this.contentResolver
//                    contentResolver1.takePersistableUriPermission(
//                        rootUri1!!,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    )
//                    val rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1)
//                    pref.hiddenUri = rootUri1.toString()
//                    pref.hiddenPermission = true
//                    binding.permissionBtn.visibility = View.GONE
//                    binding.permissionText.visibility = View.GONE
//                    floating_btn_video?.visibility = View.VISIBLE
//                }
//                121 -> {
//                    val rootUri2 = data!!.data
//                    pref.recycleUri = rootUri2.toString()
//                    pref.isRecyclePermission = true
//                    try {
//                        recycle_source?.let {
//                            recycle_name?.let { it1 ->
//                                moveToRecycleAboveQ(
//                                    it,
//                                    File(recycle_destination.toString() + File.separator + recycle_name),
//                                    Uri.parse(recycle_name),
//                                    it1
//                                )
//                            }
//                        }
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onPostResume() {
//        Log.e("ONNPAUSEEE", "POST RESUME")
//        if (isDialogAllow) {
//            if (resumeCount == 1) {
//                resumeCount = 0
//                isDialogAllow = false
//                Constants.isSplashScreen = false
//            }
//            resumeCount += 1
//        }
//        super.onPostResume()
//    }
//
//    override fun onResume() {
//        Log.e("ONNPAUSEEE", "RESUMME")
//        getVideos()
//        super.onResume()
//    }
//
//    private fun getVideos() {
//        hideVideoData.clear()
//        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            if (!TextUtils.isEmpty(pref.hiddenUri)) {
//                val treeUri = Uri.parse(pref.hiddenUri)
//                if (treeUri != null) {
//                    contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    val tree = DocumentFile.fromTreeUri(this, treeUri)
//                    if (tree != null && tree.isDirectory) {
//                        for (file in tree.listFiles()) {
//                            val media_data = Media_Data()
//                            media_data.name = file.name
//                            media_data.path = file.uri.toString()
//                            media_data.length = file.length().toString()
//                            val mFile = File(file.uri.path)
//                            media_data.length = mFile.length().toString()
//                            Log.d("TAG1234", "getVideos: $mFile")
//                            val lastModDate = Date(mFile.lastModified())
//                            media_data.modifieddate = lastModDate.toString()
//                            media_data.addeddate = lastModDate.toString()
//                            if (Utils.isVideo(getFileType(file.name))) {
//                                hideVideoData.add(media_data)
//                            }
//                        }
//                    }
//                    hideVideoData.reverse()
//                    Timber.d("hideVideoData${hideVideoData.size}")
//                   // initAdapter()
//                }
//                initAdapter()
//            }
//        } else {
//            val folder = File(Constant.videos_path)
//            if (folder.isDirectory) {
//                val allFiles = folder.listFiles()
//                if(allFiles.size!=null) {
//                allFiles?.forEach { allFile ->
//                    val media_data = Media_Data()
//                    media_data.name = allFile.name
//                    media_data.path = allFile.absolutePath
//                    media_data.folder = allFile.parent
//                    val file = File(allFile.absolutePath)
//                    media_data.length = file.length().toString()
//                    val lastModDate = Date(file.lastModified())
//                    media_data.modifieddate = lastModDate.toString()
//                    media_data.addeddate = lastModDate.toString()
//                    hideVideoData.add(media_data)
//                }
//                }
//                hideVideoData.reverse()
//                Timber.d("hideVideoData${hideVideoData.size}")
//                //initAdapter()
//            }
//            initAdapter()
//        }
//    }
//
//    fun getFileType(fileName: String?): String {
//        if (fileName != null) {
//            val typeIndex = fileName.lastIndexOf(".")
//            if (typeIndex != -1) {
//                return fileName.substring(typeIndex + 1)
//                    .lowercase(Locale.getDefault())
//            }
//        }
//        return ""
//    }
//
//    private  fun bottomSheetDialog(){
//        val dialog = BottomSheetDialog(this)
//        dialog.setContentView(R.layout.bottom_sheet_select)
//        val close= dialog.findViewById<ImageView>(R.id.tvClose)
//        val image= dialog.findViewById<ImageView>(R.id.ivGallery)
//        val folder= dialog.findViewById<ImageView>(R.id.ivFolder)
//        image?.setOnClickListener {
//            AdmobAdManager.getInstance(this).loadInterstitialAd(this, PreferencesManager.getInterstitialId(this), 0) {
//                val galleryIntent = Intent(Intent.ACTION_PICK)
//                galleryIntent.type = "video/*"
//                mUris.clear()
//                startActivityForResult(galleryIntent, 101)
//                dialog.dismiss()
//            }
//        }
//
//        folder?.setOnClickListener {
//            AdmobAdManager.getInstance(this).loadInterstitialAd(this, PreferencesManager.getInterstitialId(this), 0) {
//                val intent1 = Intent(this, VideoFolderActivity::class.java)
//                startActivity(intent1)
//                dialog.dismiss()
//            }
//        }
//
//        close?.setOnClickListener {
//            dialog.dismiss()
//        }
//
//
//        dialog.show()
//
//
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
////            R.id.fab_folder -> {
////                val intent1 = Intent(this, VideoFolderActivity::class.java)
////                startActivity(intent1)
////                //floating_btn_video?.collapse()
////            }
////            R.id.fab_gallery -> {
////                val galleryIntent = Intent(Intent.ACTION_PICK)
////                galleryIntent.type = "video/*"
////                mUris.clear()
////                startActivityForResult(galleryIntent, 101)
////               // floating_btn_video?.collapse()
////            }
//            R.id.video_unhide -> {
//                binding.run {
//                    binding.progress.visibility = View.VISIBLE
//                    binding.bgL.visibility = View.VISIBLE
//                    count.visibility = View.VISIBLE
//                    count.text = "00/" + selected_hide_video_data.size
//                    Thread {
//                        for (i in selected_hide_video_data.indices) {
//                            val sourcePath: String = selected_hide_video_data[i].path
//                            val source = File(sourcePath)
//                            val hide_data: Hide_Data? = database?.getHideData(source.name)
//                            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                                val treeUri = Uri.parse(pref.hiddenUri)
//                                if (treeUri != null) {
//                                    contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                                    val tree = DocumentFile.fromTreeUri(this@VideosActivity, treeUri)
//                                    for (file in tree!!.listFiles()) {
//                                        if (file.name == selected_hide_video_data[i].name) {
//                                            unhideFileOnAboveQ(this@VideosActivity, file.uri, selected_hide_video_data[i].name)
//                                            file.delete()
//                                            hideVideoData.remove(selected_hide_video_data[i])
//                                            database?.deleteHide(selected_hide_video_data[i].name)
//                                        }
//                                    }
//                                }
//                            } else {
//                                temp = false
//                                if (hide_data != null) if (source.name == hide_data.name) {
//                                    var destinationPath: String = hide_data.path
//                                    var destination: File
//                                    if (destinationPath == null) {
//                                        destinationPath =
//                                            Environment.getExternalStoragePublicDirectory(
//                                                Environment.DIRECTORY_PICTURES
//                                            ).toString() + "/" + source.name
//                                    }
//                                    destination = File(destinationPath)
//                                    try {
//                                        deleteFile(source, destination)
//                                    } catch (e: IOException) {
//                                        e.printStackTrace()
//                                    }
//                                    temp = true
//                                    database?.deleteHide(hide_data.name)
//                                    hideVideoData.remove(selected_hide_video_data[i])
//                                }
//                                if (!temp) {
//                                    val destinationPath =
//                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                                            .toString() + "/" + source.name
//                                    val destination = File(destinationPath)
//                                    try {
//                                        deleteFile(source, destination)
//                                        hideVideoData.remove(selected_hide_video_data[i])
//                                    } catch (e: IOException) {
//                                        e.printStackTrace()
//                                    }
//                                }
//                                val finalI: Int = i
//                                runOnUiThread { count.text = (if (finalI < 10) "0" else "") + finalI + "/" + selected_hide_video_data.size }
//                            }
//                        }
//                        runOnUiThread {
//                            Toast.makeText(this@VideosActivity, "successfully UnHide", Toast.LENGTH_SHORT).show()
//                            selected_hide_video_data.clear()
//                            title = "Videos"
//                            count.visibility = View.GONE
//                            progress.visibility = View.GONE
//                            bgL.visibility = View.GONE
//                            getVideos()
//                            adapter?.notifyDataSetChanged()
//                            floating_btn_video?.visibility = View.VISIBLE
//                            video_btn_lay?.visibility = View.GONE
//                            adapter?.IsLongClick = false
//                            IsSelectAll = false
//                            selecter?.isVisible = false
//                            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
//                            if (hideVideoData.size > 0) {
//                                binding.videoRecycler.visibility = View.VISIBLE
//                                binding.noVideo.visibility = View.GONE
//                            } else {
//                                binding.videoRecycler.visibility = View.GONE
//                                binding.noVideo.visibility = View.VISIBLE
//                            }
//                        }
//                    }.start()
//                }
//            }
//            R.id.video_delete ->{
//                binding.run {
//                    val deletedialog = AlertDialog.Builder(this@VideosActivity)
//                        .setTitle("Delete")
//                        .setMessage("Are you sure,you want to delete this videos?")
//                        .setPositiveButton("Delete") { dialog: DialogInterface, whichButton: Int ->
//                            progress.visibility = View.VISIBLE
//                            bgL.visibility = View.VISIBLE
//                            count.visibility = View.VISIBLE
//                            count.text = "00/" + selected_hide_video_data.size
//                            Thread {
//                                for (i in selected_hide_video_data.indices) {
//                                    val sourcePath: String = selected_hide_video_data[i].path
//                                    val source = File(sourcePath)
//                                    var destinationPath: String? = Constant.recycle_path.toString() + "/" + selected_hide_video_data[i].name
//                                    var destination = File(destinationPath)
//                                    try {
//                                        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                                            destinationPath = Constant.recycle_path
//                                            destination = File(destinationPath)
//                                            //                                            if (!destination.exists()) {
////                                                Uri rootUri = Uri.parse(Prefrancemanager.getHiddenUri());
////                                                ContentResolver contentResolver = getContentResolver();
////                                                contentResolver.takePersistableUriPermission(rootUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////                                                String rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri);
////                                                String path = File.separator + "Recyclebin" +
////                                                        File.separator + ".nomedia";
////                                                CreateFile createFile = new CreateFile(getApplicationContext(), contentResolver, path, rootUri, rootDocumentId
////                                                        , false, "", false, "*/*");
////                                                boolean isFileCreated = createFile.createNewFile(true, false);
////                                            }
////                                            if (Prefrancemanager.getIsRecyclePermission()) {
//                                            moveToRecycleAboveQ(
//                                                source,
//                                                File(destination.toString() + File.separator + selected_hide_video_data.get(i).name),
//                                                Uri.parse(selected_hide_video_data.get(i).path),
//                                                selected_hide_video_data.get(i).name
//                                            )
//                                            if (!TextUtils.isEmpty(pref.hiddenUri)) {
//                                                val treeUri =
//                                                    Uri.parse(pref.hiddenUri)
//                                                if (treeUri != null) {
//                                                    contentResolver.takePersistableUriPermission(
//                                                        treeUri,
//                                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                                                    )
//                                                    val tree =
//                                                        DocumentFile.fromTreeUri(this@VideosActivity, treeUri)
//                                                    if (tree != null && tree.isDirectory) {
//                                                        for (file in tree.listFiles()) {
//                                                            if (file.name == selected_hide_video_data.get(i).name) {
//                                                                file.delete()
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            //                                            } else {
////                                                recycle_source = source;
////                                                recycle_destination = destination;
////                                                recycle_name = selected_hide_video_data.get(i).getName();
////                                                askPermissionForFragment(
////                                                        "Pictures%2F.Calculator Lock%2FRecyclebin",
////                                                        ACCESS_RECYCLE_FOLDER);
////                                            }
//                                        } else {
//                                            deleteFile(source, destination)
//                                        }
//                                        val delet_data = Delet_Data()
//                                        val calendar = Calendar.getInstance()
//                                        delet_data.name = selected_hide_video_data.get(i).name
//                                        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
//                                        delet_data.date = dateFormat.format(calendar.time)
//                                        database?.addDelete(delet_data)
//                                        hideVideoData.remove(selected_hide_video_data.get(i))
//                                    } catch (e: IOException) {
//                                        e.printStackTrace()
//                                    }
//                                }
//                                runOnUiThread {
//                                    Toast.makeText(
//                                        this@VideosActivity,
//                                        "successfully Deleted",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    selected_hide_video_data.clear()
//                                    title = "Videos"
//                                    progress.visibility = View.GONE
//                                    bgL.visibility = View.GONE
//                                    count.visibility = View.GONE
//                                    getVideos()
//                                    adapter?.notifyDataSetChanged()
//                                    adapter?.IsLongClick = false
//                                    IsSelectAll = false
//                                    floating_btn_video?.visibility = View.VISIBLE
//                                    video_btn_lay?.visibility = View.GONE
//                                    selecter?.isVisible = false
//                                    selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
//                                    if (hideVideoData.size > 0) {
//                                        videoRecycler.visibility = View.VISIBLE
//                                        noVideo.visibility = View.GONE
//                                    } else {
//                                        videoRecycler.visibility = View.GONE
//                                        noVideo.visibility = View.VISIBLE
//                                    }
//                                }
//                            }.start()
//                            dialog.dismiss()
//                        }
//                        .setNegativeButton(
//                            "cancel"
//                        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
//                        .create()
//                    deletedialog.show()
//                }
//
//            }
//            R.id.permissionBtn->{
//                binding.run {
//                    val destinationPath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
//                            + File.separator +
//                            ".Calculator Lock")
//                    val destination = File(destinationPath)
//                    if (!destination.exists()) {
//                        askPermissionForFragment("Pictures",
//                            CREATE_HIDDEN_FOLDER
//                        )
//                    } else if (!pref.hiddenPermission) {
//                        permissionBtn.setText(R.string.hidden_folder_permission_btn)
//                        askPermissionForFragment(
//                            "Pictures%2F.Calculator Lock",
//                            ACCESS_HIDDEN_FOLDER
//                        )
//                    } else {
//                        permissionBtn.visibility = View.GONE
//                        permissionText.visibility = View.GONE
//                        floating_btn_video?.setVisibility(View.VISIBLE)
//                    }
//                }
//            }
//
//        }
//    }
//
//
//    fun askPermissionForFragment(targetDirectory: String, requestCode: Int) {
//        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//            val storageManager = this.getSystemService(STORAGE_SERVICE) as StorageManager
//            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
//            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
//            var scheme = uri.toString()
//            scheme = scheme.replace("/root/", "/document/")
//            scheme += "%3A$targetDirectory"
//            uri = Uri.parse(scheme)
//            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
//            startActivityForResult(intent, requestCode)
//        }
//    }
//
//
//    @Throws(IOException::class)
//    private fun moveToRecycleAboveQ(source: File, destination: File, muri: Uri, newName: String) {
//        if (!destination.parentFile.exists()) {
//            destination.parentFile.mkdirs()
//        }
//        val source_channel: FileChannel? = null
//        val destination_channel: FileChannel? = null
//        try {
//            val isRename = source.renameTo(destination)
//            if (isRename) {
//                if (!source.path.contains("'") && source.exists()) {
////                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
//                }
//            } else {
//                if (!destination.exists()) {
//                    val contentResolver = contentResolver
//                    val rootUri = Uri.parse(pref.recycleUri)
//                    contentResolver.takePersistableUriPermission(
//                        rootUri,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    )
//                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
//                    try {
//                        val fileInputStream =
//                            contentResolver.openInputStream(muri) as FileInputStream?
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    val createFile = CreateFile(
//                        this,
//                        contentResolver,
//                        File.separator + newName,
//                        rootUri,
//                        rootDocumentId,
//                        true,
//                        muri,
//                        true,
//                        Utils.getMimeType(source.name)
//                    )
//                    val isFileCreated: Boolean = createFile.createNewFile(false, true)
//                }
//                source.delete()
//            }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(source.path), null
//            ) { path: String, uri: Uri ->
//                Log.i(
//                    "ExternalStorage",
//                    "Scanned $path:$uri"
//                )
//            }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(destination.path), null
//            ) { path: String, uri: Uri ->
//                Log.i(
//                    "ExternalStorage",
//                    "Scanned $path:$uri"
//                )
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            source_channel?.close()
//            destination_channel?.close()
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun deleteFile(source: File, destination: File) {
//        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
//        var source_channel: FileChannel? = null
//        var destination_channel: FileChannel? = null
//        try {
//            val isRename = source.renameTo(destination)
//            if (isRename) {
//                if (!source.path.contains("'") && source.exists()) {
//                    contentResolver.delete(
//                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                        MediaStore.Video.Media.DATA + "='" + source.path + "'",
//                        null
//                    )
//                }
//            } else {
//                if (!destination.exists()) {
//                    destination.createNewFile()
//                }
//                source_channel = FileInputStream(source).channel
//                destination_channel = FileOutputStream(destination).channel
//                destination_channel.transferFrom(source_channel, 0, source_channel.size())
//                source_channel.close()
//                source.delete()
//            }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(source.path), null
//            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
//            MediaScannerConnection.scanFile(
//                this, arrayOf(destination.path), null
//            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
//        } finally {
//            source_channel?.close()
//            destination_channel?.close()
//        }
//    }
//
//    fun unhideFileOnAboveQ(context: Context, uri: Uri?, name: String): Boolean {
//        val contentResolver = context.contentResolver
//        var fos: FileOutputStream? = null
//        val folderName = ""
//        val relativePath = Environment.DIRECTORY_PICTURES + File.separator + "Calculator Vault Unhide"
//        val mimeType: String = getFileType(name)
//        try {
//            return if (Utils.isImage(name.split(".").toTypedArray()[1])) {
//                val contentValues = ContentValues()
//                contentValues.put(
//                    MediaStore.MediaColumns.DISPLAY_NAME,
//                    name.split(".").toTypedArray()[0]
//                )
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
//                val imageUri = contentResolver.insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    contentValues
//                )
//                val inputStream = contentResolver.openInputStream(uri!!) as FileInputStream?
//                fos = contentResolver.openOutputStream(imageUri!!) as FileOutputStream?
//                val inChannel = inputStream!!.channel
//                val outChannel = fos!!.channel
//                outChannel.transferFrom(inChannel, 0, inChannel.size())
//                inChannel.close()
//                outChannel.close()
//                true
//            } else {
//                val contentValues = ContentValues()
//                contentValues.put(
//                    MediaStore.MediaColumns.DISPLAY_NAME,
//                    name.split(".").toTypedArray()[0]
//                )
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/$mimeType")
//                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
//                val imageUri = contentResolver.insert(
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    contentValues
//                )
//                fos = contentResolver.openOutputStream(imageUri!!) as FileOutputStream?
//                val inStream = contentResolver.openInputStream(uri!!) as FileInputStream?
//                val inChannel = inStream!!.channel
//                val outChannel = fos!!.channel
//                inChannel.transferTo(0, inChannel.size(), outChannel)
//                inStream.close()
//                fos.close()
//                true
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            try {
//                fos?.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        return false
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_sort_item, menu)
//        selecter = menu.findItem(R.id.select)
//        selecter?.isVisible = false
//        return true
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (binding.progress.visibility != View.VISIBLE) {
////            if (floating_btn_video?.isExpanded == true) {
////                floating_btn_video?.collapse()
////            } else {
////                if (video_btn_lay?.visibility == View.GONE) {
////                    super.onBackPressed()
////                } else {
////                    IsSelectAll = false
////                    adapter?.IsLongClick = false
////                    floating_btn_video?.visibility = View.VISIBLE
////                    video_btn_lay?.visibility = View.GONE
////                    selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
////                    selected_hide_video_data.clear()
////                    selecter?.isVisible = false
////                    adapter?.notifyDataSetChanged()
////                    title = "Videos"
////                }
////            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        IsSelectAll = false
//        adapter?.IsLongClick = false
//    }
//}