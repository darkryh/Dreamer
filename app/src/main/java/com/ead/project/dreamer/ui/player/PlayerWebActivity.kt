package com.ead.project.dreamer.ui.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebSettings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools.Companion.hideSystemUI
import com.ead.project.dreamer.data.commons.Tools.Companion.clearData
import com.ead.project.dreamer.data.commons.Tools.Companion.onDestroy
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

    private lateinit var binding: ActivityPlayerWebBinding
    private val playerViewModel : PlayerViewModel by viewModels()
    lateinit var chapter: Chapter
    lateinit var playList : List<VideoModel>
    private var orientation : Int = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        AdBlocker.init(this)
        initVariables()
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = false
        settings.userAgentString = DreamerRequest.userAgent()
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.webViewClient = DreamerBlockClient()
        binding.webView.loadUrl(playList.last().directLink)
    }

    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            playList = it.parcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        orientation = resources.configuration.orientation
    }

    private fun updateChapter() {
        chapter.currentSeen = 1
        chapter.totalToSeen = 1
        chapter.lastSeen = Calendar.getInstance().time
        Constants.quantityAdPlus()
        playerViewModel.updateChapter(chapter)
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