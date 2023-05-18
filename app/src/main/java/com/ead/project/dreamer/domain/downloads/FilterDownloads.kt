package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class FilterDownloads @Inject constructor(
    private val getDownloads: GetDownloads,
    private val isInDownloadProgress: IsInDownloadProgress,
) {

    operator fun invoke(chapters : List<Chapter>) : List<Chapter> {
        val tempList : MutableList<Chapter> = mutableListOf()
        getDownloads.update()
        for (data in chapters.filtered()) if (!isInDownloadProgress(data)) tempList.add(data)
        return tempList
    }

    private fun List<Chapter>.filtered() : List<Chapter> =
        filter { it.state == Chapter.STATUS_STREAMING
                || it.state == Chapter.STATUS_FAILED }

}