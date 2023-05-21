package com.calculator.vault.lock.hide.photo.video.ui.recyclebin

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils.isImage
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityRecyclebinBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RecycleBinActivity : BaseActivity<ActivityRecyclebinBinding>(R.layout.activity_recyclebin), View.OnClickListener {

    //private val spinnerQuestion: Spinner? = null
    private var type = 0
    private var database: Database? = null
    var recycle_data = ArrayList<Media_Data>()
    var check_delet_data: ArrayList<Delet_Data> = ArrayList<Delet_Data>()
    var recyclerAdapter : RecyclerAdapter? = null

    companion object{
        var CREATE_HIDDEN_FOLDER = 111
        var ACCESS_HIDDEN_FOLDER = 121
        var ACCESS_RECYCLE_FOLDER = 131
        var no_recycler: LinearLayout? = null
        var btn_lay: LinearLayout? = null
        var IsSelectAll = false
        var selecter: MenuItem? = null
        var selected_recycle_data: ArrayList<Media_Data> = ArrayList<Media_Data>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.title = "Recycle Bin"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            initView()
            //initListener()
//            setPermissionButton();
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        initAdapter()



    }

    private fun initView(){
        database = Database(this@RecycleBinActivity)
        binding.run {
            recyclerDelete.setOnClickListener(this@RecycleBinActivity)
            recyclerRestore.setOnClickListener(this@RecycleBinActivity)
            no_recycler = noRecycler
            btn_lay = btnLay
        }
    }


    private fun initAdapter() {
        if (recycle_data.size > 0) {
                binding.recyclebinRecycler.visibility = View.VISIBLE
                no_recycler?.visibility = View.GONE
                recyclerAdapter = RecyclerAdapter(this)
               binding.recyclebinRecycler.adapter = recyclerAdapter
               recyclerAdapter?.addAll(recycle_data)

            } else {
            binding.recyclebinRecycler.visibility = View.GONE
            no_recycler?.visibility = View.VISIBLE
            }

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


    private fun getRecycleData() {
        recycle_data.clear()
        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (pref.isRecyclePermission) {
                if (!TextUtils.isEmpty(pref.recycleUri)) {
                    val treeUri = Uri.parse(pref.recycleUri)
                    if (treeUri != null) {
                        contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        val tree = DocumentFile.fromTreeUri(this, treeUri)
                        if (tree != null && tree.isDirectory) {
                            for (file in tree.listFiles()) {
                                val media_data = Media_Data()
                                media_data.name = file.name
                                media_data.path = file.uri.toString()
                                val mFile = File(file.uri.toString())
                                media_data.length = mFile.length().toString()
                                val lastModDate = Date(mFile.lastModified())
                                media_data.modifieddate = lastModDate.toString()
                                media_data.addeddate = lastModDate.toString()
                                if (isImage(getFileType(file.name)) || Utils.isVideo(getFileType(file.name))) {
                                    recycle_data.add(media_data)
                                }
                            }
                        }
                        recycle_data.reverse()
                    }
                    initAdapter()
                }
            } else {
                binding.recyclebinRecycler.visibility = View.GONE
                no_recycler?.visibility = View.VISIBLE
            }
        } else {
            val folder: File = File(Constant.recycle_path)
            if (folder.isDirectory) {
                val allFiles = folder.listFiles()
                if (allFiles != null) {
                    for (i in allFiles.indices) {
                        val media_data = Media_Data()
                        media_data.name = allFiles[i].name
                        media_data.path = allFiles[i].absolutePath
                        media_data.folder = allFiles[i].parent
                        val file = File(allFiles[i].absolutePath)
                        media_data.length = file.length().toString()
                        val lastModDate = Date(file.lastModified())
                        media_data.modifieddate = lastModDate.toString()
                        media_data.addeddate = lastModDate.toString()
                        recycle_data.add(media_data)
                    }
                }
                recycle_data.reverse()
            }
            initCheck()
            initAdapter()
        }
    }

    @Throws(ParseException::class)
    private fun initCheck() {
        check_delet_data.clear()
        check_delet_data.addAll(database!!.allDelete)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -10)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val temp_date = dateFormat.format(calendar.time)
        val date = dateFormat.parse(temp_date)
        for (i in check_delet_data.indices) {
            if (calendar.timeInMillis > dateFormat.parse(check_delet_data[i].date).time) {
                for (j in recycle_data.indices) {
                    if (check_delet_data.get(i).name.equals(recycle_data[j].name)) {
                        val sourcePath = recycle_data[j].path
                        val source = File(sourcePath)
                        source.delete()
                        database!!.deleteData(check_delet_data[i].name)
                        database!!.deleteHide(check_delet_data[i].name)
                        recycle_data.remove(recycle_data[j])
                    }
                }
            }
        }
    }

    private fun askPermissionForFragment(targetDirectory: String, requestCode: Int) {
        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val storageManager = this.getSystemService(STORAGE_SERVICE) as StorageManager
            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
            var scheme = uri.toString()
            scheme = scheme.replace("/root/", "/document/")
            scheme += "%3A$targetDirectory"
            uri = Uri.parse(scheme)
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == ACCESS_RECYCLE_FOLDER) {
                val uri = data!!.data
                pref.isRecyclePermission = true
                pref.recycleUri = uri.toString()
            } else if (requestCode == CREATE_HIDDEN_FOLDER) {
                if (!pref.isRecyclePermission) {
                    askPermissionForFragment(
                        "Pictures%2F.Calculator Lock",
                        ACCESS_HIDDEN_FOLDER
                    )
                }
                val rootUri = data!!.data
                val contentResolver = this.contentResolver
                contentResolver.takePersistableUriPermission(
                    rootUri!!,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
                val path = File.separator + ".Calculator Lock" +
                        File.separator + ".nomedia"
                val createFile = CreateFile(
                    applicationContext,
                    contentResolver,
                    path,
                    rootUri,
                    rootDocumentId,
                    false,
                    "",
                    false,
                    "*/*"
                )
                val isFileCreated: Boolean = createFile.createNewFile(true, false)
            } else if (requestCode == ACCESS_RECYCLE_FOLDER) {
                val rootUri1 = data!!.data
                val contentResolver1 = this.contentResolver
                contentResolver1.takePersistableUriPermission(
                    rootUri1!!,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1)
                pref.hiddenUri= rootUri1.toString()
                pref.isRecyclePermission = true
                try {
                    getRecycleData()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun restoreFileAboveQ(source: File, destination: File, muri: Uri, newName: String) {
        if (!destination.parentFile.exists()) {
            destination.parentFile.mkdirs()
        }
        val source_channel: FileChannel? = null
        val destination_channel: FileChannel? = null
        try {
            val isRename = source.renameTo(destination)
            if (isRename) {
                if (!source.path.contains("'") && source.exists()) {
//                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "='" + source.getPath() + "'", null);
                }
            } else {
                if (!destination.exists()) {
                    val contentResolver = contentResolver
                    val rootUri = Uri.parse(pref.hiddenUri)
                    contentResolver.takePersistableUriPermission(
                        rootUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)
                    try {
                        val fileInputStream =
                            contentResolver.openInputStream(muri) as FileInputStream?
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val createFile = CreateFile(
                        this,
                        contentResolver,
                        File.separator + newName,
                        rootUri,
                        rootDocumentId,
                        true,
                        muri,
                        true,
                        Utils.getMimeType(source.name)
                    )
                    val isFileCreated = createFile.createNewFile(false, true)
                }
                source.delete()
            }
            MediaScannerConnection.scanFile(
                this, arrayOf(source.path), null
            ) { path: String, uri: Uri ->
                Log.i(
                    "ExternalStorage",
                    "Scanned $path:$uri"
                )
            }
            MediaScannerConnection.scanFile(
                this, arrayOf(destination.path), null
            ) { path: String, uri: Uri ->
                Log.i(
                    "ExternalStorage",
                    "Scanned $path:$uri"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.select -> if (recyclerAdapter != null) {
                if (!IsSelectAll) {
                    IsSelectAll = true
                    recyclerAdapter?.IsLongClick = true
                    item.icon = resources.getDrawable(R.drawable.iv_select)
                    btn_lay?.visibility = View.VISIBLE
                    selected_recycle_data.clear()
                    selected_recycle_data.addAll(recycle_data)
                } else {
                    IsSelectAll = false
                    recyclerAdapter?.IsLongClick = false
                    item.icon = resources.getDrawable(R.drawable.iv_unselect)
                    selected_recycle_data.clear()
                    btn_lay?.visibility = View.GONE
                    selecter?.isVisible = false
                }
                recyclerAdapter?.notifyDataSetChanged()
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_select, menu)
        selecter = menu.findItem(R.id.select)
        selecter?.isVisible = false
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        IsSelectAll =false
        selected_recycle_data.clear()
        recyclerAdapter?.IsLongClick = false
    }

    override fun onResume() {
        super.onResume()
        try {
            getRecycleData()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun restoreFile(source: File, destination: File) {
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


    override fun onBackPressed() {
        if (selected_recycle_data.size != 0) {
            IsSelectAll = false
            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
            selected_recycle_data.clear()
            selecter?.isVisible = false
            btn_lay?.visibility = View.GONE
            recyclerAdapter?.IsLongClick = false
            recyclerAdapter?.notifyDataSetChanged()
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBack->{
                onBackPressed()
            }
            R.id.recycler_delete->{
                if (selected_recycle_data.size > 0) {
                    val deletedialog = AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are you sure,you want to delete this files?")
                        .setPositiveButton(
                            "Delete"
                        ) { dialog, whichButton ->
                            for (i in selected_recycle_data.indices) {
                                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                    if (!TextUtils.isEmpty(pref.recycleUri)) {
                                        val treeUri = Uri.parse(pref.recycleUri)
                                        if (treeUri != null) {
                                            contentResolver.takePersistableUriPermission(
                                                treeUri,
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                            )
                                            val tree = DocumentFile.fromTreeUri(this, treeUri)
                                            if (tree != null && tree.isDirectory) {
                                                for (file in tree.listFiles()) {
                                                    if (file.name == selected_recycle_data.get(i).name) {
                                                        file.delete()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val sourcePath: String = selected_recycle_data.get(i).path
                                    val source = File(sourcePath)
                                    source.delete()
                                }
                                database!!.deleteHide(selected_recycle_data[i].name)
                                database!!.deleteData(selected_recycle_data[i].name)
                                recycle_data.remove(selected_recycle_data[i])
                            }
                            Toast.makeText(this, "Delete Data...", Toast.LENGTH_SHORT).show()
                            getRecycleData()
                            selected_recycle_data.clear()
                            IsSelectAll = false
                            recyclerAdapter?.IsLongClick = false
                            recyclerAdapter?.notifyDataSetChanged()
                            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
                            selecter?.isVisible = false
                            btn_lay?.visibility = View.GONE
                            if (recycle_data.size > 0) {
                                binding.recyclebinRecycler.visibility = View.VISIBLE
                                no_recycler?.visibility = View.GONE
                            } else {
                                binding.recyclebinRecycler.visibility = View.GONE
                                no_recycler?.visibility = View.VISIBLE
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(
                            "cancel"
                        ) { dialog, which -> dialog.dismiss() }
                        .create()
                    deletedialog.show()
                } else {
                    Toast.makeText(this, "select images and videos...", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.recycler_restore->{
                if (selected_recycle_data.size > 0) {
                    for (i in selected_recycle_data.indices) {
                        val sourcePath: String = selected_recycle_data.get(i).path
                        val source = File(sourcePath)
                        if (selected_recycle_data.get(i).name.contains("Intruder_")) {
                            val destinationPath =
                                Constant.intruder_path.toString() + "/" + selected_recycle_data.get(i).name
                            val destination = File(destinationPath)
                            try {
                                restoreFile(source, destination)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                val destinationPath =
                                    Constant.home_path.toString() + File.separator + selected_recycle_data[i].name
                                val destination = File(destinationPath)
                                try {
                                    restoreFileAboveQ(
                                        source,
                                        destination,
                                        Uri.parse(selected_recycle_data.get(i).getPath()),
                                        selected_recycle_data.get(i).getName()
                                    )
                                    deleteSelectedFile(
                                        selected_recycle_data.get(i).getName()
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                if (isImage(source)) {
                                    val destinationPath =
                                        Constant.photos_path.toString() + "/" + selected_recycle_data.get(i).getName()
                                    val destination = File(destinationPath)
                                    try {
                                        restoreFile(source, destination)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    val destinationPath =
                                        Constant.videos_path.toString() + "/" + selected_recycle_data.get(i).getName()
                                    val destination = File(destinationPath)
                                    try {
                                        restoreFile(source, destination)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                        recycle_data.remove(selected_recycle_data.get(i))
                        database!!.deleteData(selected_recycle_data.get(i).getName())
                    }
                    Toast.makeText(this, "Restore Data...", Toast.LENGTH_SHORT).show()
                    selected_recycle_data.clear()
                    recyclerAdapter?.notifyDataSetChanged()
                    selecter?.setVisible(false)
                    IsSelectAll = false
                    recyclerAdapter?.IsLongClick = false
                    btn_lay?.setVisibility(View.GONE)
                    selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
                    if (recycle_data.size > 0) {
                        binding.recyclebinRecycler.visibility = View.VISIBLE
                        no_recycler?.visibility = View.GONE
                    } else {
                        binding.recyclebinRecycler.visibility = View.GONE
                        no_recycler?.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this, "select images and videos...", Toast.LENGTH_SHORT).show()
                }
            }
        }


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

    private fun deleteSelectedFile(imageName: String) {
        if (!TextUtils.isEmpty(pref.recycleUri)) {
            val treeUri = Uri.parse(pref.recycleUri)
            if (treeUri != null) {
                contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val tree = DocumentFile.fromTreeUri(this, treeUri)
                if (tree != null && tree.isDirectory) {
                    for (file in tree.listFiles()) {
                        if (file.name == imageName) {
                            file.delete()
                        }
                    }
                }
            }
        }
    }

    }
