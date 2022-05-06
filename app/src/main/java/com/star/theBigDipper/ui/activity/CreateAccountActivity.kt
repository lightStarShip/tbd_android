package com.star.theBigDipper.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidLib.AndroidLib
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.star.theBigDipper.BR
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.R
import com.star.theBigDipper.callback.AlertDialogOkCallBack
import com.star.theBigDipper.databinding.ActivityCreateAccountBinding
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.CreateAccountVM
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.nbs.android.lib.base.BaseActivity
import com.nbs.android.lib.utils.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import pub.devrel.easypermissions.EasyPermissions.RationaleCallbacks


class CreateAccountActivity : BaseActivity<CreateAccountVM, ActivityCreateAccountBinding>(), PermissionCallbacks, RationaleCallbacks {
    private val CLICK_SCAN = 0
    private val CLICK_ALBUM = 1
    private val CLICK_PRIVATE_KEY = 2
    private val IMTOKEN_ADDRESS_LENGTH = 66
    override fun getLayoutId(): Int = R.layout.activity_create_account

    override fun initView() {
        AndroidLib.initFildPath(Utils.getBaseDir(this))
        val showBackBtn = intent.getBooleanExtra(IntentKey.SHOW_BACK_BUTTON, false)
        if (showBackBtn) {
            mDataBinding.backIv.visibility = View.VISIBLE
        }
    }

    override fun initVariableId(): Int = BR.viewModel


    override fun initData() {}

    override fun initObserve() {
        mViewModel.showImportDialogEvent.observe(this, Observer { showImportDialog() })

        mViewModel.exitEvent.observe(this, Observer {
            Utils.showExitAppDialog(this@CreateAccountActivity, R.string.splash_blockchain_sync_error)
        })
    }


    private fun showImportDialog() {
        val listItems = arrayOf(getString(R.string.create_account_scanning_qr_code), getString(R.string.create_account_read_album), getString(R.string.create_account_import_private_key),getString(R.string.cancel))
        BottomMenu.show(this,getString(R.string.create_account_select_import_mode), listItems) { _, index ->
            if (index == CLICK_SCAN) {
                requestCameraPermission()
            } else if (index == CLICK_ALBUM) {
                requestLocalMemoryPermission()
            } else  if (index == CLICK_PRIVATE_KEY) {
                showImportImtokenPrivateKeyDialog()
            }else{
                dismissDialog()
            }
        }.setShowCancelButton(false)
    }

    private fun showImportImtokenPrivateKeyDialog() {
        var privateKey: EditText? = null
        var password: EditText? = null
        MessageDialog.show(this, getString(R.string.tips), "", getString(R.string.sure), getString(R.string.cancel)).setCustomView(R.layout.layout_input_imtoken_private_key) { dialog, v -> //绑定布局事件，可使用v.findViewById(...)来获取子组件
                    privateKey = v.findViewById<EditText>(R.id.private_key)
                    password = v.findViewById<EditText>(R.id.password)
                }.setOnOkButtonClickListener { baseDialog, v ->
                    var newKey = privateKey?.text.toString().trim()
                    if (newKey.length == IMTOKEN_ADDRESS_LENGTH) {
                        newKey = newKey.substring(2)
                        newKey = "0X89" + newKey + "99"
                    } else {
                        toast(getString(R.string.create_account_imtoken_error))
                        return@setOnOkButtonClickListener false
                    }
                    mViewModel.importImtoken(password?.text.toString().trim(), newKey)
                    return@setOnOkButtonClickListener false

                }
    }

    @AfterPermissionGranted(Utils.RC_LOCAL_MEMORY_PERM)
    fun requestLocalMemoryPermission() {
        if (Utils.hasStoragePermission(this)) {
            openAlbum()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_extra_write), Utils.RC_LOCAL_MEMORY_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    @AfterPermissionGranted(Utils.RC_CAMERA_PERM)
    fun requestCameraPermission() {
        if (Utils.hasCameraPermission(this)) {
            val ii = IntentIntegrator(this@CreateAccountActivity)
            ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            ii.captureActivity = ScanActivity::class.java
            ii.setPrompt(getString(R.string.create_account_scan_pirate_account_qr))
            ii.setCameraId(0)
            ii.setBarcodeImageEnabled(true)
            ii.initiateScan()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.camera), Utils.RC_CAMERA_PERM, Manifest.permission.CAMERA)
        }
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Utils.RC_SELECT_FROM_GALLERY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Utils.RC_SELECT_FROM_GALLERY == requestCode) {
            if (resultCode != Activity.RESULT_OK || null == data) {
                return
            }
            loadAccountFromUri(data.data)
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    ?: return
            if (result.contents == null) {
                return
            }
            try {
                val walletStr = result.contents
                showPasswordDialog(walletStr)
            } catch (ex: Exception) {
                toast(getString(R.string.create_account_import_account_failed) + ex.localizedMessage)
            }
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:$requestCode")
    }

    fun showPasswordDialog(walletStr: String) {
        Utils.showPassword(this, object : AlertDialogOkCallBack() {
            override fun onClickOkButton(parameter: String) {
                showDialog()
                mViewModel.importWallet(parameter, walletStr)
            }
        })
    }


    fun loadAccountFromUri(uri: Uri?) {
        if (null == uri) {
            toast(getString(R.string.create_account_error_import_image))
            return
        }
        try {
            val walletStr = Utils.parseQRCodeFile(uri, contentResolver)
            showPasswordDialog(walletStr)
        } catch (e: Exception) {
            toast(getString(R.string.create_account_import_error) + e.localizedMessage)
            e.printStackTrace()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null && dialog!!.isShow) {
                dialog!!.doDismiss()
                mViewModel.cancelRequest()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}