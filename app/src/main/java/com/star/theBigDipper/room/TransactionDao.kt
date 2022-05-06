package com.star.theBigDipper.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.star.theBigDipper.model.bean.TransactionBean

@Dao
interface TransactionDao {
    @Query("select * from transactions order by id DESC")
    fun getTransactions(): List<TransactionBean>

    @Query("select * from transactions  where transactionType = :t order by id DESC limit 1")
    fun getLastTransactionByType(t: Int): TransactionBean?

    @Query("delete from transactions")
    fun deleteTransaction()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTransaction(transactionBean: TransactionBean)

    @Query("update transactions set status = :transactionStatus where hash == :transactionHash")
    fun updateTransaction(transactionStatus: Int, transactionHash: String)
}