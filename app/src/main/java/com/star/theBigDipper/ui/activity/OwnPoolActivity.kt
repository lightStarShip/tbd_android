package com.star.theBigDipper.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import com.star.theBigDipper.BR
import com.star.theBigDipper.Constants
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityOwnPoolBinding
import com.star.theBigDipper.viewmodel.OwnPoolVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_own_pool.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class OwnPoolActivity : BaseActivity<OwnPoolVM, ActivityOwnPoolBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_own_pool

    override fun initView() {
        mViewModel.title.set(getString(R.string.market_my_pool))
        mViewModel.showBackImage.set(true)
        recyclerView.itemAnimator = null
    }

    override fun initData() {
        swipeRefreshLayout.isRefreshing = true
        mViewModel.getOwnPool(false)
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this, Observer() {
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_BUY_PACKET && resultCode == Activity.RESULT_OK) {
            mViewModel.getOwnPool(true)
        }
    }
}