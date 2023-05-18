package com.ead.project.dreamer.domain.servers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.ead.project.dreamer.app.data.util.system.launchActivity
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class LaunchToPlayerActivity @Inject constructor(
    preferenceUseCase: PreferenceUseCase
) {

    private val filesPreferences = preferenceUseCase.filesPreferences

    operator fun invoke(context: Context, chapter : Chapter, typeClass: Class<*>?, isDirect: Boolean = true) {
        (context as Activity).launchActivity(
            intent = Intent(context,typeClass).apply {
                putExtra(Chapter.REQUESTED, chapter)
                putExtra(Chapter.CONTENT_IS_DIRECT,isDirect)
                putParcelableArrayListExtra(
                    Chapter.PLAY_VIDEO_LIST,
                    toVideoModelArray(chapter) as java.util.ArrayList<out Parcelable>)
            }
        )
    }

    fun with(context: Context, chapter : Chapter,videoList: List<VideoModel>, typeClass: Class<*>?, isDirect: Boolean = true) {
        (context as Activity).launchActivity(
            intent = Intent(context,typeClass).apply {
                putExtra(Chapter.REQUESTED, chapter)
                putExtra(Chapter.CONTENT_IS_DIRECT,isDirect)
                putParcelableArrayListExtra(
                    Chapter.PLAY_VIDEO_LIST,
                    videoList as java.util.ArrayList<out Parcelable>)
            }
        )
    }

    private fun toVideoModelArray(chapter: Chapter) : ArrayList<VideoModel> =
        arrayListOf(VideoModel("default",filesPreferences.getChapterRoute(chapter)))
}