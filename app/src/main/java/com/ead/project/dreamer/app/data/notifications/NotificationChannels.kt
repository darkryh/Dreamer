package com.ead.project.dreamer.app.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.util.system.notificationManager

object NotificationChannels {

    const val CHANNEL_NEW_EPISODES = "new_episodes"
    const val CHANNEL_RECOMMENDATIONS = "recommendations"
    const val CHANNEL_WATCHLIST = "watchlist"
    const val CHANNEL_MARKETING = "marketing"

    fun initialize(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationManager = context.notificationManager

            val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channelNewEpisodes = NotificationChannel(
                CHANNEL_NEW_EPISODES,
                context.getString(R.string.channel_new_episodes),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_new_episodes_description)
                setSound(sound, audioAttributes)
            }

            val channelRecommendations = NotificationChannel(
                CHANNEL_RECOMMENDATIONS,
                context.getString(R.string.channel_recommendations),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_recommendations_description)
                setSound(sound, audioAttributes)
            }

            val channelWatchlist = NotificationChannel(
                CHANNEL_WATCHLIST,
                context.getString(R.string.channel_watch_list),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_watch_list_description)
                setSound(sound, audioAttributes)
            }

            val channelMarketing = NotificationChannel(
                CHANNEL_MARKETING,
                context.getString(R.string.channel_marketing),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_marketing_description)
                setSound(sound, audioAttributes)
            }

            val channels = listOf(
                channelNewEpisodes, channelRecommendations,
                channelWatchlist, channelMarketing
            )

            notificationManager.createNotificationChannels(channels)
        }
    }
}