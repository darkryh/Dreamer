package com.ead.project.dreamer.presentation.chapter.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.data.server.Server
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
    private val recordsUseCase: RecordsUseCase,
    val downloadUseCase: DownloadUseCase
): ViewModel() {

    fun setDownloadMode(value : Boolean) {
        Server.setDownloading(value)
    }

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
            val chapters: List<Chapter> = recordsUseCase.getRecords.fromId(id)
            val chaptersToUpdate: List<Chapter> = chapters.map { chapter ->
                chapter.copy(currentProgress = 0)
            }
            objectUseCase.updateObject(chaptersToUpdate)
        }

    fun getProfileIsFavorite(id : Int) : AnimeProfile? = runBlocking { profileUseCase.getProfile(id) }

    fun updateChapter(chapter: Chapter) =
        viewModelScope.launch (Dispatchers.IO) { objectUseCase.updateObject(chapter) }
}