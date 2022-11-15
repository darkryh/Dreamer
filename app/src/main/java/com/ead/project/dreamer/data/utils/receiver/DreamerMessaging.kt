package com.ead.project.dreamer.data.utils.receiver

import android.util.Log
import com.ead.project.dreamer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class DreamerMessaging: FirebaseMessagingService() {

    private var instance: DreamerNotifier? = null
    private fun dreamerNotifier() = instance?: DreamerNotifier().also { instance = it }

    companion object {
        const val CHANNEL_APP_ID = 51
        const val CHANNEL_APP_KEY = "CHANNEL_APP_KEY"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("testing", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val currentNotifier = message.notification
        val notification = dreamerNotifier().notifier(
            currentNotifier?.title,
            currentNotifier?.body,
            R.drawable.ic_launcher_foreground,
            CHANNEL_APP_KEY
        )
        dreamerNotifier().notificationManager().notify(CHANNEL_APP_ID ,notification.build())
    }
}