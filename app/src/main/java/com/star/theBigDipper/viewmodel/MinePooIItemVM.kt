package com.star.theBigDipper.viewmodel

import android.os.Handler
import android.os.Looper
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventReloadMinePackets
import com.star.theBigDipper.model.bean.OwnPool
import com.star.theBigDipper.service.HopService
import com.star.theBigDipper.service.SysConf
import com.star.theBigDipper.ui.activity.MineMachineListActivity.Companion.sMinerBeans
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MinePooIItemVM(vm: MinePoolVM, var pool: OwnPool) : ItemViewModel<MinePoolVM>(vm) {


    val clickCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (pool.address == SysConf.CurPoolAddress) {
                viewModel.finish()
                return
            }
            if (HopApplication.instance.isRunning) {
                HopService.stop()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.showDialog(R.string.mining_pool_exchange_mine_pool)
                Thread(Runnable {
                    sMinerBeans = null
                    SysConf.changeCurPool(pool.address!!, pool.name!!)
                    EventBus.getDefault().post(EventReloadMinePackets())
                    viewModel.dismissDialog()
                    viewModel.finishAndResultOkEvent.postValue(null)
                }).start()
            }, 200)
        }
    })
}