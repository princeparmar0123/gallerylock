package com.calculator.vault.lock.hide.photo.video.ui.intruderSelfie

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Delet_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Hide_Data
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile
import com.calculator.vault.lock.hide.photo.video.common.utils.Utils
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityIntruderBinding
import com.suke.widget.SwitchButton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class IntruderSelfieActivity : BaseActivity<ActivityIntruderBinding>(R.layout.activity_intruder) {

    var CREATE_HIDDEN_FOLDER = 123
    var ACCESS_HIDDEN_FOLDER = 12
    var ACCESS_RECYCLE_FOLDER = 121
    private var intruder_dialog: Dialog? = null
    var intruder_data = ArrayList<Media_Data>()
    var recycle_source: File? = null
    var recycle_destination: File? = null
    var recycle_name: String? = null
    private var database: Database? = null
    private var camera_per = 0
    private var d_inruder_switch: SwitchButton? = null
    private var intruderSelfieAdapter: IntruderSelfieAdapter? = null

    companion object {
        var selected_intruder_data: ArrayList<Media_Data> = ArrayList()
        var duplicate_selected_intruder_data: ArrayList<Media_Data> = ArrayList()
        var IsSelectAll = false
        var floating_btn_intruder: com.calculator.vault.lock.hide.photo.video.floatingbutton.FloatingActionButton? =
            null
        var selecter: MenuItem? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        binding.toolbar.title = "Intruder Selfie"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        floating_btn_intruder = binding.floatingBtnIntruder
        if (!pref.getIntruder) {
            initDialog()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                askPermission()
            }
        }
        initView()
        setPermissionButton()
        initListener()
    }


    private fun setPermissionButton() {
        binding.run {
            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                val destinationPath =
                    (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + ".Calculator Lock")
                val destination = File(destinationPath)
                if (!destination.exists()) {
                    permissionBtn.setText(R.string.pictures_folder_permission_btn)
                    permissionBtn.visibility = View.VISIBLE
                    permissionText.visibility = View.VISIBLE
                } else if (!pref.hiddenPermission) {
                    permissionBtn.setText(R.string.hidden_folder_permission_btn)
                    permissionBtn.visibility = View.VISIBLE
                    permissionText.visibility = View.VISIBLE
                } else {
                    permissionBtn.visibility = View.GONE
                    permissionText.visibility = View.GONE
                }
            }
        }

    }

    fun askPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, 100)
    }

    private fun initListener() {
        binding.floatingBtnIntruder.setOnClickListener { v ->
            val deletedialog =
                AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Are you sure,you want to delete this selfies?")
                    .setPositiveButton(
                        "Delete"
                    ) { dialog, whichButton ->
                        runOnUiThread {
                          //  Thread{
                      //  doAsync {
                            for (i in selected_intruder_data.indices) {
                                val sourcePath: String = selected_intruder_data[i].path
                                val source = File(sourcePath)
                                var destinationPath: String =
                                    Constant.recycle_path.toString() + "/" + selected_intruder_data[i].name
                                var destination = File(destinationPath)
                                try {
                                    if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                                        destinationPath = Constant.recycle_path.toString() + "/" + selected_intruder_data[i].name
                                        destination = File(destinationPath)
                                        if (!destination.exists()) {
                                            val rootUri = Uri.parse(pref.hiddenUri)
                                            val contentResolver = contentResolver
                                            contentResolver.takePersistableUriPermission(
                                                rootUri,
                                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                            )
                                            val rootDocumentId =
                                                DocumentsContract.getTreeDocumentId(rootUri)
                                            val path =
                                                File.separator + "Recyclebin" +
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
                                            val isFileCreated: Boolean =
                                                createFile.createNewFile(true, false)
                                        }
                                        if (pref.isRecyclePermission) {
                                            moveToRecycleAboveQ(
                                                source,
                                                File(
                                                    destination.toString() + File.separator + selected_intruder_data.get(
                                                        i
                                                    ).name
                                                ),
                                                Uri.parse(selected_intruder_data[i].path),
                                                selected_intruder_data[i].name
                                            )
                                            deleteSelectedFile(selected_intruder_data.get(i).name)
                                        } else {
                                            recycle_source = source
                                            recycle_destination = destination
                                            recycle_name = selected_intruder_data.get(i).name
                                            askPermissionForFragment(
                                                "Pictures%2F.Calculator Lock%2FRecyclebin",
                                                ACCESS_RECYCLE_FOLDER
                                            )
                                        }
                                        //                                        duplicate_selected_intruder_data.add(selected_intruder_data.get(i));
                                        //                                        mUri.add(getImageUriFromFile(selected_intruder_data.get(i).getPath(),Intruder_Activity.this));
                                    } else {
                                        deleteFile(source, destination)
                                    }
                                    val delet_data = Delet_Data()
                                    val calendar = Calendar.getInstance()
                                    delet_data.name = selected_intruder_data[i].name
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                                    delet_data.date = dateFormat.format(calendar.time)
                                    database = Database(this@IntruderSelfieActivity)
                                    database?.addDelete(delet_data)
                                    intruder_data.remove(selected_intruder_data[i])
                                    Toast.makeText(this@IntruderSelfieActivity, "successfully deleted", Toast.LENGTH_SHORT).show()
                                    getImages()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                      //  }

                          //  }.start()

                        }

                        selected_intruder_data.clear()
                        intruderSelfieAdapter?.notifyDataSetChanged()
                        floating_btn_intruder?.visibility = View.GONE
                        intruderSelfieAdapter?.IsLongClick = false
                        IsSelectAll = false
                        selecter?.isVisible = false
                        selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
                        if (intruder_data.size > 0) {
                            binding.intruderRecycler.visibility = View.VISIBLE
                            binding.noIntruder.visibility = View.GONE
                        } else {
                            binding.intruderRecycler.visibility = View.GONE
                            binding.noIntruder.visibility = View.VISIBLE
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
                    .create()
            deletedialog.show()
        }
        binding.permissionBtn.setOnClickListener { v: View? ->
            val destinationPath =
                (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + ".Calculator Lock")
            val destination = File(destinationPath)
            if (!destination.exists()) {
                askPermissionForFragment(
                    "Pictures",
                    CREATE_HIDDEN_FOLDER
                )
            } else if (!pref.hiddenPermission) {
                binding.permissionBtn.setText(R.string.hidden_folder_permission_btn)
                askPermissionForFragment(
                    "Pictures%2F.Calculator Lock",
                    ACCESS_HIDDEN_FOLDER
                )
            } else {
                binding.permissionBtn.visibility = View.GONE
                binding.permissionText.visibility = View.GONE
            }
        }
    }

    @Throws(IOException::class)
    private fun moveToRecycleAboveQ(source: File, destination: File, muri: Uri, newName: String) {
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
                    val rootUri = Uri.parse(pref.recycleUri)
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

    private fun deleteSelectedFile(imageName: String) {
        if (!TextUtils.isEmpty(pref.hiddenUri)) {
            val treeUri = Uri.parse(pref.hiddenUri)
            if (treeUri != null) {
                contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
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
            when (requestCode) {
                ACCESS_RECYCLE_FOLDER -> {
                    val rootUri2 = data!!.data
                    pref.recycleUri = rootUri2.toString()
                    pref.isRecyclePermission = true
                    try {
                        recycle_source?.let {
                            recycle_name?.let { it1 ->
                                moveToRecycleAboveQ(
                                    it,
                                    File(recycle_destination.toString() + File.separator + recycle_name),
                                    Uri.parse(recycle_name),
                                    it1
                                )
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CREATE_HIDDEN_FOLDER -> {
                    if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        if (!pref.hiddenPermission) {
                            binding.permissionBtn.setText(R.string.hidden_folder_permission_btn)
                        }
                    }
                    val rootUri = data!!.data
                    val contentResolver = this.contentResolver
                    if (rootUri != null) {
                        contentResolver.takePersistableUriPermission(
                            rootUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                    val rootDocumentId = DocumentsContract.getTreeDocumentId(rootUri)

                    val path = File.separator + ".Calculator Lock" +
                            File.separator + ".nomedia"

                    val createFile = rootUri?.let {
                        CreateFile(
                            applicationContext, contentResolver, path,
                            it, rootDocumentId, false, "", false, "*/*"
                        )
                    }

                    val isFileCreated = createFile?.createNewFile(true, false)
                }
                ACCESS_HIDDEN_FOLDER -> {
                    val rootUri1 = data?.data
                    val contentResolver1 = this.contentResolver
                    contentResolver1.takePersistableUriPermission(
                        rootUri1!!,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val rootDocumentId1 = DocumentsContract.getTreeDocumentId(rootUri1)
                    pref.hiddenUri = rootUri1.toString()
                    pref.hiddenPermission = true
                    binding.permissionBtn.visibility = View.GONE
                    binding.permissionText.visibility = View.GONE
                }
            }
        }
    }

    private fun initView() {
        database = Database(this)
        getImages()
    }

    private fun getImages() {
        intruder_data.clear()
        if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(pref.hiddenUri)) {
                val treeUri = Uri.parse(pref.hiddenUri)
                if (treeUri != null) {
                    this.contentResolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val tree = DocumentFile.fromTreeUri(this, treeUri)
                    if (tree != null && tree.isDirectory) {
                        for (file in tree.listFiles()) {
                            val media_data = Media_Data()
                            media_data.name = file.name
                            media_data.path = file.uri.toString()
                            media_data.folder = file.parentFile.toString()
                            val mFile = file.uri.path?.let { File(it) }
                            media_data.length = mFile?.length().toString()
                            val lastModDate = mFile?.let { Date(it?.lastModified()) }
                            media_data.modifieddate = lastModDate.toString()
                            media_data.addeddate = lastModDate.toString()
                            if (file.name?.contains("Intruder_") == true) {
                                intruder_data.add(media_data)
                            }
                        }
                    }
                    intruder_data.reverse()
                }
                initAdapter()
            }
        } else {
            val folder = File(Constant.intruder_path)
            if (folder.isDirectory) {
                val allFiles = folder.listFiles()
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
                    intruder_data.add(media_data)
                }
                intruder_data.reverse()
            }
            initAdapter()
        }
    }

    private fun initAdapter() {
        if (intruder_data.size > 0) {
            binding.intruderRecycler.visibility = View.VISIBLE
            binding.noIntruder.visibility = View.GONE
            intruderSelfieAdapter = IntruderSelfieAdapter(this)
            binding.intruderRecycler.layoutManager =
                GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
            binding.intruderRecycler.adapter = intruderSelfieAdapter
            intruderSelfieAdapter?.addAll(intruder_data)
        } else {
            binding.intruderRecycler.visibility = View.GONE
            binding.noIntruder.visibility = View.VISIBLE
        }
    }









    @Throws(IOException::class)
    private fun deleteFile(source: File, destination: File) {
        if (!destination.parentFile.exists()) destination.parentFile.mkdirs()
        var source_channel: FileChannel? = null
        var destination_channel: FileChannel? = null
        val hide_data = Hide_Data()
        hide_data.name = source.name
        hide_data.path = source.path
        database?.addHide(hide_data)
        try {
            val isRename = source.renameTo(destination)
            if (isRename) {
                contentResolver.delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.Media.DATA + "='" + source.path + "'",
                    null
                )
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
            MediaScannerConnection.scanFile(this, arrayOf(destination.path), null)
            { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:$uri")
            }
        } finally {
            source_channel?.close()
            destination_channel?.close()
        }
    }











    private fun initDialog() {
        intruder_dialog = Dialog(this@IntruderSelfieActivity, R.style.WideDialog)
        intruder_dialog?.setContentView(R.layout.dialog_intruder)
        intruder_dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val close: ImageView? = intruder_dialog?.findViewById(R.id.close)
        d_inruder_switch = intruder_dialog?.findViewById(R.id.d_inruder_switch)
        close?.setOnClickListener { v: View? ->
            intruder_dialog?.dismiss()
            finish()
        }
        d_inruder_switch?.setOnCheckedChangeListener { view: SwitchButton?, isChecked: Boolean ->
            camera_per = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (camera_per == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    askPermission()
                }
                if (isChecked) {
                    pref.getIntruder = true
                    intruder_dialog?.dismiss()

                } else {
                    pref.getIntruder = false
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    110
                )
            }
        }
        intruder_dialog?.show()
        intruder_dialog?.setCancelable(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.select -> if (intruderSelfieAdapter != null) {
                if (!IsSelectAll) {
                    IsSelectAll = true
                    intruderSelfieAdapter?.IsLongClick = true
                    floating_btn_intruder?.visibility = View.VISIBLE
                    item.icon = resources.getDrawable(R.drawable.iv_select)
                    selected_intruder_data.clear()
                    selected_intruder_data.addAll(intruder_data)
                } else {
                    IsSelectAll = false
                    intruderSelfieAdapter?.IsLongClick = false
                    floating_btn_intruder?.visibility = View.GONE
                    item.icon = resources.getDrawable(R.drawable.iv_unselect)
                    selected_intruder_data.clear()
                    selecter?.isVisible = false
                }
                intruderSelfieAdapter?.notifyDataSetChanged()
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
        IsSelectAll = false
        intruderSelfieAdapter?.IsLongClick = false
        selected_intruder_data.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            110 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    d_inruder_switch!!.isChecked = true
                    pref.getIntruder = true
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        askPermission()
                    }
                    intruder_dialog?.dismiss()
                } else {
                    d_inruder_switch?.isChecked = false
                    pref.getIntruder = false
                    Toast.makeText(this, "Please allow this permission...", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getImages()
    }

    override fun onBackPressed() {
        if (selected_intruder_data.size != 0) {
            IsSelectAll = false
            intruderSelfieAdapter?.IsLongClick = false
            floating_btn_intruder?.visibility = View.GONE
            selecter?.icon = resources.getDrawable(R.drawable.iv_unselect)
            selected_intruder_data.clear()
            selecter?.isVisible = false
            intruderSelfieAdapter?.notifyDataSetChanged()
        } else {
            super.onBackPressed()
        }
    }

}