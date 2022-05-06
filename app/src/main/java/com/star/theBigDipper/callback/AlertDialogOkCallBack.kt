package com.star.theBigDipper.callback

abstract class AlertDialogOkCallBack {
    abstract fun onClickOkButton(parameter: String)
    fun onClickCancelButton() {}
}