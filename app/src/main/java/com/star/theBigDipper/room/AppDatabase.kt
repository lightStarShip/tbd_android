package com.star.theBigDipper.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.star.theBigDipper.model.bean.TransactionBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Database(entities = [TransactionBean::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {

        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "transaction.db").allowMainThreadQueries().build()
            }
            return instance as AppDatabase
        }
    }

    fun delete() {
        CoroutineScope(Dispatchers.IO).launch {
            instance?.transactionDao()?.deleteTransaction()
        }
    }
}