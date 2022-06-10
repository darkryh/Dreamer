package com.ead.project.dreamer.ui.player

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import androidx.activity.viewModels
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.database.model.VideoModel
import com.ead.project.dreamer.data.network.AdBlocker
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        AdBlocker.init(this)
        initVariables()
        val settings = binding.webView.settings
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.loadUrl(playList.last().directLink)
    }

    private fun initVariables() {
        chapter = intent.extras!!.getParcelable(Constants.REQUESTED_CHAPTER)!!
        playList = intent.extras!!.getParcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
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
        Tools.hideSystemUI(this,binding.root)
    }

    override fun onStop() {
        updateChapter()
        super.onStop()
    }
}