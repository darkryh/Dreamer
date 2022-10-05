package com.ead.project.dreamer.data.utils.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val PREFERENCE_DEACTIVATION = "PREFERENCE_DEACTIVATION"
        const val PREFERENCE_ACTIVATION_FAVORITES = "PREFERENCE_ACTIVATION_FAVORITES"
        const val PREFERENCE_ACTIVATION_ALL = "PREFERENCE_ACTIVATION_ALL"
        const val NOTIFICATION_ID = "123"
    }

    private val scope = CoroutineScope(SupervisorJob())
    private val notifier = DreamerNotifier().notificationManager()

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult: PendingResult = goAsync()
        scope.launch(Dispatchers.Default) {
            try {
                val id = intent?.getIntExtra(NOTIFICATION_ID, -1) ?: -1
                when(intent?.action) {
                    PREFERENCE_DEACTIVATION -> DataStore.writeIntAsync(Constants.PREFERENCE_NOTIFICATIONS,0)
                    PREFERENCE_ACTIVATION_FAVORITES -> DataStore.writeIntAsync(Constants.PREFERENCE_NOTIFICATIONS,1)
                    PREFERENCE_ACTIVATION_ALL -> DataStore.writeIntAsync(Constants.PREFERENCE_NOTIFICATIONS,2)
                }
                notifier.cancel(id)
            } finally {
                // Must call finish() so the BroadcastReceiver can be recycled
                pendingResult.finish()
            }
        }

    }

}