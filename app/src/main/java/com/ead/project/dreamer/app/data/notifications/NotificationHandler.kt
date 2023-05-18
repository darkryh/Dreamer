package com.ead.project.dreamer.app.data.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ead.project.dreamer.presentation.main.MainActivity

object NotificationHandler {

    fun getToMainPendingIntent(context: Context,action : String) : PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.action = action
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}