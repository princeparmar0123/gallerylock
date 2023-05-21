package com.calculator.vault.lock.hide.photo.video.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.os.Build.VERSION
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import timber.log.Timber
import java.io.*

class CamerSelfiService : Service(), SurfaceHolder.Callback {
    // Camera variables
    // a surface holder
    // a variable to control the camera
    private var mCamera: Camera? = null

    // the camera parameters
    private var parameters: Camera.Parameters? = null
    private var bmp: Bitmap? = null
    var fo: FileOutputStream? = null
    private var FLASH_MODE: String? = null
    private var QUALITY_MODE = 0
    private var isFrontCamRequest = false
    private var safeToTakePicture = false
    private var pictureSize: Camera.Size? = null
    var sv: SurfaceView? = null
    private var sHolder: SurfaceHolder? = null
    private var windowManager: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    var cameraIntent: Intent? = null
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var width = 0
    var height = 0

    /**
     * Called when the activity is first created.
     */
    override fun onCreate() {
        super.onCreate()
        Log.e("onCreate", "Yes")
    }

    private fun openFrontFacingCameraGingerbread(): Camera? {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
        }
        var cameraCount = 0
        var cam: Camera? = null
        val cameraInfo = CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx)
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        cam.enableShutterSound(false)
                    }
                } catch (e: RuntimeException) {
                    Log.e(
                        "Camera",
                        "Camera failed to open: " + e.localizedMessage
                    )
                }
            }
        }
        return cam
    }

    private fun setBesttPictureResolution() {
        // get biggest picture size
        width = pref!!.getInt("Picture_Width", 0)
        height = pref!!.getInt("Picture_height", 0)
        if (width == 0 || height == 0) {
            pictureSize = getBiggesttPictureSize(parameters)
            if (pictureSize != null) parameters!!.setPictureSize(
                pictureSize!!.width,
                pictureSize!!.height
            )
            // save width and height in sharedprefrences
            width = pictureSize!!.width
            height = pictureSize!!.height
            editor!!.putInt("Picture_Width", width)
            editor!!.putInt("Picture_height", height)
            editor!!.commit()
        } else {
            // if (pictureSize != null)
            parameters!!.setPictureSize(width, height)
        }
    }

    private fun getBiggesttPictureSize(parameters: Camera.Parameters?): Camera.Size? {
        var result: Camera.Size? = null
        for (size in parameters!!.supportedPictureSizes) {
            if (result == null) {
                result = size
            } else {
                val resultArea = result.width * result.height
                val newArea = size.width * size.height
                if (newArea > resultArea) {
                    result = size
                }
            }
        }
        return result
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            true
        } else {
            // no camera on this device
            false
        }
    }

    /**
     * Check if this device has front camera
     */
    private fun checkFrontCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_FRONT
        )
    }

    var handler = Handler()

    private inner class TakeImage : AsyncTask<Intent?, Void?, Void?>() {
        protected override fun doInBackground(vararg params: Intent?): Void? {
            takeImage(params[0]!!)
            return null
        }

        override fun onPostExecute(result: Void?) {
            Log.e("PixcelDone", "Done")
            Handler().postDelayed({ Constant.isFirstTime = true }, 2000)
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Log.e("PixcelDone", "Starte")
        }
    }

    @Synchronized
    private fun takeImage(intent: Intent) {
        Log.e("ImageTakin", "takeImage")
        if (checkCameraHardware(applicationContext)) {
            val extras = intent.extras
            if (extras != null) {
                val flash_mode = extras.getString("FLASH")
                FLASH_MODE = flash_mode
                val front_cam_req = extras.getBoolean("Front_Request")
                isFrontCamRequest = front_cam_req
                Log.e("front_cam_req", front_cam_req.toString() + "")
                val quality_mode = extras.getInt("Quality_Mode")
                QUALITY_MODE = quality_mode
            }
            if (isFrontCamRequest) {

                // set flash 0ff
                FLASH_MODE = "off"
                // only for gingerbread and newer versions
                if (VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    mCamera = openFrontFacingCameraGingerbread()
                    if (mCamera != null) {
                        try {
                            mCamera!!.setPreviewDisplay(sv!!.holder)
                        } catch (e: IOException) {
                            handler.post { /*  Toast.makeText(getApplicationContext(),
                                            "API dosen't support front camera",
                                            Toast.LENGTH_LONG).show();*/
                                Log.e("SelfiCameraTackPicture", "API dosen't support front camera")
                            }
                            stopSelf()
                        }
                        val parameters = mCamera!!.parameters
                        pictureSize = getBiggesttPictureSize(parameters)
                        if (pictureSize != null) parameters
                            .setPictureSize(pictureSize!!.width, pictureSize!!.height)

                        // set camera parameters
                        mCamera!!.parameters = parameters
                        mCamera!!.startPreview()
                        if (safeToTakePicture) {
                            mCamera!!.takePicture(null, null, mCall)
                            safeToTakePicture = false
                        }

                        // return 4;
                    } else {
                        mCamera = null
                        handler.post {
                            Log.e(
                                "SelfiCameraTackPicture",
                                "Your Device dosen't have Front Camera !a"
                            )

                            /*   Toast.makeText(
                                                            getApplicationContext(),
                                                            "Your Device dosen't have Front Camera !",
                                                            Toast.LENGTH_LONG).show();*/
                        }
                        stopSelf()
                    }
                    /*
                     * sHolder = sv.getHolder(); // tells Android that this
                     * surface will have its data // constantly // replaced if
                     * (Build.VERSION.SDK_INT < 11)
                     *
                     * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                     */
                } else {
                    if (checkFrontCamera(applicationContext)) {
                        mCamera = openFrontFacingCameraGingerbread()
                        if (mCamera != null) {
                            try {
                                mCamera!!.setPreviewDisplay(sv!!.holder)
                            } catch (e: IOException) {
                                handler.post {
                                    Log.e(
                                        "SelfiCameraTackPicture",
                                        "API dosen't support front camera"
                                    )

                                    /*  Toast.makeText(
                                                                            getApplicationContext(),
                                                                            "API dosen't support front camera",
                                                                            Toast.LENGTH_LONG).show();*/
                                }
                                stopSelf()
                            }
                            val parameters = mCamera!!.parameters
                            pictureSize = getBiggesttPictureSize(parameters)
                            if (pictureSize != null) parameters.setPictureSize(
                                pictureSize!!.width,
                                pictureSize!!.height
                            )

                            // set camera parameters
                            mCamera!!.parameters = parameters
                            mCamera!!.startPreview()
                            mCamera!!.takePicture(null, null, mCall)
                            // return 4;
                        } else {
                            mCamera = null
                            /*
                             * Toast.makeText(getApplicationContext(),
                             * "API dosen't support front camera",
                             * Toast.LENGTH_LONG).show();
                             */handler.post {
                                Log.e(
                                    "SelfiCameraTackPicture",
                                    "Your Device dosen't have Front Camera !"
                                )


                                /*   Toast.makeText(
                                                                    getApplicationContext(),
                                                                    "Your Device dosen't have Front Camera !",
                                                                    Toast.LENGTH_LONG).show();*/
                            }
                            stopSelf()
                        }
                        // Get a surface
                        /*
                         * sHolder = sv.getHolder(); // tells Android that this
                         * surface will have its data // constantly // replaced
                         * if (Build.VERSION.SDK_INT < 11)
                         *
                         * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS
                         * );
                         */
                    }
                }
            } else {
                mCamera = if (mCamera != null) {
                    mCamera!!.stopPreview()
                    mCamera!!.release()
                    Camera.open()
                } else cameraInstance
                try {
                    if (mCamera != null) {
                        mCamera!!.setPreviewDisplay(sv!!.holder)
                        parameters = mCamera!!.parameters
                        if (FLASH_MODE == null || FLASH_MODE!!.isEmpty()) {
                            FLASH_MODE = "auto"
                        }
                        parameters!!.flashMode = FLASH_MODE
                        // set biggest picture
                        setBesttPictureResolution()
                        // log quality and image format
                        Log.d("Qaulity", parameters!!.jpegQuality.toString() + "")
                        Log.d("Format", parameters!!.pictureFormat.toString() + "")

                        // set camera parameters
                        mCamera!!.parameters = parameters
                        mCamera!!.startPreview()
                        Log.d("ImageTakin", "OnTake()")
                        mCamera!!.takePicture(null, null, mCall)
                    } else {
                        handler.post {
                            Log.e("SelfiCameraTackPicture", "Camera is unavailable !")
                            /*  Toast.makeText(getApplicationContext(),
                                                            "Camera is unavailable !",
                                                            Toast.LENGTH_LONG).show();*/
                        }
                    }
                    // return 4;
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    Log.e("TAG", "CmaraHeadService()::takePicture", e)
                }
                // Get a surface
                /*
                 * sHolder = sv.getHolder(); // tells Android that this surface
                 * will have its data constantly // replaced if
                 * (Build.VERSION.SDK_INT < 11)
                 *
                 * sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                 */
            }
        } else {
            // display in long period of time
            /*
             * Toast.makeText(getApplicationContext(),
             * "Your Device dosen't have a Camera !", Toast.LENGTH_LONG)
             * .show();
             */
            handler.post {
                Log.e("SelfiCameraTackPicture", "Your Device dosen't have a Camera !")

                /* Toast.makeText(getApplicationContext(),
                                    "Your Device dosen't have a Camera !",
                                    Toast.LENGTH_LONG).show();*/
            }
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // sv = new SurfaceView(getApplicationContext());
        cameraIntent = intent
        Log.d("ImageTakin", "StartCommand()")
        Log.e("SeriveApl", "Omcreante")
        pref = applicationContext.getSharedPreferences("MyPref", 0)
        editor = pref!!.edit()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params!!.gravity = Gravity.TOP or Gravity.LEFT
        params!!.width = 1
        params!!.height = 1
        params!!.x = 0
        params!!.y = 0
        sv = SurfaceView(applicationContext)
        windowManager!!.addView(sv, params)
        sHolder = sv!!.holder
        sHolder!!.addCallback(this)

        // tells Android that this surface will have its data constantly
        // replaced
        if (VERSION.SDK_INT < 11) sHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        return START_STICKY
    }

    var mCall =
        PictureCallback { data, camera -> // decode the data obtained by the camera into a Bitmap
            Log.e("ImageTakin", "Done")
            if (bmp != null) bmp!!.recycle()
            System.gc()
            bmp = decodeBitmap(data)
            val bytes = ByteArrayOutputStream()
            if (bmp != null && QUALITY_MODE == 0) bmp!!.compress(
                Bitmap.CompressFormat.JPEG,
                70,
                bytes
            ) else if (bmp != null && QUALITY_MODE != 0) bmp!!.compress(
                Bitmap.CompressFormat.JPEG,
                QUALITY_MODE,
                bytes
            )
            val Final: String
            val imagesFolder: File
            if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Final = Constant.intruder_path
                imagesFolder = File(Final)
            } else {
                Final = Constant.intruder_path
                imagesFolder = File(Final)
            }
            if (imagesFolder.exists() && imagesFolder.isDirectory) {
                println("Directory exist")
            } else {
                try {
                    if (imagesFolder.mkdirs()) {
                    } else {
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (!imagesFolder.exists()) imagesFolder.mkdirs() // <----
            val image = File(
                imagesFolder, "Intruder_" + System.currentTimeMillis() + ".jpg"
            )
            try {
                fo = FileOutputStream(image)
            } catch (e: FileNotFoundException) {
                Log.e("TAG", "FileNotFoundException", e)
                // TODO Auto-generated catch block
            }
            try {
                fo!!.write(bytes.toByteArray())
            } catch (e: IOException) {
                Log.e("TAG", "fo.write::PictureTaken", e)
            }
            try {
                fo!!.close()
                if (VERSION.SDK_INT < 19) sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())
                    )
                ) else {
                    MediaScannerConnection
                        .scanFile(
                            applicationContext, arrayOf(image.toString()),
                            null
                        ) { path, uri ->
                            Timber.tag("ExternalStorage").i("Scanned $path:")
                            Timber.tag("ExternalStorage").i("-> uri=$uri")
                        }
                }
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            Handler().postDelayed({
                try {
                    if (mCamera != null) {
                        mCamera!!.stopPreview()
                        mCamera!!.release()
                        mCamera = null
                    }
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }, 500)

            /*
               * Toast.makeText(getApplicationContext(),
               * "Your Picture has been taken !", Toast.LENGTH_LONG).show();
               */Log.d("Camera", "Image Taken !")
            if (bmp != null) {
                bmp!!.recycle()
                bmp = null
                System.gc()
            }
            mCamera = null
            handler.post { /*   Toast.makeText(getApplicationContext(),
                            "Your Picture has been taken !", Toast.LENGTH_SHORT)
                            .show();*/
                Log.e("SelfiCameraTackPicture", "Your Picture has been taken !")
            }
            stopSelf()
        }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
        if (sv != null) windowManager!!.removeView(sv)
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        super.onDestroy()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int,
    ) {
        // TODO Auto-generated method stub
        try {
            mCamera!!.startPreview()
            safeToTakePicture = true
        } catch (_: Exception) {

        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.e("ViewCreated==>", "Yes")
        if (cameraIntent != null) {
            if (Constant.isFirstTime) {
                Constant.isFirstTime = false
                Log.e("FirstTime", "Yes")
                TakeImage().execute(cameraIntent)
            } else {
                Log.e("FirstTime", "Multiple")
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    companion object {
        // Camera is not available (in use or does not exist)
        // returns null if camera is unavailable
        // attempt to get a Camera instance
        val cameraInstance: Camera?
            get() {
                var c: Camera? = null
                try {
                    c = Camera.open() // attempt to get a Camera instance
                } catch (e: Exception) {
                    // Camera is not available (in use or does not exist)
                }
                return c // returns null if camera is unavailable
            }

        fun decodeBitmap(data: ByteArray?): Bitmap? {
            var bitmap: Bitmap? = null
            val bfOptions = BitmapFactory.Options()
            bfOptions.inDither = false // Disable Dithering mode
            bfOptions.inPurgeable = true // Tell to gc that whether it needs free
            // memory, the Bitmap can be cleared
            bfOptions.inInputShareable = true // Which kind of reference will be
            // used to recover the Bitmap data
            // after being clear, when it will
            // be used in the future
            bfOptions.inTempStorage = ByteArray(32 * 1024)
            if (data != null) bitmap = BitmapFactory.decodeByteArray(
                data, 0, data.size,
                bfOptions
            )
            val m = Matrix()
            m.postRotate(270f)
            bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, m, true)
            return bitmap
        }
    }
}