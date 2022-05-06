package com.star.theBigDipper.util

import android.content.Context
import java.io.File

object AccountUtils {

    fun loadWallet(context: Context): String {
        val file = File(Utils.getBaseDir(context) + "/wallet.json")
        if (!file.exists()) {
            return ""
        }

        return file.readText()
    }
}