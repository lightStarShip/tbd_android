package com.star.theBigDipper.base

import androidLib.AndroidLib
import com.star.theBigDipper.Constants
import com.star.theBigDipper.room.DataBaseManager
import com.star.theBigDipper.util.CommonSchedulers
import com.nbs.android.lib.base.BaseModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @description:
 * @author: Mr.x
 * @date :   2020/7/10 7:58 AM
 */
open class WaitTxBaseModel : BaseModel() {
    fun waitMinedTransactionStatus(tx: String): Single<Any> {
        return Single.create(SingleOnSubscribe<Any> { emitter ->
            AndroidLib.waitMined(tx)
            emitter.onSuccess("")
        }).compose(CommonSchedulers.io2mainAndTimeout<Any>())

    }

    fun updateDBTransaction(txStatus: Int, tx: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DataBaseManager.updateTransaction(txStatus, tx)
        }
    }


    suspend fun getTransactionStatus(tx: String): Int {
        return withContext(Dispatchers.IO) {
            AndroidLib.checkTransactionStatus(tx)
        }
    }


    suspend fun checkPendingAndUpdate(type: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val transactionBean = DataBaseManager.getLastTransactionByType(type)
            if (transactionBean == null || transactionBean.status != Constants.TRANSACTION_STATUS_PENDING) {
                false
            } else {
                val txStatus = getTransactionStatus(transactionBean.hash)
                transactionBean.status = txStatus
                updateDBTransaction(txStatus, transactionBean.hash)
                txStatus == Constants.TRANSACTION_STATUS_PENDING
            }
        }
    }
}