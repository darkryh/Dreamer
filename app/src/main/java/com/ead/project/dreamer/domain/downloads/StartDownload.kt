package com.ead.project.dreamer.domain.downloads

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.ThreadUtil
import javax.inject.Inject

class StartDownload @Inject constructor(
    private val context: Context,
    private val filterDownloads: FilterDownloads,
    private val isInDownloadProgress: IsInDownloadProgress,
    private val addDownload: AddDownload,
    private val removeDownload: RemoveDownload,
    private val getDownloads: GetDownloads
) {

    operator fun invoke(chapters : List<Chapter>) {
        val filteredList = filterDownloads(chapters)
        if (filteredList.isNotEmpty()) {
            addDownload(filteredList)
            showToast(context.getString(R.string.warning_chapter_status_starting))
        } else showToast(context.getString(R.string.warning_chapter_all_downloaded))
    }

    operator fun invoke(chapter: Chapter) {
        when (chapter.downloadState) {
            Chapter.DOWNLOAD_STATUS_INITIALIZED -> {
                if (!isInDownloadProgress(chapter)) {
                    addDownload(chapter)
                    showToast(context.getString(R.string.warning_chapter_status_starting))
                }
                else showToast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.DOWNLOAD_STATUS_RUNNING -> showToast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.DOWNLOAD_STATUS_PENDING -> showToast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.DOWNLOAD_STATUS_PAUSED -> showToast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.DOWNLOAD_STATUS_FAILED -> {
                removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                addDownload(chapter)
                showToast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.DOWNLOAD_STATUS_COMPLETED -> {
                if (chapter.getFile().exists()) showToast(context.getString(R.string.warning_chapter_status_completed))
                else {
                    addDownload(chapter)
                    showToast(context.getString(R.string.warning_chapter_status_starting))
                }
            }
        }
    }

    private fun showToast(string: String) = runOnUI { DreamerApp.showShortToast(string) }
    private fun runOnUI(task: () -> Unit) = ThreadUtil.onUi { task() }
}