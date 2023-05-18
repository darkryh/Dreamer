package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.widget.Toast
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.data.files.FilesPreferences
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Thread
import javax.inject.Inject

class StartDownload @Inject constructor(
    private val context: Context,
    private val filterDownloads: FilterDownloads,
    private val isInDownloadProgress: IsInDownloadProgress,
    private val addDownload: AddDownload,
    private val removeDownload: RemoveDownload,
    private val getDownloads: GetDownloads,
    private val filesPreferences: FilesPreferences
) {

    operator fun invoke(chapters : List<Chapter>) {
        val filteredList = filterDownloads(chapters)
        if (filteredList.isNotEmpty()) {
            addDownload(filteredList)
            toast(context.getString(R.string.warning_chapter_status_starting))
        } else toast(context.getString(R.string.warning_chapter_all_downloaded))
    }

    operator fun invoke(chapter: Chapter) {
        when (chapter.state) {
            Chapter.STATUS_STREAMING -> {
                if (!isInDownloadProgress(chapter)) {
                    addDownload(chapter)
                    toast(context.getString(R.string.warning_chapter_status_starting))
                }
                else toast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.STATUS_RUNNING -> toast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.STATUS_PENDING -> toast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.STATUS_PAUSED -> toast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.STATUS_FAILED -> {
                //removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                addDownload(chapter)
                toast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.STATUS_DOWNLOADED -> {
                if (filesPreferences.getChapterFile(chapter).exists()) {
                    toast(context.getString(R.string.warning_chapter_status_completed))
                }
                else {
                    addDownload(chapter)
                    toast(context.getString(R.string.warning_chapter_status_starting))
                }
            }
        }
    }

    private fun toast(string: String) = runOnUI { context.toast(string,Toast.LENGTH_SHORT) }
    private fun runOnUI(task: () -> Unit) = Thread.onUi { task() }
}