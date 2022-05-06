package com.star.theBigDipper.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.Constants
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventReLoadWallet
import com.star.theBigDipper.model.TabWalletModel
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.GuideActivity
import com.star.theBigDipper.ui.activity.TransactionActivity
import com.star.theBigDipper.ui.fragement.TabWalletFragment
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeoutException

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabWalletVM : BaseViewModel() {
    val model = TabWalletModel()
    val dnsObservable = ObservableField<String>()
    val versionObservable = ObservableField<String>()
    val showqrImageEvent = SingleLiveEvent<Boolean>()
    val exportEvent = SingleLiveEvent<Boolean>()
    val queryTxEvent = SingleLiveEvent<Boolean>()
    val hopBalanceEvent = SingleLiveEvent<String>()
    val clearDBEvent = SingleLiveEvent<Boolean>()
    val dnsEvent = SingleLiveEvent<Boolean>()
    val createAccountEvent = SingleLiveEvent<Boolean>()
    val applyTimeoutEvent = SingleLiveEvent<Boolean>()

    override fun clickRightIv() {
        EventBus.getDefault().post(EventReLoadWallet(true))
    }

    val showAddressCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showqrImageEvent.call()
        }
    })

    val createAccountCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            createAccountEvent.call()
        }
    })

    val applyFreeEthCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            viewModelScope.launch {
                applyFreeEth()
            }
        }
    })


    val applyFreeTokenCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            viewModelScope.launch {
                applyFreeToken()
            }
        }
    })


    val exportCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            exportEvent.call()
        }
    })

    val updateAppCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_DOWNLOAD_URL)
        }
    })

    val dnsCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            dnsEvent.call()
        }
    })
    val transactionCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(TransactionActivity::class.java)
        }
    })

    val guideCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(GuideActivity::class.java)
        }
    })

    val helpCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_HELP_URL)
        }
    })
    val courseAddressCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startWebActivity(Constants.APP_COURSE_URL)
        }
    })
    val clearDBCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clearDBEvent.call()
        }
    })

    private suspend fun applyFreeToken() {

        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            showToast(R.string.wallet_please_create_account)
            return
        }
        if (WalletWrapper.HopBalance >= TabWalletFragment.FREE_HOP_MAX_VALUE) {
            showToast(R.string.wallet_apply_free_token_des)
            return
        }

        if (model.checkPendingAndUpdate(Constants.TRANSACTION_APPLY_FREE_HOP)) {
            showToast(R.string.recharge_pending)
            return
        }

        model.applyFreeHop(WalletWrapper.MainAddress).subscribe(object : SingleObserver<String> {
            override fun onSuccess(tx: String) {
                queryTxStatus(tx, true)
            }

            override fun onSubscribe(d: Disposable) {
                showDialog(R.string.creating_tx)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                dismissDialog()
                showErrorToast(R.string.wallet_apply_fail, e)
            }
        })
    }

    private suspend fun applyFreeEth() {
        if (TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            showToast(R.string.wallet_please_create_account)
            return
        }
        if (WalletWrapper.EthBalance > TabWalletFragment.FREE_ETH_MAX_VALUE) {
            showToast(R.string.wallet_apply_free_token_des)
            return
        }

        if (model.checkPendingAndUpdate(Constants.TRANSACTION_APPLY_FREE_ETH)) {
            showToast(R.string.recharge_pending)
            return
        }
        model.applyFreeEth(WalletWrapper.MainAddress).subscribe(object : SingleObserver<String> {
            override fun onSuccess(tx: String) {

                queryTxStatus(tx, false)
            }

            override fun onSubscribe(d: Disposable) {
                showDialogNotCancel(R.string.creating_tx)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                dismissDialog()
                showErrorToast(R.string.wallet_apply_fail, e)
            }

        })
    }


    fun queryTxStatus(tx: String, isFreeHop: Boolean) {
        model.waitMinedTransactionStatus(tx).subscribe(object : SingleObserver<Any> {
            override fun onSuccess(t: Any) {
                model.updateDBTransaction(Constants.TRANSACTION_STATUS_COMPLETED, tx)
                queryTxEvent.postValue(isFreeHop)
                dismissDialog()
            }

            override fun onSubscribe(d: Disposable) {
                var msg = "\nETH TX:[$tx]"
                if (isFreeHop) {
                    msg = "\nHop TX:[$tx]"
                }

                showDialogNotCancel(msg)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                dismissDialog()
                if(e is TimeoutException){
                    applyTimeoutEvent.call()
                }else{
                    showErrorToast(R.string.wallet_apply_fail, e)
                }

            }

        })

    }

    fun hopBalance() {
        model.queryHopBalance(WalletWrapper.MainAddress).subscribe(object : SingleObserver<String> {
            override fun onSuccess(it: String) {
                if (it.isEmpty() || it.equals("<nil>")) {
                    return
                }
                hopBalanceEvent.postValue(it)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable?) {
            }

        })

    }

    fun exportAccount(cr: Context, data: String, fileName: String) {
        showDialog(R.string.loading)
        viewModelScope.launch {
            kotlin.runCatching {
                model.exportAccount(cr, data, fileName)
            }.onSuccess {
                dismissDialog()
                if (it) {
                    showToast(HopApplication.instance.getString(R.string.wallet_export_success))
                } else {
                    showToast(R.string.wallet_export_fail)
                }

            }.onFailure {
                dismissDialog()
                showErrorToast(R.string.wallet_export_fail, it)
            }

        }
    }

}