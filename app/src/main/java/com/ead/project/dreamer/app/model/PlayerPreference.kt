package com.ead.project.dreamer.app.model

import com.ead.project.dreamer.data.database.model.Chapter

data class PlayerPreference(
    val isInExternalMode : Boolean,
    val isInPictureInPictureMode : Boolean,
    val chapter : Chapter?,
    val castingChapter: Chapter?
)
