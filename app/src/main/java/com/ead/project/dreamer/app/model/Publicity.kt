package com.ead.project.dreamer.app.model

data class Publicity(
    val content: String,
    val cover: String,
    val icon: String?,
    val id: Int,
    val tags: List<String>,
    val title: String,
    val visibility: Boolean,
    val web_page: String
)