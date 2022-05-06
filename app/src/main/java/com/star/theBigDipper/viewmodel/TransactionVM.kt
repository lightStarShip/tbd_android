package com.star.theBigDipper.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.model.TransactionModel
import com.nbs.android.lib.base.BaseViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TransactionVM : BaseViewModel() {
    val transactionModel = TransactionModel()
    val items: ObservableList<TransactionItemVM> = ObservableArrayList()
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    val itemBinding = ItemBinding.of<TransactionItemVM>(BR.item, R.layout.item_transaction)

    init {
        getTransactions()
    }

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getTransactions()

        }
    })

    fun getTransactions() {
        viewModelScope.launch {
            items.clear()
            for (transaction in transactionModel.getTransactions()) {
                items.add(TransactionItemVM(this@TransactionVM, transaction))
            }
            finishRefreshingEvent.call()
        }
    }
}