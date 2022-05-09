package com.ead.project.dreamer.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {

    fun getLiveDataRecords() : LiveData<List<Chapter>> = repository.getFlowChaptersRecord().asLiveData()

    fun updateContinuation(chapterList: List<Chapter>) {
        viewModelScope.launch (Dispatchers.IO) {
            val nexToChapterList : MutableList<Chapter> = ArrayList()
            var isUpgradeable = false
            for (chapter in chapterList) {
                if (chapter.alreadySeen) {
                    chapter.alreadySeen = false
                    isUpgradeable = true
                    val nextChapter = repository
                        .getChapterFromTitleAndNumber(
                            chapter.title,
                            chapter.chapterNumber + 1)

                    if (nextChapter != null) {
                        nextChapter.currentSeen = 1
                        nextChapter.lastSeen = Calendar.getInstance().time
                        nexToChapterList.add(nextChapter)
                    }
                }
            }
            if (isUpgradeable) {
                nexToChapterList.addAll(chapterList)
                repository.updateChapters(nexToChapterList.reversed())
            }
        }
    }

    fun checkIfUpgradeExist(chapterList: List<Chapter>) : Boolean {
        for (chapter in chapterList)
            if (chapter.alreadySeen)
                return true

        return false
    }
}