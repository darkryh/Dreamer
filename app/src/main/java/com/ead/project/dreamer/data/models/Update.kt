package com.ead.project.dreamer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Update(
    val title : String,
    val version : Double
) : Parcelable
