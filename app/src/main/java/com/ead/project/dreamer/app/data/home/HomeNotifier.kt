package com.ead.project.dreamer.app.data.home

import android.content.Context
import androidx.core.app.NotificationCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.notifications.NotificationChannels
import com.ead.project.dreamer.app.data.notifications.NotificationHandler
import com.ead.project.dreamer.app.data.util.system.getColorCompact
import com.ead.project.dreamer.app.data.util.system.loadImage
import com.ead.project.dreamer.app.data.util.system.notificationBuilder
import com.ead.project.dreamer.app.data.util.system.notify
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.presentation.main.MainActivity
import javax.inject.Inject

class HomeNotifier @Inject constructor(
    private val context : Context
) {

    private val chapterNotificationBuilder by lazy {
        context.notificationBuilder(NotificationChannels.CHANNEL_NEW_EPISODES) {
            setSmallIcon(R.drawable.ic_new_chapters)
            setAutoCancel(true)
            setOnlyAlertOnce(true)
            color = context.getColorCompact(R.color.orange_peel_dark)
            setColorized(true)
            setGroup(GROUP_SUMMARY_NEW_EPISODES)
        }
    }

    private val summaryBuilder by lazy {
        context.notificationBuilder(NotificationChannels.CHANNEL_NEW_EPISODES) {
            setContentTitle(context.getString(R.string.new_episodes_available))
            setSmallIcon(R.drawable.ic_new_chapters)
            color = context.getColorCompact(R.color.orange_peel_dark)
            setColorized(true)
            setGroup(GROUP_SUMMARY_NEW_EPISODES)
            setGroupSummary(true)
        }
    }

    private fun NotificationCompat.Builder.show(id : Int) {
        context.notify(id,build())
    }

    fun onChapterRelease(chapter : ChapterHome,notificationIndex : Int) {
        with(chapterNotificationBuilder) {
            clearActions()
            /*addAction(
                R.drawable.ic_play,
                context.getString(R.string.play_chapter),
                NotificationReceiver.playChapterPendingBroadcast(context)
            )
            addAction(
                R.drawable.ic_download,
                context.getString(R.string.download_chapter),
                NotificationReceiver.downloadChapterPendingBroadcast(context)
            )
            val isFavorite = true
            if (isFavorite) {
                addAction(
                    R.drawable.ic_favorite_border_24,
                    context.getString(R.string.remove_favorite_chapter),
                    NotificationReceiver.removeToFavoriteSeries(context)
                )
            }
            else {
                addAction(
                    R.drawable.ic_favorite_24,
                    context.getString(R.string.add_favorite_chapter),
                    NotificationReceiver.addToFavoriteSeries(context)
                )

            }*/

            setContentTitle(chapter.title)
            setContentText(context.getString(R.string.chapter_number,chapter.chapterNumber))
            setContentIntent(NotificationHandler.getToMainPendingIntent(context,MainActivity.CHAPTER_HOME_TARGET))
            loadImage(chapter.chapterCover)

            show(notificationIndex)
        }
    }

    fun createGroupSummaryNotification() {
        summaryBuilder.show(NEW_EPISODES_SUMMARY_ID)
    }

    companion object {
        private const val NEW_EPISODES_SUMMARY_ID = 0
        private const val GROUP_SUMMARY_NEW_EPISODES = "GROUP_SUMMARY_NEW_EPISODES"
    }
}