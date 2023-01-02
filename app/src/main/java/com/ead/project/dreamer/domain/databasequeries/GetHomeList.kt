package com.ead.project.dreamer.domain.databasequeries

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.ChapterHome
import javax.inject.Inject

class GetHomeList @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun invoke() : List<ChapterHome> = repository.getChaptersHome()

    fun livedata() : LiveData<List<ChapterHome>> =
        if (Constants.isGooglePolicyNotActivate()) { repository.getFlowChapterHome() }
        else { repository.getFlowChapterHomeCensured() }.asLiveData()
}