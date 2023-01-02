package com.ead.project.dreamer.data.utils

import android.content.Context
import android.graphics.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ead.project.dreamer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.inject.Inject


class NotificationBuilder @Inject constructor(private val context: Context) {

    fun create(title : String? = null, content : String? = null,
               idDrawable : Int, channelKey : String,
               imageUrl: String? = null, useCustomLayout : Boolean = false
    ) : NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(context,channelKey).apply {
            setSmallIcon(idDrawable)
            color = ContextCompat.getColor(context, R.color.blue_light)
            setAutoCancel(true)
            setColorized(true)
            if (useCustomLayout) {
                val collapsed = collapsed()
                val expanded = expanded()
                setStyle(NotificationCompat.DecoratedCustomViewStyle())
                setCustomContentView(collapsed)
                setCustomBigContentView(expanded)
                bindCustomNotification(
                    collapsed,
                    expanded,
                    title,
                    content,
                    imageUrl
                )
            } else {
                if (imageUrl != null) loadImage(imageUrl)
                setContentTitle(title)
                setContentText(content)
            }
        }

        return notificationBuilder
    }

    private fun bindCustomNotification(
        remoteCollapsed: RemoteViews,
        remoteExpanded : RemoteViews,
        title: String?,
        content: String?, imageUrl: String?) {
        if (title != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_title,title)
            remoteExpanded.setTextViewText(R.id.expanded_notification_title,title)
        }
        if (content != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_info,content)
            remoteExpanded.setTextViewText(R.id.expanded_notification_info,content)
        }
        if (imageUrl != null) {
            remoteCollapsed.loadImage(imageUrl,R.id.image_view_collapsed,
                NOTIFICATION_SMALL_CORNER_SIZE_DP)
            remoteExpanded.loadImage(imageUrl,R.id.image_view_expanded,
                NOTIFICATION_EXPANDED_CORNER_SIZE_DP)
        }
    }

    private fun NotificationCompat.Builder.loadImage(
        imageUrl: String
    ) = runBlocking {
        val url = URL(imageUrl)
        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) { null }
        }?.let { bitmap ->
            setLargeIcon(bitmap)
            setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null))
        }
    }


    private fun RemoteViews.loadImage(
        imageUrl: String,
        idDrawable : Int,
        pixels: Int = 0
    ) = runBlocking {
        val url = URL(imageUrl)
        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) { null }
        }?.let { bitmap -> setImageViewBitmap(idDrawable,roundedCornerBitmap(bitmap,pixels)) }
    }

    private fun collapsed() : RemoteViews = RemoteViews(context.packageName, R.layout.layout_notification_small)
    private fun expanded() : RemoteViews = RemoteViews(context.packageName, R.layout.layout_notification_expanded)

    companion object {
        const val NOTIFICATION_SMALL_CORNER_SIZE_DP = 48
        const val NOTIFICATION_EXPANDED_CORNER_SIZE_DP = 16
    }
}