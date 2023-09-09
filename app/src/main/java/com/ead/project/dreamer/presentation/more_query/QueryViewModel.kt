package com.ead.project.dreamer.presentation.more_query

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.ead.project.dreamer.domain.HomeUseCase
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueryViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
    private val profileUseCase: ProfileUseCase,
    val handleChapter: HandleChapter,
    val launchDownload: LaunchDownload,
    val preferenceUseCase: PreferenceUseCase
) : ViewModel() {

    val appBuildPreferences = preferenceUseCase.appBuildPreferences

    fun getQuery(queryOption : Int) : LiveData<List<Any>> {
        return (when(queryOption) {
            QueryActivity.QUERY_OPTION_CHAPTER_HOME -> homeUseCase.getHomeList.livedata()
            QueryActivity.QUERY_OPTION_PROFILE -> profileUseCase.getMostViewedProfiles.livedata()
            else -> homeUseCase.getHomeList.livedata()
        }).map {  it }
    }
}