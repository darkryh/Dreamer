package com.ead.project.dreamer.presentation.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.preference.Settings
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.ChapterHome
import com.ead.project.dreamer.domain.DirectoryUseCase
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val directoryUseCase: DirectoryUseCase,
    private val homeUseCase: HomeUseCase,
    val handleChapter: HandleChapter,
    val launchDownload: LaunchDownload,
    preferenceUseCase: PreferenceUseCase,
): ViewModel() {

    val appBuildPreferences = preferenceUseCase.appBuildPreferences
    private val preferences = preferenceUseCase.preferences

    fun getDirectoryState() : Flow<Boolean> = preferences.getBooleanFlow(Settings.SYNC_DIRECTORY_PROFILE)

    fun getDirectory(title : String) : LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,false)

    fun getFullDirectory(title: String): LiveData<List<AnimeBase>> =
        directoryUseCase.getDirectoryList.livedata(title,true)

    fun getChaptersHome() : LiveData<List<ChapterHome>> = homeUseCase.getHomeList.livedata()
}