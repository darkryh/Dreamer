package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.downloads.*
import javax.inject.Inject

class DownloadManager @Inject constructor(
    val startDownload: StartDownload,
    val startManualDownload: StartManualDownload,
    val launchManualDownload: LaunchManualDownload,
    val launchUpdate: LaunchUpdate,
    val filterDownloads: FilterDownloads,
    val checkIfUpdateIsAlreadyDownloaded: CheckIfUpdateIsAlreadyDownloaded,
    val removeDownload: RemoveDownload
)