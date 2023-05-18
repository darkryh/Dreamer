package com.ead.project.dreamer.presentation.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ead.project.dreamer.app.data.preference.Settings
import com.ead.project.dreamer.app.model.Requester
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.data.utils.AdManager
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val directoryUseCase: DirectoryUseCase,
    private val homeUseCase: HomeUseCase,
    val handleChapter: HandleChapter,
    val launchDownload: LaunchDownload,
    val adManager : AdManager,
    preferenceUseCase: PreferenceUseCase,
): ViewModel() {

    val appBuildPreferences = preferenceUseCase.appBuildPreferences
    private val playerPreferences = preferenceUseCase.playerPreferences
    private val preferences = preferenceUseCase.preferences

    val playerPreference = playerPreferences.preference

    fun resetRequestingProfile() {
        viewModelScope.launch {
            playerPreferences.setRequestingProfile(Requester.Deactivate)
        }
    }
    fun getDirectoryState() : Flow<Boolean> = preferences.getBooleanFlow(Settings.SYNC_DIRECTORY_PROFILE)

    fun getDirectory(title : String) : LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,false)

    fun getFullDirectory(title: String): LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,true)

    fun getChaptersHome() : LiveData<List<ChapterHome>> = homeUseCase.getHomeList.livedata()
}