package com.ead.project.dreamer.data.database.model

data class CategoryModel (
    var name : String,
    val list: MutableList<AnimeBase> = ArrayList()
)