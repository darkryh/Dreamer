package com.ead.project.dreamer.data.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ACTION = "NOTIFICATION_ACTION"
        const val PREFERENCE_DEACTIVATION = 0
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.extras?.getInt(NOTIFICATION_ACTION)) {
            PREFERENCE_DEACTIVATION -> {
                DataStore.writeBoolean(Constants.PREFERENCE_NOTIFICATIONS,false)
            }
        }
    }


}