package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetDownloads @Inject constructor() {

    private var list : MutableList<Pair<Long,Int>> = mutableListOf()

    operator fun invoke() : MutableList<Pair<Long,Int>>  {
        update()
        return list
    }

    fun update() { list = Chapter.getDownloadList() }

    fun toIdList() : List<Int> = invoke().map { it.second }
}