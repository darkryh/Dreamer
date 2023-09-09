package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.update.GetUpdate
import com.ead.project.dreamer.domain.update.IsAlreadyDownloaded
import javax.inject.Inject

class UpdateUseCase @Inject constructor(
    val getUpdate: GetUpdate,
    val isAlreadyDownloaded: IsAlreadyDownloaded
)