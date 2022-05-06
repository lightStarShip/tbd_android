package com.star.theBigDipper.ui.fragement

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.text.TextUtils
import androidLib.AndroidLib
import androidx.lifecycle.Observer
import com.star.theBigDipper.BR
import com.star.theBigDipper.Constants
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.callback.AlertDialogOkCallBack
import com.star.theBigDipper.databinding.FragmentHomeBinding
import com.star.theBigDipper.event.*
import com.star.theBigDipper.service.HopService
import com.star.theBigDipper.service.SysConf
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.MineMachineListActivity
import com.star.theBigDipper.ui.activity.MinePoolListActivity
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.TabHomeVM
import com.nbs.android.lib.base.BaseFragment
import com.nbs.android.lib.utils.AppManager
import com.nbs.android.lib.utils.toast
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TabHomeFragment : BaseFragment<TabHomeVM, FragmentHomeBinding>() {

    private var mHopIntent: Intent? = null
    override fun getLayoutId(): Int = R.layout.fragment_home
    var netType = Constants.DEFAULT_MAIN_NET

    override fun initView() {
        mViewModel.title.set(getString(R.string.app_name))
        netType = Utils.getInt(Constants.NET_TYPE, Constants.DEFAULT_MAIN_NET)

        loadLocalConf()
        showPacketsData()
        EventBus.getDefault().register(this)

        if (AndroidLib.isGlobalMode()) {
            global_model_rbtn.isChecked = true
        } else {
            intelligent_model_rbtn.isChecked = true
        }
        if (HopApplication.instance.isRunning) {
            pirate_network_status_tv.setText(getString(R.string.home_use))
        } else {
            pirate_network_status_tv.setText(getString(R.string.home_disconnected))
        }
    }

    override fun initData() {}
    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.selectPoolLiveEvent.observe(this, {
            val intent = Intent(mActivity, MinePoolListActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_MINE_POOL_CODE)
        })

        mViewModel.selectMinerLiveEvent.observe(this, Observer {
            if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
                toast(getString(R.string.home_select_subscribed_mining_pool))
                return@Observer
            }
            val intent = Intent(mActivity, MineMachineListActivity::class.java)
            startActivityForResult(intent, Constants.REQUEST_MINE_MACHINE_CODE)
        })

        mViewModel.changeVPNStatusEvent.observe(this, { changeVPNStatus() })

        mViewModel.getPoolSuccessEvent.observe(this, {
            use_flow_tv.text = Utils.convertBandWidth(SysConf.PacketsBalance)
            uncleared_tv.text = Utils.convertBandWidth(SysConf.PacketsCredit)
        })

        mViewModel.openWalletSuccessEvent.observe(this, {
            showDialog(R.string.home_connect)
            mHopIntent = Intent(mActivity, HopService::class.java)
            mActivity.startService(mHopIntent)
        })

        mViewModel.exitApp.observe(this, { msgId ->
            Utils.showExitAppDialog(mActivity, msgId)
        })

        mViewModel.showSwitchNetEvent.observe(this, { msgId ->
            showSwitchNetEvent()
        })
    }

    private fun showSwitchNetEvent() {
        val titleId: Int
        val msgId: Int
        if (netType == Constants.TEST_NET) {
            titleId = R.string.home_switch_mainnet
            msgId = R.string.home_switch_mainnet_message
        } else {
            titleId = R.string.home_switch_testnet
            msgId = R.string.home_switch_testnet_message
        }
        Utils.showOkOrCancelAlert(mActivity, titleId, msgId, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                if (netType == Constants.TEST_NET) {
                    Utils.saveInt(Constants.NET_TYPE, Constants.MAIN_NET)
                } else {
                    Utils.saveInt(Constants.NET_TYPE, Constants.TEST_NET)
                }
                AppManager.removeAllActivity()
                AppManager.killAppProcess(mActivity)
            }
        })
    }

    private fun showPacketsData() {
        if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
            miner_pool_tv.text = resources.getString(R.string.home_select_subscribed_mining_pool)
        } else {
            miner_pool_tv.text = SysConf.CurPoolName
        }
        if (TextUtils.isEmpty(SysConf.CurMinerID)) {
            miner_machin_tv.text = resources.getString(R.string.home_select_miner)
        } else {
            miner_machin_tv.text = SysConf.CurMinerID
        }
        use_flow_tv.text = Utils.convertBandWidth(SysConf.PacketsBalance)
        uncleared_tv.text = Utils.convertBandWidth(SysConf.PacketsCredit)
    }

    private fun loadLocalConf() {
        if (this.netType == Constants.DEFAULT_MAIN_NET) {
            val keyCachedPool =
                String.format(SysConf.KEY_CACHED_POOL_IN_USE, Constants.MAIN_TOKEN_ADDRESS)
            val keyCachedPoolName =
                String.format(SysConf.KEY_CACHED_POOL_NAME_IN_USE, Constants.MAIN_TOKEN_ADDRESS)
            SysConf.CurPoolAddress = Utils.getString(keyCachedPool, "")
            SysConf.CurPoolName = Utils.getString(keyCachedPoolName, "")

        } else if (this.netType == Constants.TEST_NET) {
            val keyCachedPool =
                String.format(SysConf.KEY_CACHED_POOL_IN_USE, Constants.TOKEN_ADDRESS)
            val keyCachedPoolName =
                String.format(SysConf.KEY_CACHED_POOL_NAME_IN_USE, Constants.TOKEN_ADDRESS)
            SysConf.CurPoolAddress = Utils.getString(keyCachedPool, "")
            SysConf.CurPoolName = Utils.getString(keyCachedPoolName, "")

        }
        val mKey = String.format(SysConf.KEY_CACHED_MINER_ID_IN_USE, SysConf.CurPoolAddress)
        SysConf.CurMinerID = Utils.getString(mKey, "")

        if (TextUtils.isEmpty(SysConf.CurPoolAddress) || TextUtils.isEmpty(WalletWrapper.MainAddress)) {
            SysConf.PacketsBalance = 0.0
            SysConf.PacketsCredit = 0.0
            showPacketsData()
        }
    }


    private fun changeVPNStatus() {
        if (!Utils.isFastClick) {
            return
        }
        if (!checkMessageForStartVpnService()) {
            return
        }
        if (HopApplication.instance.isRunning) {
            HopService.stop()
        } else {
            vpnPrepare()
        }
    }

    private fun vpnPrepare() {
        val ii = VpnService.prepare(mActivity)
        if (ii != null) {
            startActivityForResult(ii, RC_VPN_RIGHT)
        } else {
            onActivityResult(RC_VPN_RIGHT, Activity.RESULT_OK, null)
        }
    }

    private fun checkMessageForStartVpnService(): Boolean {
        if (TextUtils.isEmpty(SysConf.CurPoolAddress)) {
            toast(getString(R.string.home_select_subscribed_mining_pool))
            return false
        }
        if (TextUtils.isEmpty(SysConf.CurMinerID)) {
            toast(getString(R.string.home_select_miner))
            return false
        }
        return true
    }

    private fun startVpnService() {
        showDialog(R.string.home_connect)
        mHopIntent = Intent(mActivity, HopService::class.java)
        mActivity.startService(mHopIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == Constants.REQUEST_MINE_POOL_CODE || requestCode == Constants.REQUEST_MINE_MACHINE_CODE) {
            if (HopApplication.instance.isRunning) {
                HopService.stop()
            }
            showPacketsData()
        } else if (RC_VPN_RIGHT == requestCode) {
            if (WalletWrapper.isOpen) {
                if (HopApplication.instance.initServiceSuccess) {
                    startVpnService()
                } else {
                    toast(getString(R.string.home_sync_data))
                }
            } else {
                showInputPasswordDialog()
            }
        }
    }

    private fun showInputPasswordDialog() {
        Utils.showPassword(mActivity, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(password: String) {
                if (TextUtils.isEmpty(password)) {
                    toast(getString(R.string.create_account_enter_password))
                    return
                }
                mViewModel.openWallet(password)
            }
        })
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun VPNOpen(eventVPNOpen: EventVPNOpen) {
        pirate_network_status_tv.text = getString(R.string.home_use)
        dismissDialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun VPNClose(eventVPNClosed: EventVPNClosed) {
        pirate_network_status_tv.text = getString(R.string.home_disconnected)
        dismissDialog()
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rechargeSuccess(eventRechargeSuccess: EventRechargeSuccess) {
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventReloadPoolsMarket(eventReloadPoolsMarket: EventReloadMinePackets) {
        mViewModel.getPool()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadWalletSuccess(eventLoadWalletSuccess: EventLoadWalletSuccess) {
        loadLocalConf()
        mViewModel.getPool()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val RC_VPN_RIGHT = 126
    }

}