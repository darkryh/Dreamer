package com.ead.project.dreamer.app.data.preference

import androidx.datastore.core.DataStore
import com.ead.project.dreamer.app.model.EadAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class EadPreferences @Inject constructor(
    private val store : DataStore<EadAccount?>
) {

    private val scope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val user get() = store.data

    fun login(eadAccount: EadAccount) {
        scope.launch {
            store.updateData { eadAccount }
        }
    }

    fun logout() {
        scope.launch {
            store.updateData { null }
        }
    }
}