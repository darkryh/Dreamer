package com.ead.project.dreamer.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.RecordsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordsManager: RecordsManager
): ViewModel() {

    fun getLiveDataRecords() : LiveData<List<Chapter>> = recordsManager.getRecords.livedata()

    fun updateContinuation(chapterList: List<Chapter>) =
        viewModelScope.launch (Dispatchers.IO) { recordsManager.configureRecords(chapterList) }

    fun checkIfUpgradeExist(chapterList: List<Chapter>) : Boolean =
        recordsManager.configureRecords.checkIfUpgradeExist(chapterList)
}