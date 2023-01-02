package com.ead.project.dreamer.domain.downloads

import javax.inject.Inject

class LaunchUpdate @Inject constructor(
    private val launchDownload: LaunchDownload,
    private val installUpdate: InstallUpdate
) {

    operator fun invoke(title: String, url: String) {
        launchDownload(title, url)
        installUpdate()
    }
}