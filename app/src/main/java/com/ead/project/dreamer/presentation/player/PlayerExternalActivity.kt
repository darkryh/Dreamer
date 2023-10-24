package com.ead.project.dreamer.presentation.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import coil.load
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.commons.lib.views.setResourceColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.databinding.ActivityPlayerExternalBinding
import com.ead.project.dreamer.presentation.player.content.PlayerContentFragment
import com.ead.project.dreamer.presentation.player.content.chapterselector.ChapterSelectorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerExternalActivity : AppCompatActivity() {

    private val viewModel : PlayerViewModel by viewModels()

    private lateinit var videoList : List<VideoModel>
    private lateinit var chapter: Chapter

    private val binding : ActivityPlayerExternalBinding by lazy {
        ActivityPlayerExternalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        init(true)
    }

    private fun init(isNewIntent : Boolean) {
        initVariables()
        if (!isNewIntent) {
            settingThemeLayouts()
        }
        initSettings()
        launchSuggestions()
        launchExternalCast()
    }

    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Chapter.REQUESTED)?:return@let
            videoList = it.parcelableArrayList(Chapter.PLAY_VIDEO_LIST)?:return@let
        }
    }

    private fun settingThemeLayouts() {
        supportActionBar?.hide()
        binding.apply {
            val data = true
            //DataStore //.readBoolean(Constants.PREFERENCE_THEME_MODE)
            if (data) {
                buttonClose.setResourceColor(R.color.whitePrimary)
            }
            else {
                buttonClose.setResourceColor(R.color.blackPrimary)
            }
        }
    }

    private fun initSettings() {
        binding.apply {

            cover.apply {
                load(chapter.cover)
                alpha = 0.3f
            }

            buttonRelaunchExternal.setOnClickListener {
                launchExternalCast()
            }

            linearPlaylistCast.setOnClickListener {
                val fragmentManager: FragmentManager = supportFragmentManager
                val chapterSelectorFragment = ChapterSelectorFragment()
                chapterSelectorFragment.show(fragmentManager, null)
            }

            buttonClose.setOnClickListener {
                onBack()
            }
        }
    }

    override fun onStop() {
        updateChapter()
        super.onStop()
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

    private fun launchSuggestions() {
        val fragment = PlayerContentFragment()
        val data = Bundle()
        val transaction = supportFragmentManager
            .beginTransaction()

        data.putParcelable(Chapter.REQUESTED,chapter)
        fragment.arguments = data
        transaction.replace(R.id.frame_content,fragment).commit()
    }

    private fun launchExternalCast() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoList.last().directLink))
        intent.setDataAndType(Uri.parse(videoList.last().directLink), "video/*")
        startActivity(intent)
    }

}