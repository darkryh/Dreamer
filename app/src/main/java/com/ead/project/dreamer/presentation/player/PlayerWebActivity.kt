package com.ead.project.dreamer.presentation.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebSettings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.app.data.util.system.clearData
import com.ead.project.dreamer.app.data.util.system.hideSystemUI
import com.ead.project.dreamer.app.data.util.system.onDestroy
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.network.AdBlocker
import com.ead.project.dreamer.data.network.DreamerBlockClient
import com.ead.project.dreamer.data.utils.receiver.DreamerRequest
import com.ead.project.dreamer.databinding.ActivityPlayerWebBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class PlayerWebActivity : AppCompatActivity() {

    private val viewModel : PlayerViewModel by viewModels()

    lateinit var chapter: Chapter
    lateinit var playlist : List<VideoModel>

    private var orientation : Int = 0

    private val binding: ActivityPlayerWebBinding by lazy {
        ActivityPlayerWebBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        initAdBlocker()
        initVariables()
        configWebView()
        loadChapter()

        hideSystemUI()
    }
    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            playlist = it.parcelableArrayList(Chapter.PLAY_VIDEO_LIST)?:return@let
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        orientation = resources.configuration.orientation
    }

    private fun initAdBlocker() {
        AdBlocker.init(this)
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun configWebView() {
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = false
        settings.userAgentString = DreamerRequest.userAgent()
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.webViewClient = DreamerBlockClient()
    }

    private fun loadChapter() {
        binding.webView.loadUrl(playlist.last().directLink)
    }

    private fun updateChapter() {
        chapter = chapter.copy(
            currentProgress = 1,
            totalProgress = 1,
            lastDateSeen = TimeUtil.getNow()
        )
        viewModel.addViewedTime()
        viewModel.updateChapter(chapter)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSystemUI()
    }

    override fun onStop() {
        updateChapter()
        super.onStop()
    }

    override fun onDestroy() {
        binding.webView.clearData()
        binding.webView.onDestroy()
        super.onDestroy()
    }
}