package com.ead.project.dreamer.domain.servers

import android.content.Context
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import javax.inject.Inject

class LaunchVideo @Inject constructor(
    private val getPlayerType: GetPlayerType,
    private val launchToPlayerActivity: LaunchToPlayerActivity
) {

    operator fun invoke(context: Context, chapter: Chapter, isDirect: Boolean) {
        launchToPlayerActivity(context, chapter,getPlayerType(isDirect,chapter))
    }

    fun with(context: Context, chapter: Chapter,videoList: List<VideoModel>, isDirect: Boolean) {
        launchToPlayerActivity.with(context, chapter, videoList, getPlayerType(isDirect,chapter))
    }
}