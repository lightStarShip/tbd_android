package com.star.theBigDipper.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.star.theBigDipper.BR
import com.star.theBigDipper.IntentKey
import com.star.theBigDipper.R
import com.star.theBigDipper.databinding.ActivityWebBinding
import com.star.theBigDipper.viewmodel.WebVM
import com.nbs.android.lib.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity<WebVM, ActivityWebBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_web

    override fun initView() {
        mViewModel.showBackImage.set(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                title_tv.text = title
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loading_pb.visibility = View.GONE
            }
        }
        webView.setDownloadListener { url, _, _, _, _ ->
            startBrowserDownload(url)
        }
        webView.setOnLongClickListener { false }
    }

    override fun initObserve() {
    }

    override fun initVariableId(): Int = BR.viewModel


    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        val url = intent.getStringExtra(IntentKey.WEB_URL)
        url?.let {
            webView.loadUrl(it)
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.setAppCacheEnabled(false)
    }

    private fun startBrowserDownload(url: String) {
        val intent1 = Intent(Intent.ACTION_VIEW)
        intent1.data = Uri.parse(url)
        startActivity(intent1)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            (webView.parent as ViewGroup).removeView(webView)
            webView.destroy()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}