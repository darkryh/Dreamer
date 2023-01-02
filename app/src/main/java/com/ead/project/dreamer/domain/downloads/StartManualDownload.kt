package com.ead.project.dreamer.domain.downloads

import android.content.Context
import com.ead.project.dreamer.R
import com.ead.project.dreamer.app.DreamerApp
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.utils.ThreadUtil
import javax.inject.Inject

class StartManualDownload @Inject constructor(
    private val context: Context,
    private val getDownloads: GetDownloads,
    private val filterDownloads: FilterDownloads,
    private val removeDownload: RemoveDownload
) {

    operator fun invoke(chapter: Chapter,mContext: Context) {
        when (chapter.downloadState) {
            Chapter.DOWNLOAD_STATUS_INITIALIZED -> {
                if (filterDownloads.isDataNotInDownloadProgress(chapter))
                    Chapter.launchServer(mContext, chapter, true)
                else showToast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.DOWNLOAD_STATUS_RUNNING -> showToast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.DOWNLOAD_STATUS_PENDING -> showToast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.DOWNLOAD_STATUS_PAUSED -> showToast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.DOWNLOAD_STATUS_FAILED -> {
                removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                Chapter.launchServer(mContext, chapter, true)
                showToast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.DOWNLOAD_STATUS_COMPLETED -> showToast(context.getString(R.string.warning_chapter_status_completed))
        }
    }

    private fun showToast(string: String) = runOnUI { DreamerApp.showShortToast(string) }
    private fun runOnUI(task: () -> Unit) = ThreadUtil.onUi { task() }
}