package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class IsInDownloadProgress @Inject constructor(
    private val getDownloads: GetDownloads,
    private val getTempDownloads: GetTempDownloads,
    private val isInDownloadManagerProgress: IsInDownloadManagerProgress,
) {

    operator fun invoke(chapter: Chapter) : Boolean =
    getDownloads.toIdList().contains(chapter.id) || getTempDownloads().contains(chapter)
            || isInDownloadManagerProgress(chapter)
}