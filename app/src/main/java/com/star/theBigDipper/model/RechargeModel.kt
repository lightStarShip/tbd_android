package com.star.theBigDipper.model

import androidLib.AndroidLib
import com.star.theBigDipper.Constants
import com.star.theBigDipper.base.WaitTxBaseModel
import com.star.theBigDipper.model.bean.TransactionBean
import com.star.theBigDipper.room.DataBaseManager
import com.star.theBigDipper.util.CommonSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import org.json.JSONObject

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/30 2:31 PM
 */
class RechargeModel : WaitTxBaseModel() {
    fun approve(tokenNO: Double): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->

            val tx = AndroidLib.authorizeTokenSpend(tokenNO)
            val transactionBean = TransactionBean(0, Constants.TRANSACTION_APROVE, tx, Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())

    }

    fun buyPacket(userAddress: String, pollAddress: String, tokenNO: Double): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val tx = AndroidLib.buyPacket(userAddress, pollAddress, tokenNO)
            val transactionBean = TransactionBean(0, Constants.TRANSACTION_RECHARGE, tx, Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())

    }

    fun getBytesPerToken(): Single<Double> {
        return Single.create(SingleOnSubscribe<Double> { emitter ->
            val sysStr = AndroidLib.systemSettings()
            val json = JSONObject(sysStr)
            emitter.onSuccess(json.optDouble("MBytesPerToken"))
        }).compose(CommonSchedulers.io2mainAndTimeout<Double>())

    }

    fun openWallet(password: String): Single<Int> {
        return Single.create(SingleOnSubscribe<Int> { emitter ->
            val resultCode = AndroidLib.openWallet(password)
            emitter.onSuccess(resultCode)
        }).compose(CommonSchedulers.io2mainAndTimeout<Int>())


    }

    fun syncPool(poolAddress: String): Single<Boolean> {
        return Single.create(SingleOnSubscribe<Boolean> { emitter ->
            val sync = AndroidLib.waitSubPool(poolAddress)
            emitter.onSuccess(sync)
        }).compose(CommonSchedulers.io2mainAndTimeout<Boolean>())

    }
}