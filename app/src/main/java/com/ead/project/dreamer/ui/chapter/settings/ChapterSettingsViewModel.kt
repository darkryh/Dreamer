package com.ead.project.dreamer.ui.chapter.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ChapterSettingsViewModel @Inject constructor(
    private val objectManager: ObjectManager,
    private val profileManager: ProfileManager,
    private val recordsManager: RecordsManager
): ViewModel() {

    fun updateFavoriteProfile(id : Int) =
        viewModelScope.launch (Dispatchers.IO) {
            val animeProfile = profileManager.getProfile(id)
            animeProfile?.let {
                it.isFavorite = !it.isFavorite
                objectManager.updateObject(it)
            }
        }

    fun deleteRecords(id : Int) =
        viewModelScope.launch (Dispatchers.IO) {
            val chapters : List<Chapter> = recordsManager.getRecords.fromId(id)
            objectManager.updateObject(chapters.onEach { it.currentSeen = 0 })
        }

    fun getProfileIsFavorite(id : Int) : AnimeProfile? = runBlocking { profileManager.getProfile(id) }

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectManager.updateObject(chapter) }
}