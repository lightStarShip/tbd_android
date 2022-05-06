package com.star.theBigDipper.service

import android.app.IntentService
import android.content.Intent
import androidLib.AndroidLib
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.event.EventInitServiceFail
import org.greenrobot.eventbus.EventBus

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class MicroChainService : IntentService("MicroChainService") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            AndroidLib.startProtocol()
            HopApplication.instance.initServiceSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
            EventBus.getDefault().post(EventInitServiceFail())

        }
    }
}