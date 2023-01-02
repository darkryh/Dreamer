package com.ead.project.dreamer.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.domain.ProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val profileManager: ProfileManager
): ViewModel() {

    fun getLikedDirectory() : LiveData<List<AnimeProfile>> = profileManager.getLikedProfiles.livedata()

    fun getFilterDirectory(state : String?= null,genre : String?= null) : LiveData<List<AnimeProfile>>
    = profileManager.getLikedProfiles.livedata( state, genre)

}