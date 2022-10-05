package com.ead.project.dreamer.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.TIMEOUT_MS
import com.ead.project.dreamer.data.retrofit.model.discord.Discord
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.ThreadUtil
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.databinding.ActivityWebBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class WebActivity : AppCompatActivity() {

    companion object {
        const val ACTION_NONE = 0
        const val ACTION_LOGIN_DISCORD = 1
    }

    private lateinit var binding : ActivityWebBinding
    private val webViewModel : WebViewModel by viewModels()
    private lateinit var screenSize : Size
    private var action = ACTION_NONE
    private var url = Constants.BLANK_BROWSER
    private var host = "null"
    private var orientation : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariables()
        settingLayouts()
        initWeb()
        layoutFunction()
        navigateUrl()
    }

    private fun initVariables() {
        action = intent.extras!!.getInt(Constants.WEB_ACTION)
        url = intent.extras!!.getString(Constants.WEB_ACTION_URL)!!
        orientation = resources.configuration.orientation
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.userAgentString = DreamerRequest.userAgent()
    }

    private fun settingLayouts() {
        supportActionBar?.hide()
        screenSize = Tools.getScreenSize(this)
        binding.webView.layoutParams.height = (screenSize.height - binding.lnBar.height) - Tools.getNavigationBarHeight(this,orientation)
        host = URI(url).host
        binding.txvUrl.text = host
    }

    private fun layoutFunction() {
        binding.imvClose.setOnClickListener {
            finish()
        }
    }

    private fun settingWeb(webView: WebView?) {
        if (webView != null) {
            if (!webView.title?.contains("/")!!) {
                binding.txvTitle.text = webView.title
            } else {
                binding.txvTitle.text = host
            }
        }
    }

    private fun navigateUrl() {
        binding.webView.webViewClient = object : WebViewClient() {

            var timeout = true

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                try {
                    settingWeb(view)
                    ThreadUtil.runInMs({
                        if (timeout) {
                            DreamerApp.showLongToast(getString(R.string.timeout_message))
                            finish()
                        }
                    }, TIMEOUT_MS)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                timeout = false
                if (url != null) selectAction(url)
            }
        }
        binding.webView.loadUrl(url)
    }

    private fun selectAction(url : String) {
        when (action) {
            ACTION_LOGIN_DISCORD -> {
              loginWithDiscord(url)
            }
        }
    }

    private fun loginWithDiscord(url: String) {
        val code: String
        if (url.contains(Discord.IS_LOGGED) && !url.contains(Discord.REDIRECT_URI_REF)) {
            code = url
                .substringAfter("code=").substringBefore("&state=")
            DataStore.writeString(Discord.EXCHANGE_CODE, code)
            getToken()
        }
    }

    private fun getToken() {
        webViewModel.getToken().observe(this) {
            if (it != null) {
                finish()
            }
        }
    }
}