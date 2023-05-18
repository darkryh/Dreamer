package com.ead.project.dreamer.app.model.scraper

data class ChapterHomeScrap (
    val classList : String,
    val titleContainer : String,
    val chapterCoverContainer : String,
    val chapterNumberContainer : String,
    val typeContainer : String,
    val referenceContainer : String
) {
    companion object {
        const val INSTANCE = "CHAPTER_HOME_SCRAP"
    }
}