package com.ead.project.dreamer.data.utils.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.inject.Inject


class DreamerNotifier @Inject constructor() {

    private var instance : NotificationManager? = null

    fun notificationManager() = instance?: (DreamerApp.INSTANCE
            .getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager).also {
            instance = it
        }


    fun notifier(
        title : String? = null,
        content : String? = null,
        idDrawable : Int,
        channelKey : String,
        notificationLevel : Int = 3,
        imageUrl: String? = null
    ) : NotificationCompat.Builder {
        createNotificationChannel(channelKey,notificationLevel)
        val notifier = NotificationCompat.Builder(DreamerApp.INSTANCE,channelKey).apply {
            setSmallIcon(idDrawable)
            if (title != null) setContentTitle(title)
            if (content != null) setContentText(content)
            if (imageUrl != null) applyImageUrl(this,imageUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) color = DreamerApp.INSTANCE.resources
                .getColor(R.color.blueLight, DreamerApp.INSTANCE.theme)
            setAutoCancel(true)
            setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            setColorized(true)
        }
        return notifier
    }

    //private fun collapsed() = RemoteViews(DreamerApp.INSTANCE.packageName, R.layout.layout_notification_small)

    //private fun expanded() = RemoteViews(DreamerApp.INSTANCE.packageName, R.layout.layout_notification_expanded)

    /*private fun bindCustomNotification(
        remoteCollapsed: RemoteViews,
        remoteExpanded : RemoteViews,
        title: String?,
        content: String?) {
        if (title != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_title,title)
            remoteExpanded.setTextViewText(R.id.expanded_notification_title,title)
        }
        if (content != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_info,content)
            remoteExpanded.setTextViewText(R.id.expanded_notification_info,content)
        }
    }*/

    private fun applyImageUrl(
        notifier : NotificationCompat.Builder,
        imageUrl: String
    ) = runBlocking {
        val url = URL(imageUrl)

        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                null
            }
        }?.let { bitmap ->
            notifier.setLargeIcon(bitmap)
        }
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

            notificationManager().createNotificationChannel(channel)
        }
    }
}