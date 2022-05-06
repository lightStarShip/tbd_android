package com.star.theBigDipper.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.model.TabPacketsMarketModel
import com.star.theBigDipper.model.bean.MinePoolBean
import com.star.theBigDipper.ui.activity.OwnPoolActivity
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TabPacketsMarketVM : BaseViewModel() {
    val model = TabPacketsMarketModel()
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }
    val items: ObservableList<PacketsMarketItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<PacketsMarketItemVM>(BR.item, R.layout.item_market_pool)
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getPoolInfo(true)

        }
    })

    override fun clickRightTv() {
        startActivity(OwnPoolActivity::class.java)
    }

    fun getPoolInfo(syncAllPools: Boolean) {
        model.getPoolInfo(syncAllPools).subscribe(object : SingleObserver<List<MinePoolBean>> {
            override fun onSuccess(minepool: List<MinePoolBean>) {
                onGetPoolInfoSuccess(minepool)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onGetPoolInfoFailure(e)
            }

        })
    }

    private fun onGetPoolInfoFailure(t: Throwable) {
        finishRefreshingEvent.call()
        showErrorToast(R.string.get_data_failed, t)
        showEmptyLayoutEvent.value = items.size == 0
    }

    private fun onGetPoolInfoSuccess(minePoolBeans: List<MinePoolBean>) {
        items.clear()
        for ((index, minePoolBean) in minePoolBeans.withIndex()) {
            items.add(PacketsMarketItemVM(this, minePoolBean, index))
        }
        showEmptyLayoutEvent.value = items.size == 0
        finishRefreshingEvent.call()
    }

}