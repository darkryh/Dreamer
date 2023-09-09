package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.downloads.AddDownload
import com.ead.project.dreamer.domain.downloads.IsInParallelDownloadLimit
import com.ead.project.dreamer.domain.downloads.RemoveDownload
import javax.inject.Inject

class DownloadUseCase @Inject constructor(
    val add: AddDownload,
    val remove: RemoveDownload,
    val isInParallelLimit : IsInParallelDownloadLimit
)