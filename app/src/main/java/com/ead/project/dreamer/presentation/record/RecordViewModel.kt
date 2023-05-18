package com.ead.project.dreamer.presentation.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.domain.RecordsUseCase
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordsUseCase: RecordsUseCase,
    val adManager: AdManager,
    val handleChapter: HandleChapter,
    val launchDownload: LaunchDownload
): ViewModel() {

    fun getLiveDataRecords() : LiveData<List<Chapter>> = recordsUseCase.getRecords.livedata()

    fun updateContinuation(chapterList: List<Chapter>) =
        viewModelScope.launch (Dispatchers.IO) { recordsUseCase.configureRecords(chapterList) }

    fun checkIfUpgradeExist(chapterList: List<Chapter>) : Boolean =
        recordsUseCase.configureRecords.checkIfUpgradeExist(chapterList)
}