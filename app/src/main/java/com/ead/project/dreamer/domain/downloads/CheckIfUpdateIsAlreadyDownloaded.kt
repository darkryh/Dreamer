package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.utils.DirectoryManager
import javax.inject.Inject

class CheckIfUpdateIsAlreadyDownloaded @Inject constructor(
) {

    operator fun invoke(): Boolean = DirectoryManager.getUpdateFile().exists()
}