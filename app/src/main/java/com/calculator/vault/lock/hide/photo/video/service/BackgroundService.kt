package com.calculator.vault.lock.hide.photo.video.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.calculator.vault.lock.hide.photo.video.App.Companion.getInstance
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.data.database.Database
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.GoogleDriveFileHolder
import com.calculator.vault.lock.hide.photo.video.common.data.network.model.Media_Data
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.DriveServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnSuccessListener
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File
import java.util.*

class BackgroundService() : Service() {
    var upload_image_data = ArrayList<Media_Data>()
    var upload_video_data = ArrayList<Media_Data>()
    private var database: Database? = null
    private var driveServiceHelper: DriveServiceHelper? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        database = Database(this)
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val googleAccountCredential =
            GoogleAccountCredential.usingOAuth2(this, setOf(DriveScopes.DRIVE_FILE))
        googleAccountCredential.selectedAccount = googleSignInAccount!!.account
        val drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            googleAccountCredential
        )
            .setApplicationName(resources.getString(R.string.app_name))
            .build()
        driveServiceHelper = DriveServiceHelper(drive)
        val name = resources.getString(R.string.app_name)
        driveServiceHelper!!.checkFolder(name).addOnSuccessListener { aBoolean ->
            if (!aBoolean!!) {
                driveServiceHelper!!.createFolder(name).addOnSuccessListener { s ->
                    getInstance().getPref().folderId = s
                    //Prefrancemanager.putFolderId(s);
                    getImages(driveServiceHelper!!)
                    getVideo(driveServiceHelper!!)
                }
            } else {
                if ((getInstance().getPref().folderId == "")) {
                } else {
                    getImages(driveServiceHelper!!)
                    getVideo(driveServiceHelper!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        super.onCreate();
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }

    fun getImages(driveServiceHelper: DriveServiceHelper) {
        upload_image_data.clear()
        val folder = File(Constant.photos_path)
        if (folder.isDirectory) {
            val allFiles = folder.listFiles()
            if (allFiles != null) {
                for (i in allFiles.indices) {
                    val media_data = Media_Data()
                    media_data.name = allFiles.get(i).name
                    media_data.path = allFiles.get(i).absolutePath
                    media_data.folder = allFiles.get(i).parent
                    val file = File(allFiles[i].absolutePath)
                    media_data.length = file.length().toString()
                    val lastModDate = Date(file.lastModified())
                    media_data.modifieddate = lastModDate.toString()
                    media_data.addeddate = lastModDate.toString()
                    upload_image_data.add(media_data)
                }
            }
        }
        driveServiceHelper.checkSubFolder("Image")
            .addOnSuccessListener { aBoolean ->
                if (!aBoolean!!) {
                    driveServiceHelper.createSubFolder(
                        "Image",
                        getInstance().getPref().folderId
                    ).addOnSuccessListener { s ->
                        getInstance().getPref().imageFolder = s
                        uploadImages()
                        DeleteImages()
                    }
                } else {
                    uploadImages()
                    DeleteImages()
                }
            }
    }

    private fun DeleteImages() {
        driveServiceHelper!!.queryFiles(getInstance().getPref().imageFolder).addOnSuccessListener { googleDriveFileHolders: List<GoogleDriveFileHolder> ->
            for (i in googleDriveFileHolders.indices) {
                var temp: Boolean = false
                for (j in upload_image_data.indices) {
                    if ((googleDriveFileHolders.get(i).getName() == upload_image_data.get(j)
                            .getName())
                    ) {
                        temp = true
                        break
                    }
                }
                if (!temp) {
                    driveServiceHelper!!.deleteFile(googleDriveFileHolders.get(i).getId())
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            override fun onSuccess(aVoid: Void?) {
                                Log.d("Data", "onSuccess: ====> Successfull Deleted")
                            }
                        })
                }
            }
        }
    }

    private fun uploadImages() {
        for (i in upload_image_data.indices) {
            val temp = i
            Log.d("Data", "uploadImages: ====>" + upload_image_data[i].name)
            driveServiceHelper?.checkImage(upload_image_data[temp].name)?.addOnSuccessListener { aBoolean ->
                    if (!aBoolean!!) {
                        Log.d("Data", "onSuccess: ====> Upload " + upload_image_data[temp].name)
                        driveServiceHelper?.insertImageFile(
                            upload_image_data[temp].name,
                            upload_image_data[temp].path,
                            getInstance().getPref().imageFolder
                        )
                    }
                }
        }
    }

    fun getVideo(driveServiceHelper: DriveServiceHelper) {
        upload_video_data.clear()
        val folder = File(Constant.videos_path)
        if (folder.isDirectory) {
            val allFiles = folder.listFiles()
            if (allFiles != null) {
                for (i in allFiles.indices) {
                    val media_data = Media_Data()
                    media_data.name = allFiles.get(i).name
                    media_data.path = allFiles.get(i).absolutePath
                    media_data.folder = allFiles.get(i).parent
                    val file = File(allFiles[i].absolutePath)
                    media_data.length = file.length().toString()
                    val lastModDate = Date(file.lastModified())
                    media_data.modifieddate = lastModDate.toString()
                    media_data.addeddate = lastModDate.toString()
                    upload_video_data.add(media_data)
                }
            }
        }
        driveServiceHelper.checkSubFolder("Video")
            .addOnSuccessListener(object : OnSuccessListener<Boolean?> {
                override fun onSuccess(aBoolean: Boolean?) {
                    if (!aBoolean!!) {
                        driveServiceHelper.createSubFolder(
                            "Video",
                            getInstance().getPref().folderId
                        ).addOnSuccessListener(object : OnSuccessListener<String?> {
                            override fun onSuccess(s: String?) {
                                getInstance().getPref().videoFolder = s
                                uploadVideo()
                                DeleteVideo()
                            }
                        })
                    } else {
                        uploadVideo()
                        DeleteVideo()
                    }
                }
            })
    }

    private fun DeleteVideo() {
        driveServiceHelper!!.queryFiles(getInstance().getPref().videoFolder)
            .addOnSuccessListener(object : OnSuccessListener<List<GoogleDriveFileHolder>> {
                override fun onSuccess(googleDriveFileHolders: List<GoogleDriveFileHolder>) {
                    for (i in googleDriveFileHolders.indices) {
                        var temp = false
                        for (j in upload_video_data.indices) {
                            if ((googleDriveFileHolders[i].name == upload_video_data[j].name)) {
                                temp = true
                                break
                            }
                        }
                        if (!temp) {
                            driveServiceHelper!!.deleteFile(googleDriveFileHolders[i].id)
                                .addOnSuccessListener(object : OnSuccessListener<Void?> {
                                    override fun onSuccess(aVoid: Void?) {
                                        Log.d("Data", "onSuccess: ====> Successfull Deleted")
                                    }
                                })
                        }
                    }
                }
            })
    }

    private fun uploadVideo() {
        for (i in upload_video_data.indices) {
            Log.d("Data", "uploadImages: ====>" + upload_video_data[i].name)
            val temp = i
            driveServiceHelper!!.checkVideo(upload_video_data[temp].name)
                .addOnSuccessListener(object : OnSuccessListener<Boolean?> {
                    override fun onSuccess(aBoolean: Boolean?) {
                        if (!aBoolean!!) {
                            Log.d("Data", "onSuccess: ====> Upload " + upload_video_data[temp].name)
                            driveServiceHelper!!.insertVideoFile(
                                upload_video_data[temp].name,
                                upload_video_data[temp].path,
                                getInstance().getPref().videoFolder
                            )
                        }
                    }
                })
        }
    }
}