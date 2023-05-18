package com.ead.project.dreamer.domain.databasequeries

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.domain.PreferenceUseCase
import javax.inject.Inject

class GetProfilePlayerRecommendations @Inject constructor(
    private val repository: AnimeRepository,
    private val context : Context,
    preferenceUseCase: PreferenceUseCase
) {

    private val appBuildPreferences = preferenceUseCase.appBuildPreferences

    fun livedata(animeProfile: AnimeProfile) : LiveData<List<AnimeProfile>> {
        val genres = animeProfile.genres.ifEmpty {
            context.resources.getStringArray(R.array.genres_list).asList()
        }
        return if (appBuildPreferences.isUnlockedVersion())
            repository.getFlowRandomProfileListFrom(genres.random(), animeProfile)
        else {
            val genre = filterGenreByGooglePolicies(genres)
            repository.getFlowRandomProfileListCensuredFrom(genre, animeProfile)
        }.asLiveData()
    }


    private fun filterGenreByGooglePolicies(genres: List<String>): String =
        genres.filter { it != AnimeProfile.TYPE_ECCHI }.random()

}