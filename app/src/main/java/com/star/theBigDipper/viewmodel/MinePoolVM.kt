package com.star.theBigDipper.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.model.OwnPoolModel
import com.star.theBigDipper.model.bean.OwnPool
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
class MinePoolVM : BaseViewModel() {
    val model = OwnPoolModel()
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var finishAndResultOkEvent = SingleLiveEvent<Any>()
    val items: ObservableList<MinePooIItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<MinePooIItemVM>(BR.item, R.layout.item_mine_pool)

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getMinePool(true)

        }
    })

    fun getMinePool(refresh: Boolean) {
        model.getPoolDataOfUser(refresh).subscribe(object : SingleObserver<ArrayList<OwnPool>> {
            override fun onSuccess(ownPools: ArrayList<OwnPool>?) {
                requestSuccess(ownPools)
                finishRefreshingEvent.call()
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                requestFailure(e)
                finishRefreshingEvent.call()
            }

        })
    }

    private fun requestFailure(t: Throwable) {
        showErrorToast(R.string.get_data_failed, t)
        if (items.size == 0) {
            showEmptyLayoutEvent.value = true
        }
    }

    private fun requestSuccess(ownPools: List<OwnPool>?) {
        items.clear()
        var index = 0
        ownPools?.forEach {
            items.add(MinePooIItemVM(this, it))
            index++
        }
        showEmptyLayoutEvent.value = items.size == 0
    }


}