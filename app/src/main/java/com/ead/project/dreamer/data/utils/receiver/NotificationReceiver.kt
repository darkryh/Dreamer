package com.ead.project.dreamer.data.utils.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ead.project.dreamer.app.data.notifications.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationManager: NotificationManager

    /*val pendingResult: PendingResult = goAsync()
    pendingResult.finish()*/

    //notifier.cancel(intent.getIntExtra(NOTIFICATION_ID, -1))
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            ACTION_PLAY_CHAPTER -> {

            }
            ACTION_DOWNLOAD_CHAPTER -> {

            }
            ACTION_ADD_FAVORITE_CHAPTER -> {

            }
            ACTION_REMOVE_FAVORITE_CHAPTER -> {

            }
        }
        notificationManager.cancelNotification(intent.getIntExtra(NOTIFICATION_ID, -1))
    }

    companion object {

        const val NOTIFICATION_ID = "123"

        private const val ACTION_PLAY_CHAPTER = "ACTION_INTENT_PLAY_CHAPTER"
        private const val ACTION_DOWNLOAD_CHAPTER = "ACTION_DOWNLOAD_CHAPTER"
        private const val ACTION_ADD_FAVORITE_CHAPTER = "ACTION_UPDATE_FAVORITE_CHAPTER"
        private const val ACTION_REMOVE_FAVORITE_CHAPTER = "ACTION_REMOVE_FAVORITE_CHAPTER"

        fun playChapterPendingBroadcast(context: Context) : PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                action = ACTION_PLAY_CHAPTER
            }

            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        fun downloadChapterPendingBroadcast(context: Context) : PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                action = ACTION_DOWNLOAD_CHAPTER
            }

            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        fun addToFavoriteSeries(context: Context) : PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                action = ACTION_ADD_FAVORITE_CHAPTER
            }
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun removeToFavoriteSeries(context: Context) : PendingIntent {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                action = ACTION_REMOVE_FAVORITE_CHAPTER
            }
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}