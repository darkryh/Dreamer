package com.ead.project.dreamer.data.worker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context,workerParameters) {

    lateinit var repository: AnimeRepository
    lateinit var webProvider: WebProvider
    lateinit var notifier : DreamerNotifier

    private lateinit var notification : NotificationCompat.Builder

    private val listToNotify : MutableList<ChapterHome> = ArrayList()

    companion object {
        const val CHANNEL_APP_SERIES_ID = 250
        const val CHANNEL_APP_KEY_SERIES = "CHANNEL_APP_KEY_SERIES"
        const val GROUP_KEY_NOTIFICATIONS = "GROUP_KEY_NOTIFICATIONS"

        const val NotificationTitle = "Notificaciones"
        const val NotificationContent = "desactivar notificaciones, en estreno?"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (DataStore.readBoolean(Constants.PREFERENCE_NOTIFICATIONS,true)) {
                    val releaseList = repository.getChaptersHome()
                    val previousList = ChapterHome.getPreviousList()

                    previousList.apply {
                        if (isNotEmpty()) {
                            for (chapter in releaseList)
                                if (!contains(chapter.title))
                                    listToNotify.add(chapter)

                            for (i in listToNotify.indices) {
                                val chapter = listToNotify[i]
                                notification(chapter, i)
                            }
                        }
                        else notificationSetting()
                    }
                    ChapterHome.setPreviousList(releaseList.map { it.title })
                }
                Result.success()
            }
            catch (ex : IOException) {
                ex.printStackTrace()
                Result.failure()
            }
        }
    }


    private fun notification(chapter: ChapterHome,index : Int) {
        notification = notifier.notifier(
            chapter.title,
            "Capítulo numero ${chapter.chapterNumber}",
            R.drawable.ic_launcher_foreground,
            CHANNEL_APP_KEY_SERIES,
            imageUrl = chapter.chapterCover
        ).apply {
                setContentIntent(getPendingIntent())
            }
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N)
            notification.apply {
                setGroup(GROUP_KEY_NOTIFICATIONS)
                setGroupSummary(true)
            }

        notifier.notificationManager().notify(CHANNEL_APP_SERIES_ID + index,notification.build())
    }

    private fun notificationSetting() {
        notification = notifier.notifier(
            NotificationTitle,
            NotificationContent,
            R.drawable.ic_launcher_foreground,
            CHANNEL_APP_KEY_SERIES,
            imageUrl = "https://i.ibb.co/6nfLSKL/logo-app.png"
        ).apply {
            setOnlyAlertOnce(true)
            addAction(R.drawable.ic_notifications_off_24,"No",getPendingIntentSetting())
        }

        notifier.notificationManager().notify(CHANNEL_APP_SERIES_ID,notification.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent() : PendingIntent {
        val notificationIntent = Intent(DreamerApp.INSTANCE, MainActivity::class.java)
        notificationIntent.apply {
            flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return PendingIntent.getActivity(
            DreamerApp.INSTANCE, 0,
            notificationIntent, 0
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntentSetting() : PendingIntent {
        val broadCastIntent = Intent(DreamerApp.INSTANCE, NotificationReceiver::class.java)
        broadCastIntent.apply {
            putExtra(
                NotificationReceiver.NOTIFICATION_ACTION,
                NotificationReceiver.PREFERENCE_DEACTIVATION
            )
        }

        return PendingIntent.getBroadcast(
            DreamerApp.INSTANCE,0,
            broadCastIntent,PendingIntent.FLAG_UPDATE_CURRENT)
    }
}