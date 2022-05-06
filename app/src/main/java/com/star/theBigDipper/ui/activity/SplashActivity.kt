package com.star.theBigDipper.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.os.Message
import androidx.lifecycle.Observer
import com.star.theBigDipper.BR
import com.star.theBigDipper.Constants
import com.star.theBigDipper.R
import com.star.theBigDipper.callback.AlertDialogOkCallBack
import com.star.theBigDipper.databinding.ActivitySplashBinding
import com.star.theBigDipper.model.bean.AppVersionBean
import com.star.theBigDipper.service.HopService
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.SplashVM
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

/**
 * @description:
 * @author: mr.x
 * @date :   2020/5/26 10:37 AM
 */
class SplashActivity : BaseActivity<SplashVM, ActivitySplashBinding>(), PermissionCallbacks, Handler.Callback {
    private lateinit var mHandler: Handler

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initView() {}

    override fun initVariableId(): Int = BR.viewModel

    override fun initData() {
        mHandler = Handler(mainLooper, this)
        if (!Utils.isNetworkAvailable(this@SplashActivity)) {
            Utils.showOkAlert(this@SplashActivity, R.string.tips, R.string.splash_network_unavailable, object : AlertDialogOkCallBack() {
                override fun onClickOkButton(parameter: String) {
                    finish()
                }
            })
            return
        }
        if (Utils.checkVPN() && !Utils.isServiceWork(this@SplashActivity, HopService::class.java.name)) {
            Utils.showOkAlert(this@SplashActivity, R.string.tips, R.string.splash_close_other_vpn_app, object : AlertDialogOkCallBack() {
                override fun onClickOkButton(parameter: String) {
                    finish()
                }
            })
            return
        }
        mViewModel.checkVersion()
    }

    override fun initObserve() {
        mViewModel.delayLoadWalletEvent.observe(this, Observer {
            if (it == null || it.newVersion < Utils.getVersionCode(this@SplashActivity)) {
                mViewModel.loadWallet()
            } else {
                showUpdateAppDialog(it)
            }
        })

        mViewModel.initServiceFailEvent.observe(this, Observer {
            Utils.showExitAppDialog(this@SplashActivity, R.string.splash_blockchain_sync_error)
        })
    }


    private fun showUpdateAppDialog(versionBean: AppVersionBean) {
        val updateMsg: String?
        val able = resources.configuration.locale.country
        updateMsg = if (able == "CN") {
            versionBean.updateMsgCN
        } else {
            versionBean.updateMsgEN
        }
        val messageDialog = MessageDialog.build(this@SplashActivity).setCancelable(false).setTitle(getString(R.string.splash_new_version)).setMessage(updateMsg).setOkButton(getString(R.string.splash_update_version)).setOnOkButtonClickListener { _, _ ->
            Utils.openAppDownloadPage(this@SplashActivity, Constants.APP_DOWNLOAD_URL)
            finish()
            false
        }
        if (Utils.getVersionCode(this@SplashActivity) > versionBean.minversion) {
            messageDialog.setCancelButton(getString(R.string.cancel)).onCancelButtonClickListener = OnDialogButtonClickListener { _, _ ->
                mViewModel.loadWallet()
                false
            }
        }
        messageDialog.show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        mViewModel.loadWallet()
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        AppSettingsDialog.Builder(this).setTitle(getString(R.string.tips)).setRationale(R.string.splash_forbidden_permission_des).build().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                mViewModel.loadWallet()
            } else {
                finish()
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }


}