package com.ead.project.dreamer.app.model.scraper

data class NewsItemScrap(
    val classList : String,
    val titleContainer : String,
    val coverContainer : String,
    val dateContainer : String,
    val typeContainer : String,
    val referenceContainer : String
) {
    companion object {
        const val INSTANCE = "NEWS_ITEM_SCRAP"
    }
}