package com.star.theBigDipper.viewmodel

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.R
import com.star.theBigDipper.model.bean.MinePoolBean
import com.star.theBigDipper.ui.activity.RechargePacketsActivity
import com.nbs.android.lib.base.ItemViewModel
import com.nbs.android.lib.command.BindingAction
import com.nbs.android.lib.command.BindingCommand
import com.nbs.android.lib.utils.dp
import zhy.com.highlight.HighLight

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class PacketsMarketItemVM(VM: TabPacketsMarketVM, var minePool: MinePoolBean, var index: Int) : ItemViewModel<TabPacketsMarketVM>(VM) {
    lateinit var higghtLight: HighLight
    private val colorIds = intArrayOf(R.color.color_6d97ce, R.color.color_f7aa6e, R.color.color_4cc2d0)
    var background = ContextCompat.getDrawable(HopApplication.instance.applicationContext, R.drawable.bg_rectangle_round3_ffffff) as GradientDrawable
    var textColor = ContextCompat.getColor(HopApplication.instance.applicationContext, colorIds[index % 3])

    init {
        background.setColor(textColor)
        background.cornerRadius = 8f.dp
    }

    val rechargeCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.PoolKey, minePool.address)
            viewModel.startActivity(RechargePacketsActivity::class.java, bundle)
        }

    })

}