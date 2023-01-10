package com.ead.project.dreamer.ui.chapter.checker

import androidx.lifecycle.*
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.ConfigureChapters
import com.ead.project.dreamer.domain.configurations.ConfigureProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterCheckerViewModel @Inject constructor(
    private val chapterManager: ChapterManager,
    private val profileManager: ProfileManager,
    private val directoryManager: DirectoryManager,
    private val configureProfile: ConfigureProfile,
    private val configureChapters: ConfigureChapters,
): ViewModel() {

    fun getChapterData(chapter : Chapter) : LiveData<Chapter?> = chapterManager.getChapter.livedata(chapter)

    fun getAnimeBase(title : String) : LiveData<AnimeBase?> = directoryManager.getDirectory.livedata(title)

    fun getAnimeProfile(id : Int) : LiveData<AnimeProfile?> = profileManager.getProfile.livedata(id)

    fun configureProfileData(animeProfile: AnimeProfile?,id : Int,reference: String) = configureProfile(animeProfile,id,reference)

    fun configureChaptersData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureChapters(id,reference) }
}