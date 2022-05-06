package com.star.theBigDipper.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.nbs.android.lib.utils.dp
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object BitmapUtils {
    private const val DCIM = "/DCIM/Camera/"

    fun stringToQRBitmap(data: String): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400.dp.toInt(), 400.dp.toInt())
    }

    fun saveBitmapToAlbum(context: Context, bitmap: Bitmap, bitName: String): Boolean {
        val fileName: String
        var file: File
        val brand = Build.BRAND

        fileName = Environment.getExternalStorageDirectory().path.toString() + DCIM + bitName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveSignImage(context, bitName, bitmap)
            return true
        }

        Log.v("saveBitmap brand", "" + brand)
        val dirPath = Environment.getExternalStorageDirectory().path.toString() + DCIM
        file = File(dirPath)

        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (!mkdirs) {
                Log.e("makeFire", "file.mkdirs error")
                return false
            }
        }

        file = File(fileName)
        val out: FileOutputStream
        try {
            out = FileOutputStream(file)
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush()
                out.close()
                // 插入图库
                if (Build.VERSION.SDK_INT >= 29) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath())
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    val uri: Uri? = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } else {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null)
                }
            }
        } catch (e: FileNotFoundException) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.message.toString())
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return false
        } catch (e: Exception) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return false

        }
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$fileName")))

        return true
    }

    private fun saveSignImage(context: Context, fileName: String?, bitmap: Bitmap) {
        try {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            //兼容Android Q和以下版本
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)

            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            val uri: Uri? = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                val outputStream: OutputStream? = context.getContentResolver().openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        } catch (e: Exception) {
            Log.e("Exception", "Exception:" + e.message.toString())
        }
    }
}