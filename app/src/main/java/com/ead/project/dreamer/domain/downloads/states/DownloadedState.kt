package com.ead.project.dreamer.domain.downloads.states

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.files.FilesPreferences
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.error
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.domain.downloads.EnqueueDownload
import com.ead.project.dreamer.domain.downloads.LaunchDownload
import javax.inject.Inject

class DownloadedState @Inject constructor (
    private val filesPreferences: FilesPreferences,
    private val launchDownload: LaunchDownload,
    private val enqueueDownload: EnqueueDownload
) {

    operator fun invoke(context: Context, chapter: Chapter) {
        if (filesPreferences.getChapterFile(chapter).exists()) {
            context.error(context.getString(R.string.warning_chapter_status_completed))
        }
        else {
            launchDownload(chapter)
        }
    }

    operator fun invoke(context: Context, chapter: Chapter,url : String) {
        if (filesPreferences.getChapterFile(chapter).exists()) {
            context.toast(context.getString(R.string.warning_chapter_status_completed))
        }
        else {
            enqueueDownload(chapter,url)
        }
    }
}