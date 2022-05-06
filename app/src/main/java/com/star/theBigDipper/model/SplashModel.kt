package com.star.theBigDipper.model

import android.content.Context
import com.google.gson.Gson
import com.star.theBigDipper.model.bean.AppVersionBean
import com.star.theBigDipper.util.AccountUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 11:10 AM
 */
class SplashModel : InitServiceModel() {
    suspend fun loadWallet(context: Context): String {
        return withContext(Dispatchers.IO) {
            AccountUtils.loadWallet(context)
        }
    }


    suspend fun checkVersion(): AppVersionBean? {
        return withContext(Dispatchers.IO) {
            val url = URL("https://www.fastmock.site/mock/c30676a124bcbd7bfa4d0722a374f899/pirate/api/check_new_version")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connectTimeout = 2000
            urlConnection.readTimeout = 2000
            urlConnection.connect()
            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var sTempOneLine: String?
                while (bufferedReader.readLine().also { sTempOneLine = it } != null) {
                    stringBuilder.append(sTempOneLine)
                }
                val versionBean = Gson().fromJson(stringBuilder.toString(), AppVersionBean::class.java)
                versionBean
            } else {
                null
            }
        }


    }
}