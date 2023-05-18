package com.ead.project.dreamer.presentation.download

import androidx.lifecycle.ViewModel
import com.ead.project.dreamer.app.data.downloads.DownloadStore
import com.ead.project.dreamer.domain.ChapterUseCase
import com.ead.project.dreamer.domain.servers.HandleChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    val downloadStore : DownloadStore,
    val chapterUseCase: ChapterUseCase,
    val handleChapter: HandleChapter
) : ViewModel()