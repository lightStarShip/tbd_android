package com.star.theBigDipper.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.model.MineMachineListModel
import com.star.theBigDipper.model.bean.MinerBean
import com.star.theBigDipper.service.SysConf
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.text.DecimalFormat

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MineMachineListVM : BaseViewModel() {
    val model = MineMachineListModel()
    val finishAndResultOk: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val showEmptyLayoutEvent = SingleLiveEvent<Boolean>().apply { value = false }
    val mDecimalFormat = DecimalFormat("0.00")
    val items: ObservableList<MineMachineItemVM> = ObservableArrayList()
    val itemBinding = ItemBinding.of<MineMachineItemVM>(BR.item, R.layout.item_mine_machine)
    var finishRefreshingEvent = SingleLiveEvent<Any>()

    val pinCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast(R.string.testing_speed)
            if (items.size != 0) {
                viewModelScope.launch {
                    for (item in items) {
                        item.minerBean.time.set(mDecimalFormat.format(model.ping(item.minerBean.address)))
                    }
                }

            }
        }
    })

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getMachineList(SysConf.CurPoolAddress, 16)
        }
    })


    fun getMachineList(address: String, random: Int) {
        model.getMineMachine(address, random).subscribe(object : SingleObserver<List<MinerBean>> {
            override fun onSuccess(miners: List<MinerBean>) {
                onGetMachineListSuccess(miners)
            }

            override fun onSubscribe(d: Disposable) {
                addSubscribe(d)
            }

            override fun onError(e: Throwable) {
                onGetMachineListFailure(e)
            }

        })

    }

    private fun onGetMachineListFailure(t: Throwable) {
        finishRefreshingEvent.postValue(true)
        showErrorToast(R.string.get_data_failed, t)
        showEmptyLayoutEvent.value = items.size == 0
    }

    private fun onGetMachineListSuccess(minerBeans: List<MinerBean>) {
        finishRefreshingEvent.postValue(true)
        items.clear()
        for (minerBean in minerBeans) {
            items.add(MineMachineItemVM(this, minerBean))
        }
        showEmptyLayoutEvent.value = items.size == 0
    }


}