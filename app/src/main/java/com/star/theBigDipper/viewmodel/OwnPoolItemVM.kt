package com.star.theBigDipper.viewmodel

import android.os.Bundle
import android.text.SpannableString
import androidx.databinding.ObservableField
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.model.OwnPoolModel
import com.star.theBigDipper.model.bean.OwnPool
import com.star.theBigDipper.model.bean.UserPoolData
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.RechargePacketsActivity
import com.star.theBigDipper.util.Utils
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class OwnPoolItemVM(var vm: OwnPoolVM, var own: OwnPool, var index: Int) : ItemViewModel<OwnPoolVM>(vm) {
    private val model = OwnPoolModel()
    val packets = ObservableField<SpannableString>()
    val token = ObservableField<SpannableString>()
    val credit = ObservableField<SpannableString>()

    init {
        getItemPacket()
    }

    private fun getItemPacket() {
        own.address?.let {
            model.getPacketsByPool(WalletWrapper.MainAddress, it).subscribe(object : SingleObserver<UserPoolData> {
                        override fun onSuccess(userPool: UserPoolData) {
                            packets.set(Utils.formatText(Utils.convertBandWidth(userPool.packets), "\nPackets"))
                            token.set(Utils.formatText(Utils.convertCoin(userPool.token), " HOP\nToken"))
                            credit.set(Utils.formatText(Utils.convertBandWidth(userPool.credit), "\nCredit"))
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            println(e.message)
                        }
                    })
        }

    }

    val rechargeCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.PoolKey, own.address)
            vm.startActivity(RechargePacketsActivity::class.java, bundle)
        }
    })
}