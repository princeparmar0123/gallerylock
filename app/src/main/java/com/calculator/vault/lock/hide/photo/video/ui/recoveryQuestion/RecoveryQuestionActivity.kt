package com.calculator.vault.lock.hide.photo.video.ui.recoveryQuestion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.calculator.vault.lock.hide.photo.video.DrawerActivity
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityRecoveryQuestionBinding
import com.calculator.vault.lock.hide.photo.video.ui.home.HomeActivity

class RecoveryQuestionActivity : BaseActivity<ActivityRecoveryQuestionBinding>(R.layout.activity_recovery_question), View.OnClickListener {

    //private val spinnerQuestion: Spinner? = null
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        onClickListener()
        binding.run {
            type = intent.getIntExtra("type", 0)
            when (type) {
                0 -> {
                    simple.visibility = View.VISIBLE
                    recover.visibility = View.GONE
                }
                1 -> {
                    simple.visibility = View.VISIBLE
                    recover.visibility = View.GONE
                    spinnerQuestion.setSelection(pref.setQuestion)
                    questionAns.setText(pref.setAnswer)
                    save.text = "Update"
                }
                2 -> {
                    title = "Recover Password"
                    simple.visibility = View.GONE
                    recover.visibility = View.VISIBLE
                    recoverQuestion.text = Constant.recovery_question[pref.setQuestion]
                    save.text = "Reset Password"
                }
            }
        }

    }


    private fun initAdapter() {
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.item_spinner, Constant.recovery_question)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerQuestion.adapter = arrayAdapter

    }


    private fun onClickListener(){
        binding.run {
            save.setOnClickListener(this@RecoveryQuestionActivity)
            ivBack.setOnClickListener(this@RecoveryQuestionActivity)

        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.save->{
                if (type == 0) {
                    if (binding.questionAns.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        pref.question=true
                        pref.setQuestion = binding.spinnerQuestion.selectedItemPosition ?:0
                        pref.setAnswer = binding.questionAns.text.toString().trim{ it <= ' '}
                        val intent = Intent(this, DrawerActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Select Question and Write Answer...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (type == 1) {
                    if (binding.questionAns.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        pref.question=true
                        pref.setQuestion = binding.spinnerQuestion.selectedItemPosition ?:0
                        pref.setAnswer = binding.questionAns.text.toString().trim{ it <= ' '}
                        finish()
                    } else {
                        Toast.makeText(this, "Select Question and Write Answer...", Toast.LENGTH_SHORT).show()
                    }
                } else if (type == 2) {
                    if (binding.recoverAns.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        if (binding.recoverAns.text.toString().trim { it <= ' ' } == pref.setAnswer) {
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("type", 2)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Enter Correct Answer...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        binding.recoverAns.error = "Enter Answer"
                    }
                }
            }
            R.id.ivBack->{
                onBackPressed()
            }
        }
    }
}