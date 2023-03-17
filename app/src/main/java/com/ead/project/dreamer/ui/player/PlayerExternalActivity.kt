package com.ead.project.dreamer.ui.player

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import coil.load
import com.ead.commons.lib.lifecycle.activity.onBack
import com.ead.commons.lib.lifecycle.parcelable
import com.ead.commons.lib.lifecycle.parcelableArrayList
import com.ead.commons.lib.views.setResourceColor
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.ui.player.content.PlayerContentFragment
import com.ead.project.dreamer.ui.player.content.chapterselector.ChapterSelectorFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PlayerExternalActivity : AppCompatActivity() {

    private val playerViewModel : PlayerViewModel by viewModels()

    private lateinit var lnPlaylist : LinearLayout

    private lateinit var cover : ImageView
    private lateinit var relaunchButton : Button
    private lateinit var closeButton: ImageView
    private lateinit var videoList : List<VideoModel>
    private lateinit var chapter: Chapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_external)
        init(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        init(true)
    }

    private fun init(isNewIntent : Boolean) {
        initVariables()
        initLayouts()
        if (!isNewIntent) settingThemeLayouts()
        initSettings()
        launchSuggestions()
        launchExternalCast()
    }

    private fun initLayouts() {
        cover = findViewById(R.id.coverChapterExternal)
        relaunchButton = findViewById(R.id.buttonRelaunchExternal)
        closeButton = findViewById(R.id.bt_close_external_player)
        lnPlaylist = findViewById(R.id.ln_play_list_cast)
    }

    private fun initVariables() {
        intent.extras?.let {
            chapter = it.parcelable(Constants.REQUESTED_CHAPTER)!!
            videoList = it.parcelableArrayList(Constants.PLAY_VIDEO_LIST)!!
        }
    }

    private fun settingThemeLayouts() {
        supportActionBar?.hide()
        val data = DataStore
            .readBoolean(Constants.PREFERENCE_THEME_MODE)

        if (data) closeButton.setResourceColor(R.color.whitePrimary)
        else closeButton.setResourceColor(R.color.blackPrimary)
    }

    private fun initSettings() {
        cover.apply {
            load(chapter.cover)
            alpha = 0.3f
        }
        relaunchButton.setOnClickListener {
            launchExternalCast()
        }
        lnPlaylist.setOnClickListener {
            val fragmentManager: FragmentManager = supportFragmentManager
            val chapterSelectorFragment = ChapterSelectorFragment()
            chapterSelectorFragment.show(fragmentManager, null)
        }
        closeButton.setOnClickListener { onBack() }
    }

    override fun onStop() {
        updateChapter()
        super.onStop()
    }

    private fun updateChapter() {
        chapter.currentSeen = 1
        chapter.totalToSeen = 1
        chapter.lastSeen = Calendar.getInstance().time
        Constants.quantityAdPlus()
        playerViewModel.updateChapter(chapter)
    }

    private fun launchSuggestions() {
        val fragment = PlayerContentFragment()
        val data = Bundle()
        val transaction = supportFragmentManager
            .beginTransaction()

        data.putParcelable(Constants.REQUESTED_CHAPTER,chapter)
        fragment.arguments = data
        transaction.replace(R.id.Frame_Content,fragment).commit()
    }

    private fun launchExternalCast() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoList.last().directLink))
        intent.setDataAndType(Uri.parse(videoList.last().directLink), "video/*")
        startActivity(intent)
    }

}