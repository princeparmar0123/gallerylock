package com.calculator.vault.lock.hide.photo.video.ui.settings

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.calculator.vault.lock.hide.photo.video.BuildConfig
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.data.prefs.PreferencesManager
import com.calculator.vault.lock.hide.photo.video.databinding.ActivitySettingsBinding
import com.calculator.vault.lock.hide.photo.video.ui.MainActivity
import com.calculator.vault.lock.hide.photo.video.ui.recoveryQuestion.RecoveryQuestionActivity
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.ACCESS_HIDDEN_FOLDER
import com.calculator.vault.lock.hide.photo.video.ui.recyclebin.RecycleBinActivity.Companion.CREATE_HIDDEN_FOLDER
import com.suke.widget.SwitchButton
import java.io.File

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(R.layout.activity_settings), View.OnClickListener,
    SwitchButton.OnCheckedChangeListener {

    private var camera_per = 0
    private var permissionDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClickListener()
        binding.intruderSwitch.isChecked = pref.getIntruder
    }
    private fun onClickListener(){
        binding.intruderSwitch.setOnClickListener(this)
        binding.settingAboutUs.setOnClickListener(this)
        binding.settingMoreApps.setOnClickListener(this)
        binding.settingPrivacy.setOnClickListener(this)
        binding.settingContactus.setOnClickListener(this)
        binding.settingRateus.setOnClickListener(this)
        binding.settingShare.setOnClickListener(this)
        binding.settingHideappp.setOnClickListener(this)
        binding.settingQuestion.setOnClickListener(this)
        binding.settingCpass.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.intruderSwitch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.setting_about_us->{
                val about_intent = Intent(this, AboutusActivity::class.java)
                startActivity(about_intent)
            }
            R.id.setting_contactus->{
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf(resources.getString(R.string.email))
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                    intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
                    intent.type = "text/html"
                    intent.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(intent, "Send mail"))
                } catch (e: Exception) {
                    Toast.makeText(this, "Not installed Gmail", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.setting_cpass->{
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("type", 1)
                startActivity(intent)
            }
            R.id.setting_privacy->{
                val privacy_intent = Intent(this, PrivacyActivity::class.java)
                startActivity(privacy_intent)
            }
            R.id.setting_moreApps->{
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=KD Inc.")
                    )
                )
            }
            R.id.setting_rateus->{
                val uri = Uri.parse("market://details?id=$packageName")
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
                try {
                    startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }
            R.id.setting_share->{
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.setting_hideappp->{
                val intent1 = Intent(this, HideappActivity::class.java)
                startActivity(intent1)
            }
            R.id.setting_question->{
                val questionIntent = Intent(this, RecoveryQuestionActivity::class.java)
                questionIntent.putExtra("type", 1)
                startActivity(questionIntent)
            }
            R.id.ivBack ->{
              onBackPressed()
            }

        }
    }

    override fun onResume() {
        super.onResume()
            binding.intruderSwitch.setOnCheckedChangeListener(null)
        binding.intruderSwitch.isChecked = pref.getIntruder
        binding.intruderSwitch.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            110 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        askPermission()
                    }
                    pref.getIntruder = true
                } else {
                    pref.getIntruder = false
                    Toast.makeText(
                        this,
                        "Please allow this permission...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }


    private fun showPermissionDialog() {
        permissionDialog = Dialog(this, R.style.WideDialog)
        permissionDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        permissionDialog?.setCancelable(true)
        permissionDialog?.setContentView(R.layout.dialog_permission_version_11)
        permissionDialog?.setCanceledOnTouchOutside(true)
        permissionDialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        permissionDialog?.getWindow()?.setGravity(Gravity.CENTER)
        permissionDialog?.show()
        val btnAllow: TextView? = permissionDialog?.findViewById<TextView>(R.id.btnAllow)
        val btnDeny: TextView? = permissionDialog?.findViewById<TextView>(R.id.btnDeny)
        btnAllow?.setOnClickListener { v: View? ->
            val destinationPath =
                (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .path
                        + File.separator +
                        ".Calculator Lock")
            val destination = File(destinationPath)
            if (destination.exists()) {
                if (pref.hiddenPermission) {
                    pref.getIntruder = true
                    permissionDialog?.dismiss()
                } else {
                    askPermissionForFragment(
                        "Pictures%2F.Calculator Lock",
                        ACCESS_HIDDEN_FOLDER
                    )
                }
            } else {
                askPermissionForFragment(
                    "Pictures",
                    CREATE_HIDDEN_FOLDER
                )
            }
        }
        btnDeny?.setOnClickListener { v: View? -> permissionDialog?.dismiss() }
    }


    fun askPermissionForFragment(targetDirectory: String, requestCode: Int) {
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

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        camera_per = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (camera_per == PackageManager.PERMISSION_GRANTED) {
            if (isChecked) {
                if (!Settings.canDrawOverlays(this)) {
                    askPermission()
                }
                if (VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    showPermissionDialog()
                } else {
                    pref.getIntruder = true
                }
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

    fun askPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, 100)
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putInt("activeIndex", binding.navView.getSelectedIndex())
//        super.onSaveInstanceState(outState)
//    }
}