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
    private val objectUseCase: ObjectUseCase,
    private val profileUseCase: ProfileUseCase,
    private val recordsUseCase: RecordsUseCase
): ViewModel() {

    fun updateFavoriteProfile(id : Int) =
        viewModelScope.launch (Dispatchers.IO) {
            val animeProfile = profileUseCase.getProfile(id)
            animeProfile?.let {
                it.isFavorite = !it.isFavorite
                objectUseCase.updateObject(it)
            }
        }

    fun deleteRecords(id : Int) =
        viewModelScope.launch (Dispatchers.IO) {
            val chapters : List<Chapter> = recordsUseCase.getRecords.fromId(id)
            objectUseCase.updateObject(chapters.onEach { it.currentSeen = 0 })
        }

    fun getProfileIsFavorite(id : Int) : AnimeProfile? = runBlocking { profileUseCase.getProfile(id) }

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectUseCase.updateObject(chapter) }
}