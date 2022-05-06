package com.star.theBigDipper.model

import com.star.theBigDipper.base.WaitTxBaseModel
import com.star.theBigDipper.model.bean.TransactionBean
import com.star.theBigDipper.room.DataBaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionModel : WaitTxBaseModel() {
    suspend fun getTransactions(): List<TransactionBean> {
        return withContext(Dispatchers.IO) {
            DataBaseManager.getTransactions()
        }
    }

}