package com.ead.project.dreamer.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.NotificationManager
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.ObjectUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.ui.main.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class HomeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val homeUseCase: HomeUseCase,
    private val notifier: NotificationManager,
    private val objectUseCase: ObjectUseCase,
    private val profileUseCase: ProfileUseCase,
    private val webProvider: WebProvider
) : CoroutineWorker(context,workerParameters) {

    private lateinit var notification : NotificationCompat.Builder
    private val listToNotify : MutableList<ChapterHome> = ArrayList()
    private var currentPos = Constants.getNotificationIndex()
    private var isGroupedNeeded = false
    private lateinit var activeNotifications : Array<StatusBarNotification>

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                homeOperator(this)
                notificationOperator(this)
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }

    //UPDATE HOME

    private suspend fun homeOperator(scope: CoroutineScope) {
        scope.apply {
            val chapterHomeList = homeUseCase.getHomeList()

            val isDataEmpty = chapterHomeList.isEmpty()
            val chapter = if (isDataEmpty) ChapterHome.fake()
            else chapterHomeList.last()

            val homeData = async { webProvider.getChaptersHome(chapter) }
            homeData.await().apply {
                if (isDataEmpty) objectUseCase.insertObject(this)
                else objectUseCase.updateObject(this)
            }
        }
    }

    //CONFIGURATION NOTIFICATIONS

    private suspend fun notificationOperator(scope: CoroutineScope) {
        scope.apply {

            val notificationLevel = Constants.getNotificationMode()
            activeNotifications = notifier.activeNotifications

            if (notificationLevel > NotificationManager.NONE) {

                isGroupedNeeded = isGroupedNeeded()
                val releaseList = homeUseCase.getHomeList()
                val favoriteList = profileUseCase.getProfilesFavoriteReleases.stringList()
                val previousList = ChapterHome.getPreviousList()

                previousList.apply {
                    if (isNotEmpty()) {
                        if (notificationLevel == NotificationManager.FAVORITES) {
                            for (chapter in releaseList)
                                if (!contains(chapter.title) && favoriteList.contains(chapter.title))
                                    listToNotify.add(chapter)
                        }
                        else {
                            for (chapter in releaseList)
                                if (!contains(chapter.title))
                                    listToNotify.add(chapter)
                        }
                    }
                    for (i in listToNotify.indices) {
                        val chapter = listToNotify[i]
                        val index = currentPos + i
                        notification(chapter, index, isSummaryNeeded(i), isAutoCancelNeeded())
                    }
                    updateNotificationsStatus()
                }
                ChapterHome.setPreviousList(releaseList.map { it.title })
            }
        }
    }
    
    private fun notification(chapter: ChapterHome, index : Int,
                             isSummaryNeeded : Boolean,isAutoCancelNeeded : Boolean) {
        notification = notifier.create(
            title = chapter.title,
            content = context.getString(R.string.chapter_number,chapter.chapterNumber.toString()),
            idDrawable = R.drawable.ic_launcher_foreground,
            channelKey =  CHANNEL_APP_KEY_SERIES,
            imageUrl = chapter.chapterCover
        ).apply {
            setContentIntent(getPendingIntent())
            setGroup(GROUP_KEY_NOTIFICATIONS)
            notifier.notify(CHANNEL_APP_SERIES_ID + index + 1,this.build())
        }
        if (isSummaryNeeded) {
            notifier.create(
                title = context.getString(R.string.notification_summary_title),
                content = context.getString(R.string.notification_summary_description),
                idDrawable = R.drawable.ic_launcher_foreground,
                channelKey =  CHANNEL_APP_KEY_SERIES
            ).apply {
                setStyle(NotificationCompat.InboxStyle().setSummaryText(context.getString(R.string.notification_summary_description)))
                setGroup(GROUP_KEY_NOTIFICATIONS)
                setGroupSummary(true)
                setAutoCancel(isAutoCancelNeeded)
                notifier.notify(CHANNEL_APP_SUMMARY,this.build())
            }
        }
    }

    private fun updateNotificationsStatus() {
        val notifiedPos = currentPos + listToNotify.size
        if (notifiedPos >= Constants.HOME_ITEMS_LIMIT) Constants.setNotificationIndex(0)
        else Constants.setNotificationIndex(notifiedPos)
    }

    private fun getPendingIntent() : PendingIntent {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return PendingIntent.getActivity(
            context, 0,
            notificationIntent,
            NotificationManager.flagIntentImmutable()
        )
    }

    private fun isGroupedNeeded() : Boolean {
        for (notify in activeNotifications) if (notify.id == CHANNEL_APP_SUMMARY) return true

        return activeNotifications.isEmpty()
    }

    private fun isSummaryNeeded(i : Int) = isGroupedNeeded &&  (i == listToNotify.size-1)

    private fun isAutoCancelNeeded() = (activeNotifications.size + listToNotify.size) <= 1

    companion object {
        const val CHANNEL_APP_SUMMARY = 0
        const val CHANNEL_APP_SERIES_ID = 250
        const val CHANNEL_APP_KEY_SERIES = "CHANNEL_APP_KEY_SERIES"
        const val GROUP_KEY_NOTIFICATIONS = "GROUP_KEY_NOTIFICATIONS"
    }
}