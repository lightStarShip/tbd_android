package com.star.theBigDipper.ui.activity

import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.star.theBigDipper.BR
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityMainNetAddressQrCodeBinding
import com.star.theBigDipper.service.WalletWrapper
import com.star.theBigDipper.util.Utils
import com.star.theBigDipper.viewmodel.MainNetAddressQRCodeVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main_net_address_qr_code.*

class MainNetAddressQRCodeActivity : BaseActivity<MainNetAddressQRCodeVM, ActivityMainNetAddressQrCodeBinding>() {


    override fun getLayoutId(): Int = R.layout.activity_main_net_address_qr_code

    override fun initView() {
    }

    override fun initData() {
        val qrStr2Bitmap = Utils.QRStr2Bitmap(WalletWrapper.MainAddress)
        qr_iv.setImageBitmap(qrStr2Bitmap)
    }

    override fun initObserve() {
        mViewModel.finishAfterTransitionEvent.observe(this, Observer<Boolean> { ActivityCompat.finishAfterTransition(this@MainNetAddressQRCodeActivity) })

    }

    override fun initVariableId(): Int = BR.viewModel
}