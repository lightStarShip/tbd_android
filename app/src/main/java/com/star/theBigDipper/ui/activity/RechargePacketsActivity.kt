package com.star.theBigDipper.ui.activity

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.star.theBigDipper.BR
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityRechargePacketsBinding
import com.star.theBigDipper.dialog.PayPasswordDialog
import com.star.theBigDipper.dialog.PayPasswordDialog.PasswordCallBack
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.adapter.FlowSelectAdapter
import com.star.theBigDipper.ui.adapter.FlowSelectAdapter.RechargeFlowState
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.RechargePacketsVM
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseActivity
import com.nbs.android.lib.utils.toast
import kotlinx.android.synthetic.main.activity_recharge_packets.*

class RechargePacketsActivity : BaseActivity<RechargePacketsVM, ActivityRechargePacketsBinding>(),
    RechargeFlowState {
    private var mPoolAddress: String? = null
    private var tokenNO = 0.0

    override fun getLayoutId(): Int = R.layout.activity_recharge_packets
    override fun initView() {
        mViewModel.title.set(getString(R.string.recharge_recharge_flow))
        mViewModel.showBackImage.set(true)
        hop_address_et.text = WalletWrapper.MainAddress
        hop_coin_number_tv.text = Utils.convertCoin(WalletWrapper.HopBalance)
        flow_recyclerview.layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        mViewModel.initFlows()

    }

    override fun initData() {
        mPoolAddress = intent.getStringExtra(IntentKey.PoolKey)
        mViewModel.poolAddress.set(mPoolAddress)
        hop_coin_tv.text = getString(R.string.recharge_hop_coin)

    }

    override fun initObserve() {
        mViewModel.bytePreTokenEvent.observe(this, Observer {
            flow_recyclerview.adapter =
                FlowSelectAdapter(this@RechargePacketsActivity, it, this@RechargePacketsActivity)

        })

        mViewModel.syncPoolSuccessEvent.observe(this, Observer {
            showSyncPoolSuccessDialog()
        })

        mViewModel.timeoutEvent.observe(this, Observer {
            MessageDialog.show(
                this@RechargePacketsActivity,
                getString(R.string.tips),
                getString(R.string.blockchain_time_out),
                getString(R.string.sure)
            ).setCancelable(false).onOkButtonClickListener = OnDialogButtonClickListener { _, _ ->
                startActivity(TransactionActivity::class.java)
                false
            }
        })
        mViewModel.pendingEvent.observe(this, Observer {
            MessageDialog.show(
                this@RechargePacketsActivity,
                getString(R.string.tips),
                getString(R.string.recharge_pending),
                getString(R.string.sure)
            ).setOnOkButtonClickListener { _, _ ->
                startActivity(TransactionActivity::class.java)

                return@setOnOkButtonClickListener false
            }
        })

        mViewModel.exitApp.observe(this, Observer { msgId ->
            Utils.showExitAppDialog(this@RechargePacketsActivity, msgId)

        })
    }

    private fun showSyncPoolSuccessDialog() {
        val content = getString(R.string.recharge_sync_pool_success)
        MessageDialog.show(
            this@RechargePacketsActivity,
            getString(R.string.tips),
            content,
            getString(R.string.sure)
        ).setCancelable(false).onOkButtonClickListener = OnDialogButtonClickListener { _, _ ->
            setResult(Activity.RESULT_OK)
            finish()
            false
        }

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun recharge(tokenNo: Double) {
        this.tokenNO = tokenNo
        mViewModel.tokenNO = tokenNo
        if (WalletWrapper.EthBalance / Utils.COIN_DECIMAL < 0.0001) {
            toast(getString(R.string.recharge_eth_insufficient_balance))
            return
        }
        if (WalletWrapper.HopBalance < tokenNo) {
            toast(getString(R.string.recharge_token_insufficient_balance))
            return
        }
        PayPasswordDialog(this, object : PasswordCallBack {
            override fun callBack(password: String) {
                mViewModel.openWallet(password)
            }
        }).show()
    }


}