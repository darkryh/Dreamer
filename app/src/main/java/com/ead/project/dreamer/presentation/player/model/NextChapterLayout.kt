package com.ead.project.dreamer.presentation.player.model

import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView

data class NextChapterLayout(
    val root : MaterialCardView,
    val logo : ImageView,
    val textNumber : TextView
)
