package com.ead.project.dreamer.presentation.chapter.checker

import androidx.lifecycle.*
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.*
import com.ead.project.dreamer.domain.configurations.ConfigureChapters
import com.ead.project.dreamer.domain.configurations.ConfigureProfile
import com.ead.project.dreamer.domain.servers.LaunchVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterCheckerViewModel @Inject constructor(
    private val chapterUseCase: ChapterUseCase,
    private val profileUseCase: ProfileUseCase,
    private val directoryUseCase: DirectoryUseCase,
    private val configureProfile: ConfigureProfile,
    private val configureChapters: ConfigureChapters,
    val launchVideo: LaunchVideo,
    preferenceUseCase: PreferenceUseCase
): ViewModel() {

    val playerPreferences = preferenceUseCase.playerPreferences

    fun getChapterData(chapter : Chapter) : LiveData<Chapter?> = chapterUseCase.getChapter.livedata(chapter)

    fun getAnimeBase(title : String) : LiveData<AnimeBase?> = directoryUseCase.getDirectory.livedata(title)

    fun getAnimeProfile(id : Int) : LiveData<AnimeProfile?> = profileUseCase.getProfile.livedata(id)

    fun configureProfileData(animeProfile: AnimeProfile?,id : Int,reference: String) = configureProfile(animeProfile,id,reference)

    fun configureChaptersData(id : Int,reference: String) =
        viewModelScope.launch (Dispatchers.IO) { configureChapters(id,reference,true) }
}