package com.calculator.vault.lock.hide.photo.video.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import com.calculator.vault.lock.hide.photo.video.DrawerActivity
import com.calculator.vault.lock.hide.photo.video.R
import com.calculator.vault.lock.hide.photo.video.common.base.BaseActivity
import com.calculator.vault.lock.hide.photo.video.common.utils.Constant
import com.calculator.vault.lock.hide.photo.video.common.utils.Constants
import com.calculator.vault.lock.hide.photo.video.common.utils.CreateFile
import com.calculator.vault.lock.hide.photo.video.databinding.ActivityMainBinding
import com.calculator.vault.lock.hide.photo.video.databinding.DialogHowitworkBinding
import com.calculator.vault.lock.hide.photo.video.ui.home.HomeActivity
import com.calculator.vault.lock.hide.photo.video.ui.recoveryQuestion.RecoveryQuestionActivity
import java.io.File
import java.math.BigDecimal
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main),
    View.OnClickListener {

    //    private int openParenthesis = 0;
    private var dotUsed = false

    private var equalClicked = false
    private var lastExpression = ""
    var password: String? = null
    var scriptEngine: ScriptEngine? = null
    var type = true
    private var howItWorkDialog: Dialog? = null
    private  var temp_type = 0
    private  var temp = 0

    var activityResultLauncherCreateHiddenFolder: ActivityResultLauncher<Intent>? = null
    var activityResultLauncherAccessHiddenFolder: ActivityResultLauncher<Intent>? = null

    var CREATE_HIDDEN_FOLDER = 123
    var ACCESS_HIDDEN_FOLDER = 12

    companion object {
        private const val EXCEPTION = -1
        private const val IS_NUMBER = 0
        private const val IS_OPERAND = 1
        private const val IS_OPEN_PARENTHESIS = 2
        private const val IS_CLOSE_PARENTHESIS = 3
        private const val IS_DOT = 4
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // Amplitude.getInstance().logEvent("Create Calculator_Activity")
       // val intent = intent
       // val uri = intent.data
        scriptEngine = ScriptEngineManager().getEngineByName("js")
        createHiddenFolder()
        if (pref.Pass?:false) {
            type = true
        } else {
            initDialog()
        }
        temp_type = intent.getIntExtra("type", 0)
        temp = temp_type
        if (temp_type == 1) {
            type = false
            Toast.makeText(this, "Enter Old Password...!", Toast.LENGTH_SHORT).show()
        } else if (temp_type == 2) {
            type = false
            temp_type = 0
            Toast.makeText(this, "Enter New Password...!", Toast.LENGTH_SHORT).show()
        }
//        if (uri != null) {
//            Toast.makeText(this, "" + uri, Toast.LENGTH_SHORT).show()
//        }

        initListener()
    }

    private fun initListener() {
        binding.cal.number0Btn.setOnClickListener(this)
        binding.cal.number1Btn.setOnClickListener(this)
        binding.cal.number2Btn.setOnClickListener(this)
        binding.cal.number3Btn.setOnClickListener(this)
        binding.cal.number4Btn.setOnClickListener(this)
        binding.cal.number5Btn.setOnClickListener(this)
        binding.cal.number6Btn.setOnClickListener(this)
        binding.cal.number7Btn.setOnClickListener(this)
        binding.cal.number8Btn.setOnClickListener(this)
        binding.cal.number9Btn.setOnClickListener(this)
        binding.cal.plusMinus.setOnClickListener(this)
        binding.cal.clearBtn.setOnClickListener(this)
        binding.cal.persantageBtn.setOnClickListener(this)
        binding.cal.divideBtn.setOnClickListener(this)
        binding.cal.minusBtn.setOnClickListener(this)
        binding.cal.multiplyBtn.setOnClickListener(this)
        binding.cal.plusBtn.setOnClickListener(this)
        binding.cal.equalBtn.setOnClickListener(this)
        binding.cal.dotBtn.setOnClickListener(this)

        if (pref.Pass) {
            binding.reset.visibility = View.VISIBLE
        } else {
            binding.reset.visibility = View.GONE
        }
            binding.reset.setOnClickListener { v: View? ->
                val intent = Intent(this, RecoveryQuestionActivity::class.java)
                intent.putExtra("type", 2)
                startActivity(intent)
            }

        binding.cal.clearBtn.setOnLongClickListener { v: View? ->
            binding.inputText.setText("")
            binding.outputText.setText("")
            dotUsed = false
            equalClicked = false
            password = null
            false
        }

    }

    private fun initDialog() {
        howItWorkDialog = Dialog(this, R.style.WideDialog)

        howItWorkDialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        howItWorkDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        howItWorkDialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
        howItWorkDialog?.window?.setDimAmount(0.80f)

        val binding: DialogHowitworkBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_howitwork,
                null,
                false
            )

        howItWorkDialog?.setContentView(binding.root)

        binding.cancle.setOnClickListener { v: View? ->
            howItWorkDialog?.dismiss()
            type = true
        }
            binding.close.setOnClickListener { v: View? ->
                howItWorkDialog?.dismiss()
            type = true
        }
                binding.ok.setOnClickListener { v: View? ->
            howItWorkDialog?.dismiss()
            type = false
        }
        howItWorkDialog?.show()
        howItWorkDialog?.setCancelable(false)
    }


    private fun calculate(input: String) {
        var input = input
        for (i in 0 until input.length) {
            val first = input[0]
            if (first.code == 48) {
                input = input.substring(1)
            }
        }
        var result = ""
        try {
            var temp = input
            if (equalClicked) {
                temp = binding.outputText.text.toString() + lastExpression
            } else {
                saveLastExpression(input)
            }
            result = scriptEngine?.eval(
                temp.replace("%".toRegex(), "/100").replace("x".toRegex(), "*")
                    .replace("[^\\x00-\\x7F]".toRegex(), "/")
            ).toString()
            val decimal = BigDecimal(result)
            result = decimal.setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString()
            equalClicked = true
        } catch (e: Exception) {
//            Toast.makeText(getApplicationContext(), "Wrong Format", Toast.LENGTH_SHORT).show();
            return
        }
        if (result == "Infinity") {
            Toast.makeText(
                applicationContext,
                "Division by zero is not allowed",
                Toast.LENGTH_SHORT
            ).show()
            binding.outputText.setText(input)
        } else if (result.contains(".")) {
            result = result.replace("\\.?0*$".toRegex(), "")
            binding.outputText.setText("=$result")
        }
    }

    private fun saveLastExpression(input: String) {
        val lastOfExpression = input[input.length - 1].toString() + ""
        if (input.length > 1) {
            if (lastOfExpression == ")") {
                lastExpression = ")"
                var numberOfCloseParenthesis = 1
                for (i in input.length - 2 downTo 0) {
                    if (numberOfCloseParenthesis > 0) {
                        val last = input[i].toString() + ""
                        if (last == ")") {
                            numberOfCloseParenthesis++
                        } else if (last == "(") {
                            numberOfCloseParenthesis--
                        }
                        lastExpression = last + lastExpression
                    } else if (defineLastCharacter(input[i].toString() + "") == IS_OPERAND) {
                        lastExpression = input[i].toString() + lastExpression
                        break
                    } else {
                        lastExpression = ""
                    }
                }
            } else if (defineLastCharacter(lastOfExpression + "") == IS_NUMBER) {
                lastExpression = lastOfExpression
                for (i in input.length - 2 downTo 0) {
                    val last = input[i].toString() + ""
                    if (defineLastCharacter(last) == IS_NUMBER || defineLastCharacter(
                            last
                        ) == IS_DOT
                    ) {
                        lastExpression = last + lastExpression
                    } else if (defineLastCharacter(last) == IS_OPERAND) {
                        lastExpression = last + lastExpression
                        break
                    }
                    if (i == 0) {
                        lastExpression = ""
                    }
                }
            }
        }
    }


    private fun defineLastCharacter(lastCharacter: String): Int {
        try {
            lastCharacter.toInt()
            return IS_NUMBER
        } catch (e: NumberFormatException) {
        }
        if (lastCharacter == "+" || lastCharacter == "-" || lastCharacter == "x" || lastCharacter == "\u00F7" || lastCharacter == "%") return IS_OPERAND
        if (lastCharacter == "(") return IS_OPEN_PARENTHESIS
        if (lastCharacter == ")") return IS_CLOSE_PARENTHESIS
        return if (lastCharacter == ".") IS_DOT else -1
    }

    private fun addNumber(number: String): Boolean {
        binding.run {
            var done = false
            val operationLength: Int = inputText.text.length
            if (operationLength > 0) {
                val lastCharacter: String =
                    inputText.text.get(operationLength - 1).toString() + ""
                val lastCharacterState = defineLastCharacter(lastCharacter)
                if (operationLength == 1 && lastCharacterState == IS_NUMBER && lastCharacter == "0") {
                    inputText.setText(inputText.text.toString() + number)
                    done = true
                } else if (lastCharacterState == IS_OPEN_PARENTHESIS) {
                    inputText.setText(inputText.text.toString() + number)
                    done = true
                } else if (lastCharacterState == IS_CLOSE_PARENTHESIS || lastCharacter == "%") {
                    inputText.setText(inputText.text.toString() + "x" + number)
                    done = true
                } else if (lastCharacterState == IS_NUMBER || lastCharacterState == IS_OPERAND || lastCharacterState == IS_DOT) {
                    inputText.setText(inputText.text.toString() + number)
                    done = true
                }
            } else {
                inputText.setText(inputText.text.toString() + number)
                done = true
            }
            return done
        }
    }


    override fun onClick(v: View?) {
        if(type) {
            when (v?.id) {
                R.id.number0_btn -> {
                    if (addNumber("0"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number1_btn -> {
                    if (addNumber("1"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number2_btn -> {
                    if (addNumber("2"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number3_btn -> {
                    if (addNumber("3"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number4_btn -> {
                    if (addNumber("4"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number5_btn -> {
                    if (addNumber("5"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number6_btn -> {
                    if (addNumber("6"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number7_btn -> {
                    if (addNumber("7"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number8_btn -> {
                    if (addNumber("8"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.number9_btn -> {
                    if (addNumber("9"))
                        equalClicked = false
                    calculate(binding.inputText.text.toString())
                }
                R.id.clear_btn -> {
                    dotUsed = if (binding.inputText.text.toString().trim { it <= ' ' }.length > 0) {
                        binding.inputText.setText(
                            binding.inputText.text.toString().trim { it <= ' ' }
                                .substring(
                                    0,
                                    binding.inputText.text.toString().trim { it <= ' ' }.length - 1
                                )
                        )
                        calculate(binding.inputText.text.toString())
                        binding.inputText.text.toString().contains(".")
                    } else {
                        binding.inputText.setText("")
                        binding.outputText.setText("")
                        false
                    }
                    equalClicked = false
                }
                R.id.multiply_btn -> {
                    if (addOperand("x")) equalClicked = false
                }
                R.id.plus_minus -> {
                    val input = StringBuilder()
                    if (binding.inputText.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                        input.append(binding.inputText.text.toString().trim { it <= ' ' })
                        val ch: String =
                            binding.inputText.text.toString().trim { it <= ' ' }.get(0).toString()
                        if (ch == "-") {
                            input.deleteCharAt(0)
                        } else {
                            input.insert(0, "-")
                        }
                    }
                    binding.inputText.setText(input.toString())
                    input.setLength(0)
                }
                R.id.persantage_btn -> {
                    if (addOperand("%"))
                        equalClicked = false
                }
                R.id.divide_btn -> {
                    if (addOperand("\u00F7")) equalClicked = false
                }
                R.id.minus_btn -> {
                    if (addOperand("-")) equalClicked = false
                }
                R.id.equal_btn -> {
                    val input_data: String =
                        binding.inputText.text.toString().trim { it <= ' ' }
                    val match = arrayOf("(", ")", "+", "-", "x", "÷", "%", ".")
                    var check = false
                    for (i in match.indices) {
                        if (input_data.contains(match[i])) {
                            check = true
                            break
                        }
                    }
                    if (check) {
                        if (equalClicked) {
                            equalClicked = false
                            binding.inputText.startAnimation(
                                AnimationUtils.loadAnimation(
                                    this,
                                    R.anim.slide_up
                                )
                            )
                            Handler().postDelayed({
                                val out: String =
                                    binding.outputText.text.toString().trim { it <= ' ' }
                                binding.inputText.setText(out.replace("=", ""))
                                binding.outputText.setText("")
                            }, 1000)
                        }
                    } else {
                        if (input_data == pref.password) {
                            binding.inputText.setText("")
                            binding.outputText.setText("")
                            if (pref.question) {
//                                val intent = Intent(this, HomeActivity::class.java)
                                val intent = Intent(this, DrawerActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, RecoveryQuestionActivity::class.java)
                                intent.putExtra("type", 0)
                                startActivity(intent)
                            }
                        } else {
                            if (VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                !Settings.canDrawOverlays(this)
                            ) {
                            } else {
                                Constant.StartCameraService(this)
                            }
                        }
                    }
                }
                R.id.dot_btn -> {
                    if (addDot()) equalClicked = false
                }
                R.id.plus_btn -> {
                    if (addOperand("+"))
                        equalClicked = false
                }

            }
        }else{
          when(v?.id){
              R.id.equal_btn->{
                  if (temp_type == 0) {
                      if (binding.inputText.text.toString().trim { it <= ' ' }.length == 4) {
                          if (password == null) {
                              password = binding.inputText.text.toString().trim { it <= ' ' }
                              binding.outputText.setText(password)
                              binding.inputText.setText("")
                              Toast.makeText(this, "Re-enter Password...", Toast.LENGTH_SHORT)
                                  .show()
                          } else {
                              if (binding.inputText.text.toString().trim { it <= ' ' } == password) {
                                  if (temp == 0) {
                                      val intent = Intent(
                                          this, RecoveryQuestionActivity::class.java
                                      )
                                      intent.putExtra("type", 0)
                                      startActivity(intent)
                                  } else if (temp == 1) {
                                      onBackPressed()
                                      //showInter() ads
                                  } else if (temp == 2) {
                                      //showInter ads
                                  }
                                  binding.inputText.setText("")
                                  binding.outputText.setText("")
                                  type = true
                                  pref.Pass = true
                                  pref.password = password
                                  Toast.makeText(
                                      this,
                                      "successfully set Password...",
                                      Toast.LENGTH_SHORT
                                  ).show()
                              } else {
                                  Toast.makeText(this, "Same Password enter...", Toast.LENGTH_SHORT)
                                      .show()
                              }
                          }
                      }
                  } else {
                      if (binding.inputText.text.toString().trim { it <= ' ' }.length == 4) {
                          if (password == null) {
                              password = binding.inputText.text.toString().trim { it <= ' ' }
                              if (password == pref.password) {
                                  temp_type = 0
                                  password = null
                                  binding.inputText.setText("")
                                  binding.outputText.setText("")
                                  type = false
                                  Toast.makeText(this, "Enter New Password...", Toast.LENGTH_SHORT)
                                      .show()
                              } else {
                                  password = null
                                  Toast.makeText(
                                      this,
                                      "Enter Correct Old Password",
                                      Toast.LENGTH_SHORT
                                  ).show()
                              }
                          }
                      }
                  }
              }
              R.id.clear_btn->{
                  if (binding.inputText.getText().toString().trim { it <= ' ' }.length > 0) {
                      binding.inputText.setText(
                          binding.inputText.getText().toString().trim { it <= ' ' }
                              .substring(
                                  0,
                                  binding.inputText.getText().toString().trim { it <= ' ' }.length - 1
                              )
                      )
                  }
              }
          }
            if (binding.inputText.text.toString().trim { it <= ' ' }.length < 4) {
                click(v)
            }
        }
    }


    private fun click(v: View?) {
        when (v?.id) {
            R.id.number0_btn -> if (addNumber("0")) equalClicked = false
            R.id.number1_btn -> if (addNumber("1")) equalClicked = false
            R.id.number2_btn -> if (addNumber("2")) equalClicked = false
            R.id.number3_btn -> if (addNumber("3")) equalClicked = false
            R.id.number4_btn -> if (addNumber("4")) equalClicked = false
            R.id.number5_btn -> if (addNumber("5")) equalClicked = false
            R.id.number6_btn -> if (addNumber("6")) equalClicked = false
            R.id.number7_btn -> if (addNumber("7")) equalClicked = false
            R.id.number8_btn -> if (addNumber("8")) equalClicked = false
            R.id.number9_btn -> if (addNumber("9")) equalClicked = false
        }
    }

    private fun addDot(): Boolean {
        var done = false
        if (binding.inputText.text.isEmpty()) {
            binding.inputText.setText("0.")
            dotUsed = true
            done = true
        } else if (dotUsed == true) {
        } else if (defineLastCharacter(
                binding.inputText.text.get(binding.inputText.text.length - 1).toString() + ""
            ) == IS_OPERAND
        ) {
            binding.inputText.setText(binding.inputText.text.toString() + "0.")
            done = true
            dotUsed = true
        } else if (defineLastCharacter(binding.inputText.text.get(binding.inputText.text.length - 1).toString() + "") == IS_NUMBER) {
            binding.inputText.setText(binding.inputText.text.toString() + ".")
            done = true
            dotUsed = true
        }
        return done
    }

    private fun addOperand(operand: String): Boolean {
        var done = false
        val operationLength: Int = binding.inputText.text.length
        if (operationLength > 0) {
            val lastInput: String = binding.inputText.text.get(operationLength - 1).toString() + ""
            if (lastInput == "+" || lastInput == "-" || lastInput == "*" || lastInput == "\u00F7" || lastInput == "%") {
                Toast.makeText(applicationContext, "Wrong format", Toast.LENGTH_LONG).show()
            } else if (operand == "%" && defineLastCharacter(lastInput) == IS_NUMBER) {
                binding.inputText.setText(binding.inputText.text.toString() + operand)
                dotUsed = false
                equalClicked = false
                lastExpression = ""
                done = true
            } else if (operand != "%") {
                binding.inputText.setText(binding.inputText.text.toString() + operand)
                dotUsed = false
                equalClicked = false
                lastExpression = ""
                done = true
            }
        }
        return done
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
            //            startActivityForResult(intent, requestCode);
            if (requestCode == CREATE_HIDDEN_FOLDER) {
                activityResultLauncherCreateHiddenFolder?.launch(intent)
            } else {
                activityResultLauncherAccessHiddenFolder?.launch(intent)
            }
        }
    }

    private fun createHiddenFolder() {
        activityResultLauncherCreateHiddenFolder = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            Log.e("ONRESULTTT", "RESULTTT")
            if (!pref.hiddenPermission) {
                askPermissionForFragment(
                    "Pictures%2F.Calculator Lock",
                    ACCESS_HIDDEN_FOLDER
                )
            } else {
                val destinationPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.toString() + File.separator +
                            ".Calculator Lock"
                val destination = File(destinationPath)
                val rootUri = result.data?.data
                val contentResolver = contentResolver
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
                if (destination.exists()) {
                    Constant.StartCameraService(this)
                } else {
                    askPermissionForFragment("Pictures", CREATE_HIDDEN_FOLDER)
                }
            }
        }
        activityResultLauncherAccessHiddenFolder = registerForActivityResult(
            StartActivityForResult()
        ) { o ->
            Log.e("ONRESULTTT", "RESULTTssssT")
            pref.hiddenPermission = true
            val rootUri1 = o.data!!.data
            pref.hiddenUri = rootUri1.toString()
            val destinationPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator +
                        ".Calculator Lock"
            val destination = File(destinationPath)
            Constant.StartCameraService(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constants.isSplashScreen = true
    }

    override fun onResume() {
        super.onResume()
        if (pref.Pass) {
            binding.reset.visibility = View.VISIBLE
        } else {
            binding.reset.visibility = View.GONE
        }
    }


}