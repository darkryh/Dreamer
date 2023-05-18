package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.downloads.*
import javax.inject.Inject

class DownloadUseCase @Inject constructor(
    val startDownload: StartDownload,
    val startManualDownload: StartManualDownload,
    val createManualDownload: CreateManualDownload,
    val launchUpdate: LaunchUpdate,
    val filterDownloads: FilterDownloads,
    val isDownloaded: IsDownloaded,
    val checkIfUpdateIsAlreadyDownloaded: CheckIfUpdateIsAlreadyDownloaded,
    val removeDownload: RemoveDownload
)