package com.star.theBigDipper.service

import androidLib.AndroidLib

object WalletWrapper {
    var MainAddress = ""
    var SubAddress = ""
    var EthBalance = 0.0
    var HopBalance = 0.0
    var Approved = 0.0
    fun walletJsonData(): String {
        return AndroidLib.walletJson()
    }

    fun closeWallet() {
        AndroidLib.closeWallet()
    }

    val isOpen: Boolean
        get() = AndroidLib.isWalletOpen()
}