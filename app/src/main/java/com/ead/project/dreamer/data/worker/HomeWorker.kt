package com.ead.project.dreamer.data.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.app.model.scrapping.ChapterHomeScrap
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.network.WebProvider
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.receiver.DreamerNotifier
import com.ead.project.dreamer.data.utils.receiver.NotificationReceiver
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
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider
    lateinit var notifier : DreamerNotifier

    private lateinit var notification : NotificationCompat.Builder
    private val listToNotify : MutableList<ChapterHome> = ArrayList()
    private var currentPos = Constants.getNotificationIndex()
    private var isGroupedNeeded = false
    private lateinit var activeNotifications : Array<StatusBarNotification>

    private val allNotifications = getPendingIntentSetting(NotificationReceiver.PREFERENCE_ACTIVATION_ALL)
    private val favoriteNotifications = getPendingIntentSetting(NotificationReceiver.PREFERENCE_ACTIVATION_FAVORITES)
    private val noNotifications = getPendingIntentSetting(NotificationReceiver.PREFERENCE_DEACTIVATION)

    companion object {
        const val CHANNEL_APP_SUMMARY = 0
        const val CHANNEL_APP_SERIES_ID = 250
        const val CHANNEL_SETTINGS_ID = 1500
        const val CHANNEL_APP_KEY_SERIES = "CHANNEL_APP_KEY_SERIES"
        const val GROUP_KEY_NOTIFICATIONS = "GROUP_KEY_NOTIFICATIONS"

        const val NotificationTitle = "Configuración notificaciones"
        const val NotificationContent = "Escoja el modo, en que desea recibir notificaciones de tus animes."

        const val NotificationSummaryTitle = "Nuevos estrenos."
        const val NotificationSummaryContent = "animes en estreno."
    }

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

    private suspend fun homeOperator(scope: CoroutineScope) {
        scope.apply {
            val chapterHomeList = repository.getChaptersHome()
            val chapterHomeScrap : ChapterHomeScrap =
                ChapterHomeScrap.get()?:ChapterHomeScrap.getDataFromApi(repository)

            val isDataEmpty = chapterHomeList.isEmpty()
            val chapter = if (isDataEmpty) ChapterHome.fake()
            else repository.getChaptersHome().last()

            val homeData = async { webProvider.getChaptersHome(chapter,chapterHomeScrap) }
            homeData.await().apply {
                if (isDataEmpty) repository.insertAllChaptersHome(this)
                else repository.updateHome(this)
                Result.success()
            }
        }
    }

    private suspend fun notificationOperator(scope: CoroutineScope) {
        scope.apply {
            val notificationLevel = Constants.getNotificationMode()
            activeNotifications = notifier.notificationManager().activeNotifications
            if (!isFirstTimeNotification()) notificationSetting()
            if (notificationLevel > DreamerNotifier.NONE) {
                isGroupedNeeded = isGroupedNeeded()
                val releaseList = repository.getChaptersHome()
                val favoriteList = repository.getFavoriteProfileReleasesTitles()
                val previousList = ChapterHome.getPreviousList()
                previousList.apply {
                    if (isNotEmpty()) {
                        if (notificationLevel == DreamerNotifier.FAVORITES) {
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

    private suspend fun isFirstTimeNotification() : Boolean =
        DataStore.readBooleanAsync(Constants.SYNC_NOTIFICATIONS_FIRST_TIME)

    private fun notification(chapter: ChapterHome, index : Int,
                             isSummaryNeeded : Boolean,isAutoCancelNeeded : Boolean) {
        notification = notifier.notifier(
            chapter.title,
            "Capítulo numero ${chapter.chapterNumber}",
            R.drawable.ic_launcher_foreground,
            CHANNEL_APP_KEY_SERIES,
            imageUrl = chapter.chapterCover,
            isCustom = false
        ).apply {
            setContentIntent(getPendingIntent())
            setGroup(GROUP_KEY_NOTIFICATIONS)
            notifier.notificationManager().notify(CHANNEL_APP_SERIES_ID + index + 1,this.build())
        }
        if (isSummaryNeeded) {
            notifier.notifier(NotificationSummaryTitle,
                NotificationSummaryContent,
                R.drawable.ic_launcher_foreground,
                CHANNEL_APP_KEY_SERIES,
                isCustom = false).apply {
                setStyle(NotificationCompat.InboxStyle().setSummaryText(NotificationSummaryContent))
                setGroup(GROUP_KEY_NOTIFICATIONS)
                setGroupSummary(true)
                setAutoCancel(isAutoCancelNeeded)
                notifier.notificationManager().notify(CHANNEL_APP_SUMMARY,this.build())
                }
        }
    }

    private fun notificationSetting() {
        notification = notifier.notifier(
            NotificationTitle,
            NotificationContent,
            R.drawable.ic_launcher_foreground,
            CHANNEL_APP_KEY_SERIES,
            imageUrl = "https://i.ibb.co/6nfLSKL/logo-app.png",
            pixelSizeExpanded = 64,
            isCustom = false
        ).apply {
            setOnlyAlertOnce(true)
            addAction(0,"Todos",allNotifications)
            addAction(0,"Solo favoritos",favoriteNotifications)
            addAction(0,"Ninguno",noNotifications)
        }
        DataStore.writeBooleanAsync(Constants.SYNC_NOTIFICATIONS_FIRST_TIME,true)
        notifier.notificationManager().notify(CHANNEL_SETTINGS_ID,notification.build())
    }

    private fun updateNotificationsStatus() {
        val notifiedPos = currentPos + listToNotify.size
        if (notifiedPos >= Constants.HOME_ITEMS_LIMIT) Constants.setNotificationIndex(0)
        else Constants.setNotificationIndex(notifiedPos)
    }

    private fun getPendingIntent() : PendingIntent {
        val notificationIntent = Intent(DreamerApp.INSTANCE, MainActivity::class.java)
        notificationIntent.apply {
            flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return PendingIntent.getActivity(
            DreamerApp.INSTANCE, 0,
            notificationIntent,
            DreamerNotifier.flagIntentImmutable()

        )
    }

    private fun getPendingIntentSetting(value : String) : PendingIntent {
        val broadCastIntent = Intent(DreamerApp.INSTANCE, NotificationReceiver::class.java)
        broadCastIntent.apply {
            action = value
            putExtra(NotificationReceiver.NOTIFICATION_ID, CHANNEL_SETTINGS_ID)
        }

        return PendingIntent.getBroadcast(
            DreamerApp.INSTANCE,0,
            broadCastIntent,
            DreamerNotifier.flagIntentMutable())
    }

    private fun isGroupedNeeded() : Boolean {
        for (notify in activeNotifications) if (notify.id == CHANNEL_APP_SUMMARY) return true

        return activeNotifications.isEmpty()
    }

    private fun isSummaryNeeded(i : Int) = isGroupedNeeded &&  (i == listToNotify.size-1)

    private fun isAutoCancelNeeded() = (activeNotifications.size + listToNotify.size) <= 1
}