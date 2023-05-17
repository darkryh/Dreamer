package com.ead.project.dreamer.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.domain.PreferenceUseCase
import com.ead.project.dreamer.domain.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    val preferenceUseCase: PreferenceUseCase
): ViewModel() {

    fun getLikedDirectory() : LiveData<List<AnimeProfile>> = profileUseCase.getLikedProfiles.livedata()

    fun getFilterDirectory(state : String?= null,genre : String?= null) : LiveData<List<AnimeProfile>>
    = profileUseCase.getLikedProfiles.livedata( state, genre)

}