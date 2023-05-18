package com.ead.project.dreamer.domain.downloads

import javax.inject.Inject

class GetDownloads @Inject constructor() {

    private var list : MutableList<Pair<Long,Int>> = mutableListOf()

    operator fun invoke() : MutableList<Pair<Long,Int>>  {
        update()
        return list
    }
    //todo storedownloads
    fun update() { list = emptyList<Pair<Long,Int>>().toMutableList()  }

    fun toIdList() : List<Int> = invoke().map { it.second }
}