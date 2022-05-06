package com.star.theBigDipper.ui.activity

import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityGuideBinding
import com.star.theBigDipper.ui.adapter.GuideAdapter
import com.star.theBigDipper.viewmodel.GuideVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : BaseActivity<GuideVM, ActivityGuideBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_guide

    override fun initView() {
        mViewModel.showBackImage.set(true)
        mViewModel.title.set(getString(R.string.wallet_operation_guide))
    }

    override fun initData() {
        val guideAdapter = GuideAdapter(this, mViewModel.images)
        viewpager.adapter = guideAdapter
        indicator.setViewPager(viewpager)
        guideAdapter.registerDataSetObserver(indicator.dataSetObserver)
    }

    override fun initObserve() {
    }

    override fun initVariableId(): Int = BR.viewModel
}