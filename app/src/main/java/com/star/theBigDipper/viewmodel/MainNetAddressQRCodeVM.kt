package com.star.theBigDipper.viewmodel

import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainNetAddressQRCodeVM : BaseViewModel() {
    val finishAfterTransitionEvent = SingleLiveEvent<Boolean>()

    val finishCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finishAfterTransitionEvent.postValue(true)
        }

    })

}