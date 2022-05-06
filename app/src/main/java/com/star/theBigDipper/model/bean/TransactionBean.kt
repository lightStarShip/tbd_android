package com.star.theBigDipper.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionBean constructor(@PrimaryKey(autoGenerate = true) var id: Int, var transactionType: Int, var hash: String, var status: Int)
