package com.ead.project.dreamer.app.model.scraper

data class ChapterScrap (
    val classList : String,
    val titleContainer : String,
    val coverContainer : String,
    val numberContainer : String,
    val referenceContainer : String
) {
    companion object {
        const val INSTANCE = "CHAPTER_SCRAP"
    }
}