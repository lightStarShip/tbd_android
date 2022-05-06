package com.star.theBigDipper.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableField
import com.star.theBigDipper.Constants
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventNewAccount
import com.star.theBigDipper.model.CreateAccountModel
import com.star.theBigDipper.room.DataBaseManager
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.MainActivity
import com.star.theBigDipper.util.Utils
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateAccountVM : BaseViewModel() {
    val model = CreateAccountModel()
    val password = ObservableField<String>()
    val confirmPassword = ObservableField<String>()
    val showImportDialogEvent = SingleLiveEvent<Any?>()
    val exitEvent = SingleLiveEvent<Any?>()

    val finisCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finish()
        }

    })

    val createCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (verifyPassword()) {
                createAccount()
            }
        }

    })

    private fun verifyPassword(): Boolean {
        if (TextUtils.isEmpty(password.get())) {
            uc.toastEvent.value = R.string.create_account_enter_password
            return false
        } else if (!confirmPassword.get().equals(password.get())) {
            uc.toastEvent.value = R.string.create_account_twice_password_not_same
            return false
        }
        return true
    }

    fun createAccount() {
        model.createAccount(password.get()!!).subscribe(object : SingleObserver<String> {
            override fun onSuccess(wallet: String) {
                createSuccess(wallet)
            }

            override fun onSubscribe(d: Disposable) {
                showDialogNotCancel(R.string.creating_account)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                createFailure(e)
            }

        })
    }

    private fun createSuccess(it: String) {
        WalletWrapper.MainAddress = JSONObject(it).optString("mainAddress")
        DataBaseManager.deleteTransaction()
        showDialogNotCancel(R.string.loading)
        initService(true)
    }

    private fun createFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.create_account_failed, t)
    }

    val importCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showImportDialogEvent.call()
        }

    })


    fun importWallet(password: String, walletStr: String) {

        model.importWallet(walletStr, password).subscribe(object : SingleObserver<Int> {

            override fun onSubscribe(d: Disposable) {
                showDialogNotCancel(R.string.loading)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                importWalletFailure(e)
            }

            override fun onSuccess(resultCode: Int) {
                if (resultCode == Constants.OPEN_WALLET_SUCCESS) {
                    WalletWrapper.MainAddress = JSONObject(walletStr).optString("mainAddress")
                    DataBaseManager.deleteTransaction()
                    importWalletSuccess()
                    return
                }
                dismissDialog()
                var msgId = 0
                when (resultCode) {
                    Constants.PASSWORD_ERROR -> {
                        msgId = R.string.password_error
                    }
                    Constants.WALLET_ERROR -> {
                        msgId = R.string.wallet_error
                    }
                    Constants.WALLET_SAVE_ERROR -> {
                        msgId = R.string.save_error
                    }
                }
                showToast(msgId)

            }

        })
    }

    fun importImtoken(password: String, privateKey: String) {

        model.importImtokenPrivateKey(password, privateKey)
            .subscribe(object : SingleObserver<String> {

                override fun onSubscribe(d: Disposable) {
                    showDialogNotCancel(R.string.loading)
                    addSubscribe(d)
                }

                override fun onError(e: Throwable) {
                    dismissDialog()
                    showErrorToast(R.string.create_account_import_imtoken_error, e)
                }

                override fun onSuccess(walletStr: String) {
                    WalletWrapper.MainAddress = JSONObject(walletStr).optString("mainAddress")
                    DataBaseManager.deleteTransaction()
                    importWalletSuccess()
                }

            })
    }


    private fun importWalletSuccess() {
        initService(false)
    }

    private fun importWalletFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.password_error, t)
    }

    fun initService(isCreated: Boolean) {

        model.initService(HopApplication.instance.applicationContext).subscribe(object :
            SingleObserver<Any> {
            override fun onSuccess(any: Any) {
                initServiceSuccess(isCreated)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                initServiceFailure()
            }

        })

    }

    private fun initServiceFailure() {
        exitEvent.call()
    }

    private fun initServiceSuccess(isCreated: Boolean) {
        dismissDialog()
        val netType = Utils.getInt(Constants.NET_TYPE, Constants.DEFAULT_MAIN_NET)
        Utils.clearAllData()
        Utils.saveInt(Constants.NET_TYPE, netType)
        if (isCreated) {
            showToast(R.string.create_account_success)
            EventBus.getDefault().post(EventNewAccount())
        } else {
            showToast(R.string.create_account_import_success)
            EventBus.getDefault().post(EventNewAccount())
        }
        startActivityAndFinish(MainActivity::class.java)
    }

}