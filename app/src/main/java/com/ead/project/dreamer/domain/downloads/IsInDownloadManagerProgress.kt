package com.ead.project.dreamer.domain.downloads

import android.database.Cursor
import com.ead.project.dreamer.data.commons.Tools.Companion.downloadItem
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class IsInDownloadManagerProgress @Inject constructor(
    private val getCursorFromDownloads: GetCursorFromDownloads
) {

    operator fun invoke(chapter: Chapter) : Boolean {
        val cursor = getCursorFromDownloads()
        return checkInCursor(chapter, cursor)
    }

    operator fun invoke(chapter: Chapter,queryId : Long) : Boolean {
        val cursor = getCursorFromDownloads(queryId)
        return checkInCursor(chapter, cursor)
    }

    private fun checkInCursor(chapter: Chapter, cursor: Cursor) : Boolean {
        while (cursor.moveToNext())
            if (chapter.id == cursor.downloadItem().idReference)
                return true

        return false
    }
}