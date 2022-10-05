package com.ead.project.dreamer.ui.settings.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.worker.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class SettingsFixerViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val constraints: Constraints
) : ViewModel() {

    fun getConnectionState(url: String) : MutableLiveData<Int> {
        val state : MutableLiveData<Int> = MutableLiveData(0)
        viewModelScope.launch (Dispatchers.IO) {
            state.postValue(getConnection(url))
        }
        return state
    }

    fun synchronizeScrapper() {
        val syncingRequest =
            OneTimeWorkRequestBuilder<ScrapperWorker>()
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            Constants.SYNC_SCRAPPER,
            ExistingWorkPolicy.REPLACE,
            syncingRequest)
    }


    private fun getConnection(url : String) : Int {
        return try {
            when (Jsoup.connect(url)
                .ignoreContentType(true)
                .method(Connection.Method.GET)
                .execute()
                .statusCode()) {
                200 -> 1
                else -> 0
            }
        } catch (e : Exception) {
            Log.d("testing", "getConnection: ${e.cause}")
            -1
        }
    }
}