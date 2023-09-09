package com.ead.project.dreamer.app.model

data class AutomaticServerPreference(
    val internalServerList : List<String>,
    val externalServerList : List<String>,
    val downloadServerList : List<String>
)