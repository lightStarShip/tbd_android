package com.star.theBigDipper.ui.fragement

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.star.theBigDipper.BR
import com.star.theBigDipper.Constants
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.R
import com.star.theBigDipper.callback.AlertDialogOkCallBack
import com.star.theBigDipper.databinding.FragmentWalletBinding
import com.star.theBigDipper.event.EventLoadWalletSuccess
import com.star.theBigDipper.event.EventReLoadWallet
import com.star.theBigDipper.event.EventRechargeSuccess
import com.star.theBigDipper.model.TabWalletModel
import com.star.theBigDipper.service.HopService
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.ui.activity.CreateAccountActivity
import com.star.theBigDipper.ui.activity.MainActivity
import com.star.theBigDipper.ui.activity.MainNetAddressQRCodeActivity
import com.star.theBigDipper.ui.activity.TransactionActivity
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.TabWalletVM
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener
import com.kongzue.dialog.v3.InputDialog
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseFragment
import com.nbs.android.lib.utils.toast
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import zhy.com.highlight.HighLight
import zhy.com.highlight.position.OnBottomPosCallback
import zhy.com.highlight.shape.RectLightShape

class TabWalletFragment : BaseFragment<TabWalletVM, FragmentWalletBinding>() {

    companion object {
        const val FREE_HOP_MAX_VALUE = 500
        const val FREE_ETH_MAX_VALUE = 0.05
    }

    lateinit var higghtLight: HighLight

    private var mTabSettingModel: TabWalletModel? = null

    override fun getLayoutId(): Int = R.layout.fragment_wallet


    override fun initView() {
        mViewModel.title.set(getString(R.string.tab_wallet))
        mViewModel.showRightImage.set(true)
        mTabSettingModel = TabWalletModel()
        EventBus.getDefault().register(this)
        val netType = Utils.getInt(Constants.NET_TYPE, Constants.DEFAULT_MAIN_NET)
        if (netType != Constants.TEST_NET) {
            hineGetFreeCoin()

        }
        val newDns = Utils.getString(Constants.NEW_DNS, Constants.DNS)
        mViewModel.dnsObservable.set(resources.getString(R.string.wallet__dns) + newDns)
        val versionText = getString(R.string.splash_current_version_name) + Utils.getVersionName(mActivity)
        mViewModel.versionObservable.set(versionText)

        main_network_address_value_tv.setOnLongClickListener(View.OnLongClickListener {
            if (!TextUtils.isEmpty(main_network_address_value_tv.text.toString())) {
                Utils.copyToMemory(mActivity, main_network_address_value_tv.text.toString())
                toast(getString(R.string.copy_success))
            }
            return@OnLongClickListener false
        })

        if (MainActivity.firstCreateAccountActivity) {
            showGuide()
        }
    }

    private fun hineGetFreeCoin() {
        title_get_free_coin.visibility = View.GONE
        bg_get_free_hop_coin.visibility = View.GONE
        bg_get_free_eth_coin.visibility = View.GONE
        apply_free_token_btn.visibility = View.GONE
        get_free_hop_title.visibility = View.GONE
        get_free_eth_title.visibility = View.GONE
        apply_free_eth_btn.visibility = View.GONE
        line5.visibility = View.GONE
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.showqrImageEvent.observe(this, {
            showAddressImage()
        })
        mViewModel.exportEvent.observe(this, {
            exportWallet()
        })

        mViewModel.queryTxEvent.observe(this, {
            showQueryTxDialog(it)
        })

        mViewModel.hopBalanceEvent.observe(this, {
            if (it == "0" || it == null) {
                apply_free_token_btn.isEnabled = true

            }else{
                apply_free_token_btn.isEnabled = Utils.convertCoin(it.toDouble()).toDouble() <= FREE_HOP_MAX_VALUE
            }
        })

        mViewModel.clearDBEvent.observe(this, {
            showClearLocalDataDialog()

        })

        mViewModel.dnsEvent.observe(this, {
            showChangeDNSDialog()
        })

        mViewModel.createAccountEvent.observe(this, {
            showCreateAccountAlert()
        })
        
        mViewModel.applyTimeoutEvent.observe(this, {
            MessageDialog.show(mActivity, getString(R.string.tips), getString(R.string.apply_free_coin_blockchain_time_out), getString(R.string.sure)).setCancelable(false).onOkButtonClickListener = OnDialogButtonClickListener { _, _ ->
                startActivity(TransactionActivity::class.java)
                false
            }
        })
    }

    private fun showClearLocalDataDialog() {
        Utils.showOkOrCancelAlert(mActivity, R.string.tips, R.string.wallet_clear_local_data, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                Utils.clearLocalData(mActivity)
                toast(getString(R.string.wallet_clear_success))
            }
        })
    }

    private fun showQueryTxDialog(isFreeHop: Boolean) {
        val title: String
        if (isFreeHop) {
            title = "Hop"
            apply_free_token_btn.isEnabled = false
        } else {
            title = "Eth"
            apply_free_eth_btn.isEnabled = false
        }
        MessageDialog.show(mActivity, title, getString(R.string.wallet_apply_success), getString(R.string.sure))
        EventBus.getDefault().post(EventReLoadWallet(false))
    }


    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onShow() {
        super.onShow()
        initData()
    }


    override fun initData() {

        mViewModel.hopBalance()
        main_network_address_value_tv.text = WalletWrapper.MainAddress
        apply_free_eth_btn.isEnabled = WalletWrapper.EthBalance <= FREE_ETH_MAX_VALUE
        MainActivity.walletBean?.let {
            hop_number_tv.text = Utils.convertCoin(MainActivity.walletBean!!.hop)
            eth_number_tv.text = Utils.convertCoin(MainActivity.walletBean!!.eth)
        }

    }


    private fun showAddressImage() {
        val intent = Intent(mActivity, MainNetAddressQRCodeActivity::class.java)
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, qr_code_iv, "image").toBundle()
        startActivity(intent, bundle)
    }

    private fun showCreateAccountAlert() {
        Utils.showOkOrCancelAlert(mActivity, R.string.wallet_replace_account_title, R.string.wallet__replace_msg, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                if (HopApplication.instance.isRunning) {
                    HopService.stop()
                }
                val createIntent = Intent(mActivity, CreateAccountActivity::class.java)
                createIntent.putExtra(IntentKey.SHOW_BACK_BUTTON, true)
                startActivity(createIntent)
            }
        })
    }

    private fun showChangeDNSDialog() {
        InputDialog.build(mActivity).setTitle(R.string.tips).setMessage(R.string.wallet_dns_empty).setOkButton(R.string.sure, OnInputDialogButtonClickListener { _, _, inputStr ->
                    if (TextUtils.isEmpty(inputStr)) {
                        toast(getString(R.string.wallet_dns_empty))
                        return@OnInputDialogButtonClickListener true
                    }
                    if (!Utils.isIpAddress(inputStr)) {
                        toast(getString(R.string.wallet_dns_failed))
                        return@OnInputDialogButtonClickListener true
                    }
                    Utils.saveData(Constants.NEW_DNS, inputStr)
                    toast(getString(R.string.wallet_update_dns_success))
                    mActivity.finish()
                    false
                }).setCancelButton(R.string.cancel).show()
    }


    private fun exportWallet() {
        if (!Utils.checkStorage(mActivity)) {
            return
        }
        if (WalletWrapper.MainAddress === "") {
            return
        }
        val accountData = WalletWrapper.walletJsonData()
        if (TextUtils.isEmpty(accountData)) {
            toast(getString(R.string.create_account_empty_account))
            return
        }

        showDialog(R.string.loading)
        mViewModel.exportAccount(mActivity, accountData, getString(R.string.wallet_pirate_account))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rechargeSuccess(eventRechargeSuccess: EventRechargeSuccess) {
        EventBus.getDefault().post(EventReLoadWallet(false))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadWalletSuccess(eventLoadWalletSuccess: EventLoadWalletSuccess) {
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun showGuide() {
        higghtLight = HighLight(mActivity).autoRemove(false).intercept(true).setClickCallback {
            higghtLight.next()
        }.setOnLayoutCallback {
            higghtLight.addHighLight(R.id.apply_free_eth_btn, R.layout.guide2, OnBottomPosCallback(10F), RectLightShape()).addHighLight(R.id.apply_free_token_btn, R.layout.guide3, OnBottomPosCallback(10F), RectLightShape()).show()
        }.enableNext()
    }

}