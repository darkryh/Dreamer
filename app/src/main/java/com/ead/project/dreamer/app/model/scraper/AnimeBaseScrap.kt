package com.ead.project.dreamer.app.model.scraper

data class AnimeBaseScrap(
    val classList : String,
    val titleContainer : String,
    val imageContainer : String,
    val typeContainer : String,
    val referenceContainer : String,
    val yearContainer : String
) {

    companion object {
        const val INSTANCE = "ANIME_BASE_SCRAP"
    }
}