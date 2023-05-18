package com.ead.project.dreamer.domain.downloads

import android.content.Context
import android.widget.Toast
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.data.system.extensions.toast
import com.ead.project.dreamer.data.utils.Thread
import com.ead.project.dreamer.domain.servers.LaunchServer
import javax.inject.Inject

class StartManualDownload @Inject constructor(
    private val context: Context,
    private val isInDownloadProgress: IsInDownloadProgress,
    private val launchServer: LaunchServer,
    private val getDownloads: GetDownloads,
    private val removeDownload: RemoveDownload
) {

    operator fun invoke(chapter: Chapter,mContext: Context) {
        when (chapter.state) {
            Chapter.STATUS_STREAMING -> {
                if (!isInDownloadProgress(chapter))
                    launchServer(mContext, chapter,true)
                else toast(context.getString(R.string.warning_chapter_status_in_progress))
            }
            Chapter.STATUS_RUNNING -> toast(context.getString(R.string.warning_chapter_status_in_progress))
            Chapter.STATUS_PENDING -> toast(context.getString(R.string.warning_chapter_status_pending))
            Chapter.STATUS_PAUSED -> toast(context.getString(R.string.warning_chapter_status_paused))
            Chapter.STATUS_FAILED -> {
                //removeDownload(getDownloads().singleOrNull{ it.second == chapter.id })
                launchServer(mContext, chapter,true)
                toast(context.getString(R.string.warning_chapter_status_failed))
            }
            Chapter.STATUS_DOWNLOADED -> toast(context.getString(R.string.warning_chapter_status_completed))
        }
    }

    private fun toast(string: String) = runOnUI { context.toast(string,Toast.LENGTH_SHORT) }
    private fun runOnUI(task: () -> Unit) = Thread.onUi { task() }
}