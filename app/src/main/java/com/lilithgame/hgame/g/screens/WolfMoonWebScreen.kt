package com.lilithgame.hgame.g.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lilithgame.hgame.g.R
import com.lilithgame.hgame.g.databinding.ScreenWolfMoonWebBinding
import com.lilithgame.hgame.g.repository.WolfMoonRepository
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_BASE_LINK_DATA
import com.lilithgame.hgame.g.screens.WolfMoonSplashScreen.Companion.EXTRA_LINK_DATA

class WolfMoonWebScreen : AppCompatActivity() {
    private var messageAb: ValueCallback<Array<Uri?>>? = null
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ScreenWolfMoonWebBinding.inflate(layoutInflater)
    }
    private lateinit var webView: WebView
    private lateinit var base: String
    private lateinit var repo: WolfMoonRepository

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = resources.getColor(R.color.black, null)

        webView = binding.webView
        val url = requireNotNull(intent.getStringExtra(EXTRA_LINK_DATA))
        base = requireNotNull(intent.getStringExtra(EXTRA_BASE_LINK_DATA))
        webView.loadUrl(url)
        repo = WolfMoonRepository(getSharedPreferences("prefs", Context.MODE_PRIVATE))

        webView.webViewClient = Client()
        webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }

            @SuppressLint("SetJavaScriptEnabled")
            override fun onCreateWindow(
                view: WebView?, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(applicationContext)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    private fun selectImageIfNeed() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = IMAGE_MIME_TYPE
        startActivityForResult(
            Intent.createChooser(intent, IMAGE_CHOOSER_TITLE),
            RESULT_CODE
        )
    }

    private inner class Client : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (errorCode == -2) {
                Toast.makeText(this@WolfMoonWebScreen, "Error", Toast.LENGTH_LONG).show()
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            url?.let {
                if (it == base.substringAfter("https://").substringBefore("/")) {
                    startActivity(Intent(this@WolfMoonWebScreen, WolfMoonGameScreen::class.java))
                } else {
                    if (!repo.getIsSaved() && !it.contains(base)) {
                        repo.save(it)
                    }
                    CookieManager.getInstance().flush()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val IMAGE_CHOOSER_TITLE = "Image Chooser"
        private const val IMAGE_MIME_TYPE = "image/*"

        private const val RESULT_CODE = 1
    }
}