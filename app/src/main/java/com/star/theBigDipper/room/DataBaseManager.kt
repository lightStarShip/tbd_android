package com.star.theBigDipper.room

import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.model.bean.TransactionBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object DataBaseManager {
    val transactionDao = AppDatabase.getInstance(HopApplication.instance).transactionDao()
    fun addTransaction(transactionBean: TransactionBean) {
        transactionDao.addTransaction(transactionBean)
    }

    fun updateTransaction(transactioStatus: Int, transactioHash: String) {
        transactionDao.updateTransaction(transactioStatus, transactioHash)
    }

    fun getLastTransactionByType(t: Int): TransactionBean? {
        return transactionDao.getLastTransactionByType(t)
    }

    fun getTransactions(): List<TransactionBean> {
        return transactionDao.getTransactions()
    }

    fun deleteTransaction() {
        CoroutineScope(Dispatchers.IO).launch {
            transactionDao.deleteTransaction()
        }
    }
}