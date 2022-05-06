package com.star.theBigDipper

import android.util.Log
import com.kongzue.dialog.util.DialogSettings
import com.nbs.android.lib.base.BaseApplication
import com.star.theBigDipper.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.plugins.RxJavaPlugins
import kotlin.properties.Delegates

class HopApplication : BaseApplication() {
    var isRunning = false
    var initServiceSuccess= false
    override fun onCreate() {
        super.onCreate()
        instance = this
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS
        DialogSettings.modalDialog = true
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(applicationContext, "cfdc2c8a96", false)
        }
        RxJavaPlugins.setErrorHandler { throwable -> Log.d(TAG, "accept: " + throwable.message) }
    }

    companion object {
        private const val TAG = "HopApplication"
        var instance: HopApplication by Delegates.notNull()
    }
}