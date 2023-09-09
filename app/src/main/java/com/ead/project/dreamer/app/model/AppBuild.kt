package com.ead.project.dreamer.app.model

import android.os.Parcelable
import com.ead.project.dreamer.data.models.Update
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppBuild(
    val minVersion: Double,
    val update : Update,
    val resumedVersionNotes : String?,
    val versionNotes : String?,
    val downloadReference : String,
    val currentVersionDeprecated : Boolean = false,
    val isUnlockedVersion : Boolean = true,
    val isDarkTheme : Boolean = false
) : Parcelable