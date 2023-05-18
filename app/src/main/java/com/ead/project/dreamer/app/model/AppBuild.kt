package com.ead.project.dreamer.app.model

data class AppBuild(
    val minVersion: Double,
    val lastVersion : Double,
    val resumedVersionNotes : String?,
    val versionNotes : String?,
    val downloadReference : String,
    val currentVersionDeprecated : Boolean = false,
    val isUnlockedVersion : Boolean = true,
    val isDarkTheme : Boolean = false
)