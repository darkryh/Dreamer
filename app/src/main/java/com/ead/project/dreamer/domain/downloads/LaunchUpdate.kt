package com.ead.project.dreamer.domain.downloads

import javax.inject.Inject

class LaunchUpdate @Inject constructor(
    private val createDownload: CreateDownload,
    private val installUpdate: InstallUpdate
) {

    operator fun invoke(title: String, url: String) {
        createDownload(title, url)
        installUpdate()
    }
}