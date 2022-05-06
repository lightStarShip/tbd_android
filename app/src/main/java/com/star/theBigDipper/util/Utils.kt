package com.star.theBigDipper.util

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.WriterException
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.callback.AlertDialogOkCallBack
import com.star.theBigDipper.ui.activity.MineMachineListActivity.Companion.sMinerBeans
import com.star.theBigDipper.ui.activity.MinePoolListActivity.Companion.sMinePoolBeans
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.InputInfo
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.utils.AppManager
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.jvm.Throws

object Utils {
    const val RC_LOCAL_MEMORY_PERM = 123
    const val RC_CAMERA_PERM = 124
    const val RC_SELECT_FROM_GALLERY = 125
    const val COIN_DECIMAL = 1e18
    private val appContext: Context = HopApplication.instance
    private val sharedPref = appContext.getSharedPreferences("pirateManager", Context.MODE_PRIVATE)

    fun convertCoin(coinV: Double): String {
        return String.format(Locale.CHINA, "%.4f ", coinV / COIN_DECIMAL)
    }

    fun convertBandWidth(packetsV: Double): String {
        return when {
            packetsV > 1e12 -> {
                String.format(Locale.CHINA, "%.2f T", packetsV / 1e12)
            }
            packetsV > 1e9 -> {
                String.format(Locale.CHINA, "%.2f G", packetsV / 1e9)
            }
            packetsV > 1e6 -> {
                String.format(Locale.CHINA, "%.2f M", packetsV / 1e6)
            }
            packetsV > 1e3 -> {
                String.format(Locale.CHINA, "%.2f K", packetsV / 1e3)
            }
            else -> {
                String.format(Locale.CHINA, "%.2f B", packetsV)
            }
        }
    }

    fun saveData(key: String?, value: String?) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key: String, defaultVal: String): String {
        return sharedPref.getString(key, defaultVal).toString()
    }

    fun getInt(key: String, defaultVal: Int): Int {
        return sharedPref.getInt(key, defaultVal)
    }

    fun saveInt(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun saveBoolean(key: String?, value: Boolean?) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value!!)
        editor.commit()
    }

    fun getBoolean(key: String?, defaultVal: Boolean?): Boolean {
        return sharedPref.getBoolean(key, defaultVal!!)
    }

    fun clearSharedPref() {
        val edit = sharedPref.edit()
        edit.clear()
        edit.commit()
    }

    fun clearAllData() {
        sMinerBeans = null
        sMinePoolBeans = null
        clearSharedPref()
    }

    fun showOkAlert(context: AppCompatActivity, tittleID: Int, messageId: Int, callBack: AlertDialogOkCallBack?) {
        MessageDialog.show(context, context.getString(tittleID), context.getString(messageId), context.getString(R.string.sure)).onOkButtonClickListener = OnDialogButtonClickListener { baseDialog, v ->
            callBack?.onClickOkButton("")
            false
        }
    }

    fun showOkOrCancelAlert(context: AppCompatActivity, tittleID: Int, messageId: Int, callBack: AlertDialogOkCallBack?) {
        MessageDialog.show(context, context.getString(tittleID), context.getString(messageId), context.getString(R.string.sure), context.getString(R.string.cancel)).setOnOkButtonClickListener { baseDialog, v ->
            callBack?.onClickOkButton("")
            false
        }.onCancelButtonClickListener = OnDialogButtonClickListener { baseDialog, v ->
            callBack?.onClickCancelButton()
            false
        }
    }


    fun showPassword(context: AppCompatActivity, callBack: AlertDialogOkCallBack) {
        InputDialog.build(context) //.setButtonTextInfo(new TextInfo().setFontColor(Color.GREEN))
                .setTitle(R.string.tips).setMessage(R.string.create_account_enter_password).setOkButton(R.string.sure) { _, _, inputStr ->
                    callBack.onClickOkButton(inputStr)
                    false
                }.setCancelButton(R.string.cancel).setHintText(context.getString(R.string.create_account_enter_ethereum_password)).setInputInfo(InputInfo().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)).setCancelable(false).show()
    }

    fun copyToMemory(context: Context, src: String?) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("pirate memory string", src)
        clipboard.setPrimaryClip(clip)
    }

    fun QRStr2Bitmap(data: String?): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        try {
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 600, 600)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(Exception::class)
    fun parseQRCodeFile(uri: Uri, cr: ContentResolver): String {
        val bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri))
        return parseQRcodeFromBitmap(bitmap)
    }

    @Throws(Exception::class)
    private fun parseQRcodeFromBitmap(bitmap: Bitmap): String {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val bb = BinaryBitmap(HybridBinarizer(source))
        val hints: MutableMap<DecodeHintType, Any?> = LinkedHashMap()
        val reader = QRCodeReader()
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        hints[DecodeHintType.TRY_HARDER] = true
        hints[DecodeHintType.POSSIBLE_FORMATS] = BarcodeFormat.QR_CODE
        val r = reader.decode(bb, hints)
        return r.text
    }

    fun hasStoragePermission(ctx: Context?): Boolean {
        return EasyPermissions.hasPermissions(ctx!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun checkStorage(ctx: Activity): Boolean {
        if (!EasyPermissions.hasPermissions(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(ctx, ctx.getString(R.string.rationale_extra_write), RC_LOCAL_MEMORY_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return false
        }
        return true
    }

    fun hasCameraPermission(ctx: Context?): Boolean {
        return EasyPermissions.hasPermissions(ctx!!, Manifest.permission.CAMERA)
    }

    fun checkCamera(ctx: Activity): Boolean {
        if (!hasCameraPermission(ctx)) {
            EasyPermissions.requestPermissions(ctx, ctx.getString(R.string.camera), RC_CAMERA_PERM, Manifest.permission.CAMERA)
            return false
        }
        return true
    }

    fun getBaseDir(context: Context): String {
        return context.filesDir.absolutePath
    }

    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }

    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return "V" + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "V0.0"
    }

    fun isNetworkAvailable(activity: AppCompatActivity): Boolean {
        val context = activity.applicationContext
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager == null) {
            return false
        } else {
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo.size > 0) {
                for (i in networkInfo.indices) {
                    if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isServiceWork(mContext: Context, serviceName: String): Boolean {
        var isWork = false
        val myAM = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myList = myAM.getRunningServices(100)
        if (myList.size <= 0) {
            return false
        }
        for (i in myList.indices) {
            val mName = myList[i].service.className.toString()
            if (mName == serviceName) {
                isWork = true
                break
            }
        }
        return isWork
    }

    fun checkVPN(): Boolean {
        val networkList: MutableList<String> = ArrayList()
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp) {
                    networkList.add(networkInterface.name)
                }
            }
        } catch (ex: Exception) {
            return false
        }
        return networkList.contains("tun0") || networkList.contains("ppp0")
    }

    private const val MIN_CLICK_DELAY_TIME = 500
    private var lastClickTime: Long = 0
    val isFastClick: Boolean
        get() {
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
                lastClickTime = curClickTime
                return true
            }
            return false
        }

    fun openAppDownloadPage(context: Context,downloadUrl:String) {
        val it = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
        context.startActivity(it)
    }

    fun isIpAddress(address: String?): Boolean {
        val regex = "^([1-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))(\\.([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$"
        val p = Pattern.compile(regex)
        val m = p.matcher(address)
        return m.matches()
    }

    fun clearLocalData(context: Context) {
        deleteFile(File(getBaseDir(context), "data"))
    }

    fun deleteFile(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        val files = file.listFiles()
        for (f in files) {
            val name = file.name
            println(name)
            if (f.isDirectory) {
                deleteFile(f)
            } else {
                f.delete()
            }
        }
        file.delete()
    }

    fun formatText(start: String, end: String): SpannableString {
        val spannableString = SpannableString(start + end)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#aaffffff"))
        val relativeSizeSpan = RelativeSizeSpan(0.8f)
        spannableString.setSpan(colorSpan, start.length - 1, (start + end).length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(relativeSizeSpan, start.length - 1, (start + end).length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    fun showExitAppDialog(activity: AppCompatActivity, msgId: Int) {
        val instance = HopApplication.instance
        MessageDialog.show(activity, instance.getString(R.string.tips), instance.getString(msgId), instance.getString(R.string.sure)).setOnOkButtonClickListener { baseDialog, v ->
            AppManager.removeAllActivity()
            AppManager.killAppProcess(activity)
            return@setOnOkButtonClickListener false
        }
    }

    fun showSyncErrorDialog(activity: AppCompatActivity, msgId: Int,onOkButtonClickListener:OnDialogButtonClickListener ) {
        val instance = HopApplication.instance
        MessageDialog.show(activity, instance.getString(R.string.tips), instance.getString(msgId), instance.getString(R.string.sure)).setOnOkButtonClickListener(onOkButtonClickListener)
    }

    fun format(number:Double):String{
        return DecimalFormat("0.00").format(number)
    }


}