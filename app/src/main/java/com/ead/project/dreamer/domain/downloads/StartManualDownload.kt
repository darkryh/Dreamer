package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.widget.Toast
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.ThreadUtil
import javax.inject.Inject

class StartManualDownload @Inject constructor(
    private val context: Context,
    private val isInDownloadProgress: IsInDownloadProgress,
    private val getDownloads: GetDownloads,
    private val removeDownload: RemoveDownload
) {

    operator fun invoke(chapter: Chapter,mContext: Context) {
        when (chapter.downloadState) {
            Chapter.DOWNLOAD_STATUS_INITIALIZED -> {
                if (!isInDownloadProgress(chapter))
                    Chapter.launchServer(mContext, chapter, true)
                else toast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.DOWNLOAD_STATUS_RUNNING -> toast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.DOWNLOAD_STATUS_PENDING -> toast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.DOWNLOAD_STATUS_PAUSED -> toast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.DOWNLOAD_STATUS_FAILED -> {
                removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                Chapter.launchServer(mContext, chapter, true)
                toast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.DOWNLOAD_STATUS_COMPLETED -> toast(context.getString(R.string.warning_chapter_status_completed))
        }
    }

    private fun toast(string: String) = runOnUI { context.toast(string,Toast.LENGTH_SHORT) }
    private fun runOnUI(task: () -> Unit) = ThreadUtil.onUi { task() }
}