package com.ead.project.dreamer.app.model

data class AppStatus(
    val id: Int,
    val minVersion: Double,
    val lastVersion : Double,
    val resumedVersionNotes : String?=null,
    val downloadReference : String
)