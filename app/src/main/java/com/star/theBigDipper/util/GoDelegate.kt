package com.star.theBigDipper.util

import android.util.Log
import android.widget.Toast
import androidLib.HopDelegate
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventRechargeSuccess
import com.star.theBigDipper.service.HopService
import com.star.theBigDipper.ui.activity.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GoDelegate : HopDelegate {
    override fun serviceExit(e: Exception?) {
        HopService.stop()
    }

    override fun actionNotify(type: Short, msg: String?) {
        Log.w("Go", "actionNotify: type:[$type] msg:=>$msg")
        when (type.toInt()) {
            MainActivity.ATSysSettingChanged -> {
            }
            MainActivity.ATPoolsInMarketChanged -> {
            }
            MainActivity.ATNeedToRecharge -> {
                MainScope().launch {
                    Toast.makeText(HopApplication.instance, HopApplication.instance.getText(R.string.packets_insufficient_need_recharge), Toast.LENGTH_SHORT).show()
                }
                HopService.stop()
            }
            MainActivity.ATRechargeSuccess -> EventBus.getDefault().post(EventRechargeSuccess())
            else -> {
            }
        }
    }

    override fun log(str: String) {
        Log.i("Go", str)
    }
}