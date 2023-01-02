package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.commons.Tools.Companion.downloadItem
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class IsInDownloadManagerProgress @Inject constructor(
    private val getCursorFromDownloads: GetCursorFromDownloads
) {

    operator fun invoke(chapter: Chapter) : Boolean {
        val cursor = getCursorFromDownloads()
        while (cursor.moveToNext())
            if (chapter.id == cursor.downloadItem().idReference)
                return true

        return false
    }
}