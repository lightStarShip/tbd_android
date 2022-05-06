package com.star.theBigDipper.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.star.theBigDipper.R

class PayPasswordDialog(context: Context, private val mRechargeFlowCallBack: PasswordCallBack) : Dialog(context, R.style.PayPasswordDialog) {
    private lateinit var mPasswordEt: EditText

    interface PasswordCallBack {
        fun callBack(password: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pay_password)
        mPasswordEt = findViewById(R.id.key_word_et)
        findViewById<View>(R.id.sure_tv).setOnClickListener {
            mRechargeFlowCallBack.callBack(mPasswordEt.text.toString().trim { it <= ' ' })
            dismiss()
        }
    }

    override fun show() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        super.show()
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.attributes = layoutParams
        Handler(Looper.getMainLooper()).postDelayed({
            mPasswordEt.isFocusable = true
            mPasswordEt.isFocusableInTouchMode = true
            mPasswordEt.requestFocus()
            val inputManager = mPasswordEt.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(mPasswordEt, 0)
        }, 200)
    }

}