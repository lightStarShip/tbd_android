package com.star.theBigDipper.ui.fragement

import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.FragmentPacketsMarketBinding
import com.star.theBigDipper.viewmodel.TabPacketsMarketVM
import com.nbs.android.lib.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_packets_market.*

class TabPacketsMarketFragment : BaseFragment<TabPacketsMarketVM, FragmentPacketsMarketBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_packets_market

    override fun initView() {
        recyclerView.itemAnimator = null
        mViewModel.title.set(getString(R.string.tab_flow_market))
        mViewModel.showRightText.set(true)
        mViewModel.rightText.set(getString(R.string.market_my_pool))
    }

    override fun initData() {
        swipeRefreshLayout.isRefreshing = true
        mViewModel.getPoolInfo(false)
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this, {
            swipeRefreshLayout.isRefreshing = false
        })
    }


}