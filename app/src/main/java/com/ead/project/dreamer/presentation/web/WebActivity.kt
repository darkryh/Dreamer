package com.ead.project.dreamer.presentation.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.activity.showLongToast
import com.ead.commons.lib.metrics.getNavigationBarHeight
import com.ead.commons.lib.metrics.getScreenSize
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.discord.Discord
import com.ead.project.dreamer.app.data.util.HttpUtil
import com.ead.project.dreamer.data.network.DreamerWebView.Companion.TIMEOUT_MS
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.databinding.ActivityWebBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class WebActivity : AppCompatActivity() {

    private val viewModel : WebViewModel by viewModels()

    private var action = ACTION_NONE
    private var url = HttpUtil.BLANK_BROWSER
    private var host = "null"

    private var orientation : Int = 0

    private val binding : ActivityWebBinding by lazy {
        ActivityWebBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initVariables()
        initLayouts()
        initWebView()
        navigateUrl()
    }

    private fun initVariables() {
        intent.extras?.let {
            action = it.getInt(WEB_ACTION)
            url = it.getString(WEB_ACTION_URL)?:return@let
        }
        orientation = resources.configuration.orientation
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.apply {
            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString = DreamerRequest.userAgent()
        }
    }

    private fun initLayouts() {
        binding.apply {
            supportActionBar?.hide()

            webView.layoutParams.height = (getScreenSize().height - linearBar.height) -
                    getNavigationBarHeight(orientation)

            host = URI(url).host
            textUrl.text = host

            imageClose.setOnClickListener {
                finish()
            }
        }
    }

    private fun settingWebView(webView: WebView?) {
        binding.apply {
            if (webView != null) {
                if (!webView.title?.contains("/")!!) {
                    textTitle.text = webView.title
                } else {
                    textTitle.text = host
                }
            }
        }
    }

    private fun navigateUrl() {
        binding.apply {
            webView.webViewClient = object : WebViewClient() {

                var timeout = true

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    try {
                        settingWebView(view)
                        Thread.runInMs({
                            if (timeout) {
                                showLongToast(getString(R.string.timeout_message))
                                finish()
                            }
                        }, TIMEOUT_MS)
                    } catch (e: InterruptedException) { e.printStackTrace() }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    timeout = false
                    if (url != null) selectAction(url)
                }
            }
            webView.loadUrl(url)
        }

    }

    private fun selectAction(url : String) {
        when (action) {
            ACTION_LOGIN_DISCORD -> {
              loginWithDiscord(url)
            }
        }
    }

    private fun loginWithDiscord(url: String) {
        if (url.isDiscordUserLogged()) {
            Discord.setExchangeCode(url.getDiscordExchangeCode())
            getDiscordToken()
        }
    }

    private fun getDiscordToken() {
        viewModel.getToken().observe(this) { discordToken ->
            if (discordToken != null) {
                finish()
            }
        }
    }

    private fun String.isDiscordUserLogged() : Boolean {
        return contains(Discord.IS_LOGGED) && !contains(Discord.REDIRECT_URI_REF)
    }

    private fun String.getDiscordExchangeCode() : String {
        return substringAfter("code=").substringBefore("&state=")
    }

    companion object {
        const val ACTION_NONE = 0
        const val ACTION_LOGIN_DISCORD = 1

        const val WEB_ACTION = "WEB_ACTION"
        const val WEB_ACTION_URL = "WEB_ACTION_URL"
    }
}