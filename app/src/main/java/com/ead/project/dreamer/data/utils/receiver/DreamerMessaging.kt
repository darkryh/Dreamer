package com.ead.project.dreamer.data.utils.receiver

import android.util.Log
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DreamerMessaging: FirebaseMessagingService() {

    @Inject lateinit var notifier: NotificationManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("testing", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val currentNotifier = message.notification
        val notification = notifier.create(
            title = currentNotifier?.title,
            content = currentNotifier?.body,
            idDrawable = R.drawable.ic_launcher_foreground,
            channelKey =  CHANNEL_APP_KEY
        )
        notifier.notify(CHANNEL_APP_ID ,notification.build())
    }

    companion object {
        const val CHANNEL_APP_ID = 51
        const val CHANNEL_APP_KEY = "CHANNEL_APP_KEY"
    }
}