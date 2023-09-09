package com.ead.project.dreamer.app.data.util.system

import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

fun NotificationCompat.Builder.loadImage(imageUrl: String) = runBlocking {
    val url = URL(imageUrl)
    withContext(Dispatchers.IO) {
        try {
            val input = url.openStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }?.let { bitmap ->
        setLargeIcon(bitmap)
        setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(bitmap))
    }
}