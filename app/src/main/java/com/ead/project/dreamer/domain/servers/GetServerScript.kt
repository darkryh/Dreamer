package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.data.AnimeRepository
import javax.inject.Inject

class GetServerScript @Inject constructor(
    private val repository: AnimeRepository
) {

    operator fun invoke() : String = repository.getServerScript()

}