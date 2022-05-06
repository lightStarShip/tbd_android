package com.star.theBigDipper.model

import android.content.Context
import androidLib.AndroidLib
import com.star.theBigDipper.Constants
import com.star.theBigDipper.base.WaitTxBaseModel
import com.star.theBigDipper.model.bean.TransactionBean
import com.star.theBigDipper.room.DataBaseManager
import com.star.theBigDipper.util.BitmapUtils
import com.star.theBigDipper.util.CommonSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/27 9:09 AM
 */
class TabWalletModel : WaitTxBaseModel() {

    suspend fun exportAccount(context: Context, data: String, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val accountQR = BitmapUtils.stringToQRBitmap(data)
            BitmapUtils.saveBitmapToAlbum(context, accountQR, fileName)
        }

    }

    fun applyFreeEth(address: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val tx = AndroidLib.applyFreeEth(address)
            val transactionBean = TransactionBean(0, Constants.TRANSACTION_APPLY_FREE_ETH, tx, Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())

    }

    fun applyFreeHop(address: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            val tx = AndroidLib.applyFreeToken(address)
            val transactionBean = TransactionBean(0, Constants.TRANSACTION_APPLY_FREE_HOP, tx, Constants.TRANSACTION_STATUS_PENDING)
            DataBaseManager.addTransaction(transactionBean)
            emitter.onSuccess(tx)
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }

    fun queryHopBalance(address: String): Single<String> {
        return Single.create(SingleOnSubscribe<String> { emitter ->
            emitter.onSuccess(AndroidLib.tokenBalance(address))
        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
    }

}