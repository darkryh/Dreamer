package com.ead.project.dreamer.app.model

import com.ead.project.dreamer.data.database.model.ChapterHome

data class HomePreference(
    val list : List<ChapterHome>,
    val notifyingIndex : Int
)
