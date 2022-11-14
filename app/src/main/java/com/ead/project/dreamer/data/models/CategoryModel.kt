package com.ead.project.dreamer.data.models

import com.ead.project.dreamer.data.database.model.AnimeBase

data class CategoryModel (
    var name : String,
    val list: MutableList<AnimeBase> = ArrayList()
)