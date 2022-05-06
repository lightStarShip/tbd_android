package com.star.theBigDipper.viewmodel

import androidLib.AndroidLib
import com.star.theBigDipper.Constants
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.model.TabHomeModel
import com.star.theBigDipper.model.bean.UserPoolData
import com.star.theBigDipper.service.SysConf
import com.star.theBigDipper.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.command.BindingConsumer
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabHomeVM : BaseViewModel() {
    val model = TabHomeModel()
    val selectPoolLiveEvent = SingleLiveEvent<Boolean>()
    val selectMinerLiveEvent = SingleLiveEvent<Boolean>()
    val changeVPNStatusEvent = SingleLiveEvent<Boolean>()
    val getPoolSuccessEvent = SingleLiveEvent<Boolean>()
    val openWalletSuccessEvent = SingleLiveEvent<Boolean>()
    val showSwitchNetEvent = SingleLiveEvent<Boolean>()
    val exitApp = SingleLiveEvent<Int>()
    val changeModelCommand = BindingCommand(null, object : BindingConsumer<String> {
        override fun call(t: String) {
            AndroidLib.setGlobalModel(t == HopApplication.instance.getString(R.string.home_global_model))
        }

    })

    override fun clickRightTv() {
        super.clickRightTv()
        showSwitchNetEvent.call()
    }

    val slelctPoolCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            selectPoolLiveEvent.call()
        }
    })

    val slelctMinerCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            selectMinerLiveEvent.call()
        }
    })

    val changeVPNStatusCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            changeVPNStatusEvent.call()
        }
    })

    fun getPool() {
        model.getPool(WalletWrapper.MainAddress, SysConf.CurPoolAddress).subscribe(object : SingleObserver<UserPoolData> {
                    override fun onSuccess(userPool: UserPoolData) {
                        SysConf.PacketsBalance = userPool.packets
                        SysConf.PacketsCredit = userPool.credit
                        getPoolSuccessEvent.call()
                    }

                    override fun onSubscribe(d: Disposable) {
                        addSubscribe(d)
                    }

                    override fun onError(e: Throwable) {

                    }

                })

    }

    fun openWallet(password: String) {
        model.openWallet(password).subscribe(object : SingleObserver<Int> {
            override fun onSuccess(resultCode: Int) {
                if (resultCode == Constants.OPEN_WALLET_SUCCESS) {
                    openWalletSuccessEvent.call()
                    return
                }
                dismissDialog()
                when (resultCode) {
                    Constants.PASSWORD_ERROR -> showToast(R.string.password_error)
                    Constants.PROTOCOL_STOPPED -> exitApp.postValue(R.string.protocol_topped)
                    Constants.NO_WALLET -> exitApp.postValue(R.string.no_walet)
                }
            }

            override fun onSubscribe(d: Disposable) {
                showDialog(R.string.open_wallet)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                dismissDialog()
                showErrorToast(R.string.password_error, e)
            }

        })
    }

}