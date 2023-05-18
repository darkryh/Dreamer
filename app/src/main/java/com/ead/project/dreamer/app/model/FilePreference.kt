package com.ead.project.dreamer.app.model

data class FilePreference(
    val mainFolderPath : String,
    val seriesFolderPath : String,
    val isMainFolderCreated : Boolean,
    val isSeriesFolderCreated : Boolean,
    val isFirstTimeChecking : Boolean
)
