package com.star.theBigDipper.viewmodel

import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.model.bean.MinerBean
import com.star.theBigDipper.service.SysConf
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import kotlinx.coroutines.launch


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MineMachineItemVM(vm: MineMachineListVM, var minerBean: MinerBean) : ItemViewModel<MineMachineListVM>(vm) {
    val pingCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            viewModelScope.launch {
                kotlin.runCatching {
                    minerBean.time.set(viewModel.mDecimalFormat.format(viewModel.model.ping(minerBean.address)))
                }

            }
        }
    })

    val itemClickCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (minerBean.address == SysConf.CurMinerID) {
                viewModel.finish()
                return
            }
            SysConf.setCurMiner(minerBean.address)
            viewModel.finishAndResultOk.postValue(true)
        }
    })
}