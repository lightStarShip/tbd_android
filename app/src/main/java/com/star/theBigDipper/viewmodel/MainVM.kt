package com.star.theBigDipper.viewmodel

import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventLoadWalletSuccess
import com.star.theBigDipper.model.MainModel
import com.star.theBigDipper.model.bean.WalletBean
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.MainActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainVM : BaseViewModel() {
    val model = MainModel()
    val hindeFreeCoinEvent = SingleLiveEvent<Boolean>()

    fun getWalletInfo(isShowLoading: Boolean) {
        model.getWalletInfo().subscribe(object : SingleObserver<WalletBean> {
            override fun onSuccess(wallet: WalletBean?) {
                walletInfoSuccess(isShowLoading, wallet)
            }

            override fun onSubscribe(d: Disposable) {
                if (isShowLoading) {
                    showDialog(R.string.loading)
                }
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                if (isShowLoading) {
                    dismissDialog()
                }
                walletInfoFailure(e)
            }
        })
    }

    fun syncSubPoolsData() {
        viewModelScope.launch {
            try {
                model.syncSubPoolsData()
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    fun initService() {
        model.initService(HopApplication.instance.applicationContext).subscribe(object : SingleObserver<Any> {
            override fun onSuccess(t: Any) {
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    private fun walletInfoSuccess(isShowLoading: Boolean, walletBean: WalletBean?) {
        if (isShowLoading) {
            dismissDialog()
        }
        if (walletBean != null) {
            MainActivity.walletBean = walletBean
            WalletWrapper.MainAddress = walletBean.main
            WalletWrapper.SubAddress = walletBean.sub
            WalletWrapper.EthBalance = walletBean.eth
            WalletWrapper.HopBalance = walletBean.hop
            WalletWrapper.Approved = walletBean.approved
            if (MainActivity.walletBean!!.hop != 0.0) {
                hindeFreeCoinEvent.postValue(true)
            }
            EventBus.getDefault().postSticky(EventLoadWalletSuccess())
        } else {
            showToast(R.string.get_data_failed)
        }
    }

    fun walletInfoFailure(throwable: Throwable) {
        showErrorToast(R.string.get_data_failed, throwable)
    }

}
