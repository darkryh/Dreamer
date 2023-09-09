package com.ead.project.dreamer.domain.downloads

import android.content.Context
import com.ead.project.dreamer.data.database.model.Chapter
import com.ead.project.dreamer.domain.downloads.states.DownloadedState
import com.ead.project.dreamer.domain.downloads.states.FailedState
import com.ead.project.dreamer.domain.downloads.states.PausedState
import com.ead.project.dreamer.domain.downloads.states.PendingState
import com.ead.project.dreamer.domain.downloads.states.RunningState
import com.ead.project.dreamer.domain.downloads.states.StreamingState
import javax.inject.Inject

class AddDownload @Inject constructor(
    private val streamingState: StreamingState,
    private val runningState: RunningState,
    private val pendingState: PendingState,
    private val pausedState: PausedState,
    private val failedState: FailedState,
    private val downloadedState: DownloadedState
) {

    operator fun invoke(context: Context,chapters : List<Chapter>)  {

    }

    operator fun invoke(context : Context,chapter: Chapter) {
        when(chapter.state) {
            Chapter.STATUS_STREAMING -> streamingState(context, chapter)
            Chapter.STATUS_RUNNING -> runningState(context)
            Chapter.STATUS_PENDING -> pendingState(context)
            Chapter.STATUS_PAUSED -> pausedState(context)
            Chapter.STATUS_FAILED -> failedState(context, chapter)
            Chapter.STATUS_DOWNLOADED -> downloadedState(context, chapter)
        }
    }

    operator fun invoke(context: Context,chapter: Chapter,url : String) {
        when(chapter.state) {
            Chapter.STATUS_STREAMING -> streamingState(context, chapter, url)
            Chapter.STATUS_RUNNING -> runningState(context)
            Chapter.STATUS_PENDING -> pendingState(context)
            Chapter.STATUS_PAUSED -> pausedState(context)
            Chapter.STATUS_FAILED -> failedState(context, chapter, url)
            Chapter.STATUS_DOWNLOADED -> downloadedState(context, chapter, url)
        }
    }
}