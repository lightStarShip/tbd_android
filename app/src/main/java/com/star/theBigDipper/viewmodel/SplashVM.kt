package com.star.theBigDipper.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.model.SplashModel
import com.star.theBigDipper.model.bean.AppVersionBean
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.CreateAccountActivity
import com.star.theBigDipper.ui.activity.MainActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SplashVM : BaseViewModel() {
    val TAG = "SplashVM"
    val model = SplashModel()
    val delayLoadWalletEvent = SingleLiveEvent<AppVersionBean?>()
    val initServiceFailEvent = SingleLiveEvent<Boolean>()

    fun loadWallet() {
        viewModelScope.launch {
            runCatching {
                model.loadWallet(HopApplication.instance.applicationContext)
            }.onSuccess {
                loadWalletSuccess(it)
            }.onFailure {
                loadWalletFailure()
            }

        }

    }

    fun initService() {
        model.initService(HopApplication.instance.applicationContext).subscribe(object : SingleObserver<Any> {
            override fun onSuccess(t: Any) {
                initServiceSuccess()
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: " + e.message)
                initServiceFailure()
            }
        })
    }

    private fun initServiceSuccess() {
        startActivityAndFinish(MainActivity::class.java)
    }

    private fun initServiceFailure() {
        initServiceFailEvent.postValue(true)
    }

    private fun loadWalletFailure() {
        startActivityAndFinish(CreateAccountActivity::class.java)
    }

    private fun loadWalletSuccess(walletJson: String) {
        if (TextUtils.isEmpty(walletJson)) {
            startActivityAndFinish(CreateAccountActivity::class.java)
        } else {
            WalletWrapper.MainAddress = JSONObject(walletJson).optString("mainAddress")
            initService()
        }

    }


    fun checkVersion() {
        viewModelScope.launch {
            kotlin.runCatching {
                model.checkVersion()
            }.onSuccess {
                checkVersionSuccess(it)
            }.onFailure {
                checkVersionFailure()
            }
        }
    }

    private fun checkVersionFailure() {
        delayLoadWalletEvent.postValue(AppVersionBean())
    }

    private fun checkVersionSuccess(it: AppVersionBean?) {
        delayLoadWalletEvent.postValue(it)
    }


}