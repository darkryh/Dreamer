package com.ead.project.dreamer.app.data.marketing

import android.util.Log
import com.ead.project.dreamer.app.data.notifications.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InAppMessaging: FirebaseMessagingService() {

    @Inject lateinit var notifier: NotificationManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("InAppMessaging", "token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification = message.notification ?: return
        notifier.onMarketing(notification)
        notifier.createGroupMarketingSummaryNotification()
    }
}