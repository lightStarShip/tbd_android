package com.star.theBigDipper.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.Constants
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventReLoadWallet
import com.star.theBigDipper.event.EventRechargeSuccess
import com.star.theBigDipper.model.RechargeModel
import com.star.theBigDipper.service.WalletWrapper
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.math.pow

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class RechargePacketsVM : BaseViewModel() {
    val model = RechargeModel()
    private val AUTHORIZE_TOKEN = 4.2e8
    val poolAddress = ObservableField<String>("")
    var tokenNO = 0.0
    val bytePreTokenEvent = SingleLiveEvent<Double>()
    val syncPoolSuccessEvent = SingleLiveEvent<Boolean>()
    val timeoutEvent = SingleLiveEvent<Boolean>()
    val pendingEvent = SingleLiveEvent<Boolean>()
    val exitApp = SingleLiveEvent<Int>()

    fun initFlows() {
        model.getBytesPerToken().subscribe(object : SingleObserver<Double> {
            override fun onSuccess(t: Double) {
                onInitFlowsSuccess(t)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onInitFlowsFailure(e)
            }

        })

    }

    private fun onInitFlowsFailure(t: Throwable) {
        showErrorToast(R.string.get_data_failed, t)
    }

    private fun onInitFlowsSuccess(it: Double) {
        bytePreTokenEvent.value = it
    }

    fun openWallet(password: String) {
        model.openWallet(password).subscribe(object : SingleObserver<Int> {
            override fun onSuccess(resultCode: Int) {
                if (resultCode == Constants.OPEN_WALLET_SUCCESS) {
                    viewModelScope.launch {
                        onOpenWalletSuccess()
                    }
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
                onOpenWalletFailure(e)
            }

        })

    }

    private fun onOpenWalletFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.password_error, t)
    }

    private suspend fun onOpenWalletSuccess() {
        if (WalletWrapper.Approved / 10.0.pow(18.0) >= tokenNO) {
            if (model.checkPendingAndUpdate(Constants.TRANSACTION_RECHARGE)) {
                dismissDialog()
                pendingEvent.call()
            } else {
                dismissDialog()
                buyPacket()
            }
        } else {
            if (model.checkPendingAndUpdate(Constants.TRANSACTION_APROVE)) {
                dismissDialog()
                pendingEvent.call()
            } else {
                dismissDialog()
                approve()
            }
        }
    }

    fun buyPacket() {
        model.buyPacket(WalletWrapper.MainAddress, poolAddress.get()!!, tokenNO).subscribe(object : SingleObserver<String> {
                    override fun onSuccess(t: String) {
                        onBuyPacketSuccess(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                        showDialogNotCancel(R.string.recharge_buy_packets)
                        addSubscribe(d)
                    }

                    override fun onError(e: Throwable) {
                        onBuyPacketFailure(e)
                    }

                })
    }

    private fun onBuyPacketFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.recharge_buy_packets_error, t)

    }

    private fun onBuyPacketSuccess(tx: String) {
        queryTxStatus(tx, false)
    }

    private fun approve() {

        model.approve(AUTHORIZE_TOKEN).subscribe(object : SingleObserver<String> {
            override fun onSuccess(approve: String) {
                onApproveSuccess(approve)
            }

            override fun onSubscribe(d: Disposable) {
                showDialogNotCancel(R.string.approving)
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onApproveFailure(e)
            }

        })
    }

    private fun onApproveFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.recharge_approve_error, t)
    }

    private fun onApproveSuccess(tx: String) {
        queryTxStatus(tx, true)

    }

    fun queryTxStatus(tx: String, isProve: Boolean) {
        model.waitMinedTransactionStatus(tx).subscribe(object : SingleObserver<Any> {
            override fun onSuccess(t: Any) {
                EventBus.getDefault().post(EventReLoadWallet(false))
                model.updateDBTransaction(Constants.TRANSACTION_STATUS_COMPLETED, tx)
                onQueryTxStatusSuccess(isProve)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onQueryTxStatusFailure()
            }

        })
    }

    private fun onQueryTxStatusFailure() {
        dismissDialog()
        timeoutEvent.call()
    }

    private fun onQueryTxStatusSuccess(prove: Boolean) {
        dismissDialog()
        EventBus.getDefault().post(EventRechargeSuccess())
        if (prove) {
            buyPacket()
        } else {
            showDialogNotCancel(R.string.recharge_sync_pool)
            EventBus.getDefault().post(EventRechargeSuccess())
            syncPool()
        }
    }

    private fun syncPool() {
        model.syncPool(poolAddress.get()!!).subscribe(object : SingleObserver<Boolean> {
            override fun onSuccess(b: Boolean) {
                onSyncPoolSuccess(b)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onSyncPoolFailure(e)
            }
        })
    }

    private fun onSyncPoolFailure(t: Throwable) {
        dismissDialog()
        showErrorToast(R.string.recharge_sync_pool_failed, t)
    }

    private fun onSyncPoolSuccess(syncSuccess: Boolean) {
        dismissDialog()
        if (syncSuccess) {
            syncPoolSuccessEvent.value = true
        } else {
            showToast(R.string.recharge_sync_pool_failed)
        }
    }

}