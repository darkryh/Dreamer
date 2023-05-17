package com.ead.project.dreamer.app.data.ads

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.model.AdPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdPreferences @Inject constructor(
    private val store : DataStore<AdPreference>
) {

    companion object {
        private const val AD_PLAYER_LIMIT = 3
        private const val AD_WEB_LIMIT = 3
    }

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun shouldShowAdPlayer() = runBlocking { store.data.first().viewedTimes >= AD_PLAYER_LIMIT  }

    fun shouldShowAdWeb() = runBlocking { store.data.first().viewedTimes >= AD_WEB_LIMIT }

    fun addViewedTime() {
        scope.launch {
            store.updateData { adPreference: AdPreference ->
                adPreference.copy(
                    viewedTimes = adPreference.viewedTimes + 1
                )
            }
        }
    }

    fun resetViews() {
        scope.launch {
            store.updateData { adPreference: AdPreference ->
                adPreference.copy(
                    viewedTimes = 0
                )
            }
        }
    }
}