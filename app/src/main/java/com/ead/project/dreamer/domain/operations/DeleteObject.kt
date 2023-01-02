package com.ead.project.dreamer.domain.operations

import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class DeleteObject @Inject constructor(
    private val repository: AnimeRepository
) {

    suspend operator fun <T : Any> invoke(mObject : T) {
        when(mObject) {
            is Chapter -> repository.deleteChaptersById(mObject.idProfile)
        }
    }
}