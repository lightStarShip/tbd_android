package com.star.theBigDipper.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityMineMachineBinding
import com.star.theBigDipper.model.bean.MinerBean
import com.star.theBigDipper.service.SysConf
import com.star.theBigDipper.viewmodel.MineMachineListVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_mine_machine.*

class MineMachineListActivity : BaseActivity<MineMachineListVM, ActivityMineMachineBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_mine_machine

    override fun initView() {
        mViewModel.title.set(getString(R.string.mine_machine))
        mViewModel.showBackImage.set(true)
        swipeRefreshLayout.setRefreshing(true)
        recyclerView.itemAnimator = null
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this, Observer() {
            swipeRefreshLayout.setRefreshing(false)
        });

        mViewModel.finishAndResultOk.observe(this, Observer() {
            setResult(Activity.RESULT_OK, null)
            finish()
        });
    }

    override fun initVariableId(): Int = BR.viewModel


    override fun initData() {
        mViewModel.getMachineList(SysConf.CurPoolAddress, 16)
    }


    companion object {
        @JvmStatic
        var sMinerBeans: List<MinerBean>? = null
    }

}