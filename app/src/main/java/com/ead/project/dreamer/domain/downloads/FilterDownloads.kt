package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.commons.Tools.Companion.notContains
import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class FilterDownloads @Inject constructor(
    private val getDownloads: GetDownloads,
    private val getTempDownloads: GetTempDownloads,
    private val isInDownloadManagerProgress: IsInDownloadManagerProgress,
) {

    operator fun invoke(chapters : List<Chapter>) : List<Chapter> {
        val tempList : MutableList<Chapter> = mutableListOf()
        getDownloads.update()
        for (data in chapters.filtered()) if (isDataNotInDownloadProgress(data)) tempList.add(data)
        return tempList
    }

    private fun List<Chapter>.filtered() : List<Chapter> =
        filter { it.downloadState == Chapter.DOWNLOAD_STATUS_INITIALIZED
                || it.downloadState == Chapter.DOWNLOAD_STATUS_FAILED }

    fun isDataNotInDownloadProgress(chapter: Chapter) =
        getDownloads.toIdList().notContains(chapter.id) && getTempDownloads().notContains(chapter)
                && !isInDownloadManagerProgress(chapter)

}