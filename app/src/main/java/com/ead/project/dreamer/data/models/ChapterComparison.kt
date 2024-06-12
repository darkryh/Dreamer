package com.ead.project.dreamer.data.models

import com.ead.project.dreamer.data.database.model.Chapter

data class ChapterComparison(
    val idProfile : Int,
    val title : String,
    val cover : String,
    val number : Int,
    val reference : String,
) {
     fun toChapter() : Chapter {
         return Chapter(
             id = 0,
             idProfile = idProfile,
             title = title,
             cover = cover,
             number = number,
             reference = reference
         )
     }
}