package com.ead.project.dreamer.data.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.receiver.NotificationReceiver
import com.ead.project.dreamer.data.worker.HomeWorker
import javax.inject.Inject

class NotificationManager @Inject constructor(context: Context) {

    private val notificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationBuilder : NotificationBuilder = NotificationBuilder(context)
    val activeNotifications : Array<StatusBarNotification>  = notificationManager.activeNotifications

    fun create(
        title : String? = null,
        content : String? = null,
        idDrawable : Int,
        channelKey : String,
        notificationLevel : Int = 3,
        imageUrl: String? = null,
        useCustomLayout : Boolean = false
    ) : NotificationCompat.Builder {
        createNotificationChannel(channelKey,notificationLevel)
        return notificationBuilder.create(title,content,idDrawable,channelKey, imageUrl,useCustomLayout)
    }


    private fun createNotificationChannel(Channel_Key : String,notificationLevel: Int)  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val name = "Dreamer_notification"
            val content = "Dreamer_content_notification"

            val importance = when(notificationLevel) {
                3 -> NotificationManager.IMPORTANCE_HIGH
                2 -> NotificationManager.IMPORTANCE_DEFAULT
                1 -> NotificationManager.IMPORTANCE_LOW
                else -> NotificationManager.IMPORTANCE_UNSPECIFIED
            }

            val channel = NotificationChannel(Channel_Key,name,importance).apply {
                description = content
                setSound(sound,audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getPendingIntent(context: Context, action : String) : PendingIntent {
        val broadCastIntent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
            putExtra(NotificationReceiver.NOTIFICATION_ID, CHANNEL_SETTINGS_ID)
        }

        return PendingIntent.getBroadcast(
            context,0,
            broadCastIntent,
            flagIntentMutable()
        )
    }

    fun notify(id : Int,notification: Notification) = notificationManager.notify(id, notification)

    fun cancel(id : Int) = notificationManager.cancel(id)

    companion object {
        fun flagIntentImmutable() : Int = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) PendingIntent.FLAG_UPDATE_CURRENT
        else PendingIntent.FLAG_IMMUTABLE

        fun flagIntentMutable() : Int = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) PendingIntent.FLAG_UPDATE_CURRENT
        else PendingIntent.FLAG_MUTABLE

        const val ALL = 2
        const val FAVORITES = 1
        const val NONE = 0

        private const val CHANNEL_SETTINGS_ID = 1500

        fun showSettingNotification(notifier : com.ead.project.dreamer.data.utils.NotificationManager,context: Context) {
            val notification = notifier.create(
                title = context.getString(R.string.first_time_notification_title),
                content = context.getString(R.string.first_time_notification_description),
                idDrawable = R.drawable.ic_launcher_foreground,
                channelKey = HomeWorker.CHANNEL_APP_KEY_SERIES,
                imageUrl = Constants.LOGO_URL
            ).apply {
                setOnlyAlertOnce(true)
                addAction(0,"Todos",notifier.getPendingIntent(context, NotificationReceiver.PREFERENCE_ACTIVATION_ALL))
                addAction(0,"Solo favoritos",notifier.getPendingIntent(context, NotificationReceiver.PREFERENCE_ACTIVATION_FAVORITES))
                addAction(0,"Ninguno",notifier.getPendingIntent(context, NotificationReceiver.PREFERENCE_DEACTIVATION))
            }
            notifier.notify(CHANNEL_SETTINGS_ID,notification.build())
            Constants.disableFirstTimeShowingNotifications()
        }
    }
}