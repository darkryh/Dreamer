package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.widget.Toast
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.toast
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
            toast(context.getString(R.string.warning_chapter_status_starting))
        } else toast(context.getString(R.string.warning_chapter_all_downloaded))
    }

    operator fun invoke(chapter: Chapter) {
        when (chapter.downloadState) {
            Chapter.DOWNLOAD_STATUS_INITIALIZED -> {
                if (!isInDownloadProgress(chapter)) {
                    addDownload(chapter)
                    toast(context.getString(R.string.warning_chapter_status_starting))
                }
                else toast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.DOWNLOAD_STATUS_RUNNING -> toast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.DOWNLOAD_STATUS_PENDING -> toast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.DOWNLOAD_STATUS_PAUSED -> toast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.DOWNLOAD_STATUS_FAILED -> {
                removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                addDownload(chapter)
                toast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.DOWNLOAD_STATUS_COMPLETED -> {
                if (chapter.getFile().exists()) toast(context.getString(R.string.warning_chapter_status_completed))
                else {
                    addDownload(chapter)
                    toast(context.getString(R.string.warning_chapter_status_starting))
                }
            }
        }
    }

    private fun toast(string: String) = runOnUI { context.toast(string,Toast.LENGTH_SHORT) }
    private fun runOnUI(task: () -> Unit) = ThreadUtil.onUi { task() }
}