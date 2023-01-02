package com.ead.project.dreamer.domain.downloads

import com.ead.project.dreamer.data.database.model.Chapter
import javax.inject.Inject

class GetTempDownloads @Inject constructor() {

    private val list : MutableList<Chapter> = mutableListOf()

    operator fun invoke() = list

    fun getChapter() : Chapter? = try { list.removeFirst() } catch (e : Exception) { null }
}