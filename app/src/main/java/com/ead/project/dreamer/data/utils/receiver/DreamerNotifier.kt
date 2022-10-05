package com.ead.project.dreamer.data.utils.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.*
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

    private lateinit var collapsed : RemoteViews
    private lateinit var expanded : RemoteViews

    fun notifier(
        title : String? = null,
        content : String? = null,
        idDrawable : Int,
        channelKey : String,
        notificationLevel : Int = 3,
        imageUrl: String? = null,
        pixelSizeSmall : Int = 48,
        pixelSizeExpanded: Int = 16,
        isCustom : Boolean = true
    ) : NotificationCompat.Builder {
        createNotificationChannel(channelKey,notificationLevel)
        val notifier = NotificationCompat.Builder(DreamerApp.INSTANCE,channelKey).apply {
            setSmallIcon(idDrawable)
            collapsed = collapsed()
            expanded = expanded()
            color = ContextCompat.getColor(DreamerApp.INSTANCE, R.color.blue_light)
            setAutoCancel(true)
            setColorized(true)
            if (isCustom) {
                setStyle(NotificationCompat.DecoratedCustomViewStyle())
                setCustomContentView(collapsed)
                setCustomBigContentView(expanded)
                bindCustomNotification(
                    collapsed,
                    expanded,
                    title,
                    content,
                    imageUrl,
                    pixelSizeSmall,
                    pixelSizeExpanded
                )
            }else {
                if (imageUrl != null) applyImageUrl(this,imageUrl)
                setContentTitle(title)
                setContentText(content)
            }
        }

        return notifier
    }

    private fun collapsed() = RemoteViews(DreamerApp.INSTANCE.packageName, R.layout.layout_notification_small)

    private fun expanded() = RemoteViews(DreamerApp.INSTANCE.packageName, R.layout.layout_notification_expanded)

    private fun bindCustomNotification(
        remoteCollapsed: RemoteViews,
        remoteExpanded : RemoteViews,
        title: String?,
        content: String?, imageUrl: String?,
        pixelSizeSmall: Int,
        pixelSizeExpanded: Int) {
        if (title != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_title,title)
            remoteExpanded.setTextViewText(R.id.expanded_notification_title,title)
        }
        if (content != null) {
            remoteCollapsed.setTextViewText(R.id.collapsed_notification_info,content)
            remoteExpanded.setTextViewText(R.id.expanded_notification_info,content)
        }
        if (imageUrl != null) {
            applyImageUrl(remoteCollapsed,imageUrl,R.id.image_view_collapsed,pixelSizeSmall)
            applyImageUrl(remoteExpanded,imageUrl,R.id.image_view_expanded,pixelSizeExpanded)
        }
    }

    private fun applyImageUrl(
        builder: NotificationCompat.Builder,
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
            builder.setLargeIcon(bitmap)
        }
    }

    private fun applyImageUrl(
        remoteViews: RemoteViews,
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
        }?.let { bitmap ->
            remoteViews.setImageViewBitmap(idDrawable,roundedCornerBitmap(bitmap,pixels))
        }
    }

    private fun roundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
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

    companion object {
        fun flagIntentImmutable() = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) PendingIntent.FLAG_UPDATE_CURRENT
        else PendingIntent.FLAG_IMMUTABLE

        fun flagIntentMutable() = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) PendingIntent.FLAG_UPDATE_CURRENT
        else PendingIntent.FLAG_MUTABLE

        const val ALL = 2
        const val FAVORITES = 1
        const val NONE = 0
    }
}