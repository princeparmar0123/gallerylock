package com.calculator.vault.lock.hide.photo.video

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityDrawerBinding
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityIntroBinding
import com.calculator.vault.lock.hide.photo.video.ui.MainActivity
import com.calculator.vault.lock.hide.photo.video.ui.home.HomeActivity

class IntroActivity : BaseActivity<ActivityIntroBinding>(R.layout.activity_intro) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgAcceptContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}