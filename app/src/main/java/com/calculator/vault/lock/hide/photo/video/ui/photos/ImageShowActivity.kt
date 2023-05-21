package com.calculator.vault.lock.hide.photo.video.ui.photos

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.os.Build.VERSION
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.viewpager.widget.ViewPager
import com.calculator.vault.lock.hide.photo.video.App.Companion.getInstance
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.FileUtils
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityImageShowBinding
import com.calculator.vault.lock.hide.photo.video.ui.photos.PhotosActivity.hide_media_data
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class ImageShowActivity : BaseActivity<ActivityImageShowBinding>(R.layout.activity_image_show) {

    companion object {

    }


    // private val image_viewpager: ViewPager? = null
    private var viewpager_adapter: SwipeAdapter? = null
    private var pos = 0
    private val photosData: ArrayList<Media_Data> = ArrayList<Media_Data>()
    var hide_data: ArrayList<Hide_Data> = ArrayList()
    private var type = 0
    var temp = false
    private var database: Database? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        pos = intent.getIntExtra("pos", 0)
        if (hide_media_data != null && hide_media_data.size !== 0) {
            photosData.addAll(hide_media_data)
        }
        type = intent.getIntExtra("type", 0)
        database = Database(this)
        initView()
    }


    private fun initView() {
        setSupportActionBar(binding.toolbar)
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //        getSupportActionBar().setDisplayShowTitleEnabled(false);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.navigationIcon?.setColorFilter(resources.getColor(android.R.color.white),
            PorterDuff.Mode.SRC_ATOP)

        initAdapter()


        binding.imageViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                title = photosData[position].name
                binding.toolbar.visibility = View.VISIBLE
                Handler(Looper.myLooper()!!).postDelayed({ binding.toolbar.visibility = View.GONE },
                    3000)
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private fun initAdapter() {
        viewpager_adapter =
            SwipeAdapter(
                this@ImageShowActivity,
                photosData
            )
        binding.imageViewpager.adapter = viewpager_adapter
        binding.imageViewpager.currentItem = pos
        title = photosData[pos].name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.menu_unhide -> {
                hide_data.clear()
                database?.allHide?.let { hide_data.addAll(it) }
                val sourcePath = photosData[binding.imageViewpager.currentItem].path
                val source = File(sourcePath)

                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    val treeUri = Uri.parse(getInstance().getPref().hiddenUri)
                    if (treeUri != null) {
                        this.contentResolver.takePersistableUriPermission(treeUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        val tree = DocumentFile.fromTreeUri(this, treeUri)
                        for (documentFile in tree!!.listFiles()) {
                            if (documentFile.isDirectory && documentFile.name == Constant.photo_Folder) {
                                for (file in documentFile.listFiles()) {
                                    if (file.name == photosData[binding.imageViewpager.currentItem].name) {
                                        unhideFileOnAboveQ(this,
                                            file.uri,
                                            file.name.toString())
                                        file.delete()
                                        database!!.deleteHide(photosData[binding.imageViewpager.currentItem].name)
                                        photosData.removeAt(binding.imageViewpager.currentItem)
                                        break
                                    }
                                }
                            }
                        }
                    }
                } else {
                    temp = false
                    var j = 0
                    while (j < hide_data.size) {
                        if (source.name == hide_data[j].name) {
                            val destinationPath = hide_data[j].path
                            val destination = File(destinationPath)
                            try {
                                deleteFile(source, destination)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            temp = true
                            database?.deleteHide(hide_data[j].name)
                            photosData.removeAt(binding.imageViewpager.currentItem)
                            break
                        }
                        j++
                    }
                    if (!temp) {
                        val destinationPath =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .toString() + "/" + source.name
                        val destination = File(destinationPath)
                        try {
                            deleteFile(source, destination)
                            photosData.removeAt(binding.imageViewpager.currentItem)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                binding.imageViewpager.adapter = viewpager_adapter
                if (photosData.size == 0) {
                    finish()
                }
            }
            R.id.menu_delete -> {
                val deletedialog = AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Are you sure,you want to delete this photo?")
                    .setPositiveButton(
                        "Delete"
                    ) { dialog, whichButton ->
                        val sourcePath1 = photosData[binding.imageViewpager.currentItem].path
                        val fileName = photosData[binding.imageViewpager.currentItem].name
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                        val dateTime = dateFormat.format(calendar.time)
                        var source1 = File(sourcePath1)
                        val recycleFile = File(Constant.recycle_path)
                        if (!recycleFile.exists()) recycleFile.mkdirs()
                        var destinationPath: String =
                            Constant.recycle_path.toString() + "/" + photosData[binding.imageViewpager.currentItem].name
                        var destination = File(destinationPath)
                        try {
                            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                destinationPath = Constant.recycle_path
                                destination = File(destinationPath)
                                var path = FileUtils.getRealPath(this@ImageShowActivity,
                                    Uri.parse(sourcePath1))
                                if (path != null)
                                    source1 = File(path)

                                Utils.moveToRecycleAboveQ(this, source1,
                                    File(destination.toString() + File.separator + photosData[binding.imageViewpager.currentItem].name),
                                    Uri.parse(photosData[binding.imageViewpager.currentItem].path),
                                    photosData[binding.imageViewpager.currentItem].name)

                            } else {
                                deleteFile(source1, destination)
                            }

                            try {
                                val delet_data = Delet_Data()
                                delet_data.name = fileName
                                delet_data.date = dateTime
                                database!!.addDelete(delet_data)
                                photosData.remove(photosData[binding.imageViewpager.currentItem])

//                                val delet_data = Delet_Data()
//                                val calendar = Calendar.getInstance()
//                                delet_data.name =
//                                    photosData[binding.imageViewpager.currentItem].name
//                                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
//
//                                delet_data.date = dateTime
//                                database!!.addDelete(delet_data)
//
                                if (photosData.size == 0) {
                                    finish()
                                } else {
//                                    initAdapter()
                                    initAdapter()
//                                    binding.imageViewpager.adapter = viewpager_adapter
                                }


//                                val delet_data = Delet_Data()
//                                val calendar = Calendar.getInstance()
//                                delet_data.name = photosData[binding.imageViewpager.currentItem].name
//                                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
//                                delet_data.date = dateFormat.format(calendar.time).toString()
//                                database?.addDelete(delet_data)
//                                photosData.remove(photosData[binding.imageViewpager.currentItem])
//                                binding.imageViewpager.adapter = viewpager_adapter
//                                if (photosData.size == 0) {
//                                    finish()
//                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "cancel"
                    ) { dialog, which -> dialog.dismiss() }
                    .create()
                deletedialog.show()
            }
        }
        return false
    }

    fun unhideFileOnAboveQ(context: Context, uri: Uri?, name: String): Boolean {
        val contentResolver = context.contentResolver
        var fos: FileOutputStream? = null
        val folderName = ""
        val relativePath = Environment.DIRECTORY_PICTURES + File.separator +
                "Calculator Vault Unhide"
        val mimeType = getFileType(name)
        Log.e("unhideFileOnAboveQ", "name==> $name  mimeType==> $mimeType")
        try {
            return if (Utils.isImage(mimeType)) {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,
                    name.split("\\.").toTypedArray()[0])
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
                val inputStream = contentResolver.openInputStream(uri!!) as FileInputStream?
                fos = contentResolver.openOutputStream(imageUri!!) as FileOutputStream?
                val inChannel = inputStream!!.channel
                val outChannel = fos!!.channel
                outChannel.transferFrom(inChannel, 0, inChannel.size())
                inChannel.close()
                outChannel.close()
                true
            } else {
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,
                    name.split("\\.").toTypedArray()[0])
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/$mimeType")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                val imageUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
                fos = contentResolver.openOutputStream(imageUri!!) as FileOutputStream?
                val inStream = contentResolver.openInputStream(uri!!) as FileInputStream?
                val inChannel = inStream!!.channel
                val outChannel = fos!!.channel
                inChannel.transferTo(0, inChannel.size(), outChannel)
                inStream.close()
                fos.close()
                true
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e("TAG121", "unhideFileOnAboveQ: " + e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG121", "unhideFileOnAboveQ: " + e.message)
        } finally {
            try {
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun getFileType(fileName: String?): String? {
        if (fileName != null) {
            val typeIndex = fileName.lastIndexOf(".")
            if (typeIndex != -1) {
                return fileName.substring(typeIndex + 1)
                    .lowercase(Locale.getDefault())
            }
        }
        return ""
    }

    @Throws(IOException::class)
    private fun deleteFile(source: File, destination: File) {
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
                this, arrayOf(destination.path), null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_hide, menu)
        if (type == 0) {
            menu.findItem(R.id.menu_unhide).isVisible = true
            menu.findItem(R.id.menu_delete).isVisible = true
        } else if (type == 1) {
            menu.findItem(R.id.menu_unhide).isVisible = false
            menu.findItem(R.id.menu_delete).isVisible = true
        } else if (type == 2) {
            menu.findItem(R.id.menu_unhide).isVisible = false
            menu.findItem(R.id.menu_delete).isVisible = false
        }
        return true
    }

//    @Throws(IOException::class)
//    private fun moveToRecycleAboveQ(source: File, destination: File, muri: Uri, newName: String) {
//        if (!destination.parentFile.exists()) {
//            destination.parentFile.mkdirs()
//        }
////        val source_channel: FileChannel? = null
////        val destination_channel: FileChannel? = null
//        try {
//            val isRename = source.renameTo(destination)
//            if (isRename) {
//                if (!source.path.contains("'") && source.exists()) {
////                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
//                }
//            } else {
//                moveFile2(source, destination)
////                if (!destination.exists()) {
////                    val contentResolver = contentResolver
////                    val rootUri = Uri.parse(getInstance().getPref().recycleUri)
////                    contentResolver.takePersistableUriPermission(rootUri,
////                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
////                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
////                    try {
////                        val fileInputStream =
////                            contentResolver.openInputStream(muri) as FileInputStream?
////                    } catch (e: Exception) {
////                        e.printStackTrace()
////                    }
////                    val createFile = CreateFile(this,
////                        contentResolver,
////                        File.separator + newName,
////                        rootUri,
////                        rootDocumentId,
////                        true,
////                        muri,
////                        true,
////                        Utils.getMimeType(source.name))
////                    val isFileCreated = createFile.createNewFile(false, true)
////                }
//                source.delete()
//            }
//            MediaScannerConnection.scanFile(this, arrayOf(source.path), null
//            ) { path: String, uri: Uri ->
//            }
////            MediaScannerConnection.scanFile(this, arrayOf(destination.path), null
////            ) { path: String, uri: Uri ->
////                Log.i("ExternalStorage", "Scanned $path:$uri")
////            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
////            source_channel?.close()
////            destination_channel?.close()
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun moveFile2(source: File, destination: File) {
//        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
//        var source_channel: FileChannel? = null
//        var destination_channel: FileChannel? = null
//        try {
//            val isRename = source.renameTo(destination)
//            if (isRename) {
//                if (!source.path.contains("'")) {
//                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        MediaStore.Images.Media.DATA + "='" + source.path + "'",
//                        null)
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
//
//                source_channel?.close()
//                destination_channel?.close()
//            }
//            MediaScannerConnection.scanFile(this, arrayOf(source.path), null
//            ) { path, uri -> }
//
//            MediaScannerConnection.scanFile(this, arrayOf(destination.path), null
//            ) { path, uri ->  }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    @Throws(IOException::class)
    private fun moveFile(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        var source_channel: FileChannel? = null
        var destination_channel: FileChannel? = null
        try {
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'")) {
                    contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA + "='" + source.path + "'",
                        null)
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

                source_channel?.close()
                destination_channel?.close()
            }
            MediaScannerConnection.scanFile(this, arrayOf(source.path), null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }

            MediaScannerConnection.scanFile(this, arrayOf(destination.path), null
            ) { path, uri -> Log.i("ExternalStorage", "Scanned $path:$uri") }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        val hide_data = Hide_Data()
        hide_data.name = destination.name
        hide_data.path = source.path
        database!!.addHide(hide_data)
    }

}