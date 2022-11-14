package com.ead.project.dreamer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoModel (
    val quality : String,
    val directLink : String
) : Parcelable