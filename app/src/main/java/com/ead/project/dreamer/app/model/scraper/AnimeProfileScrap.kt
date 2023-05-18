package com.ead.project.dreamer.app.model.scraper


data class AnimeProfileScrap (
    val coverPhotoContainer: String,
    val profilePhotoContainer: String,
    val titleContainer: String,
    val titleAlternativeContainer: String,
    val ratingContainer: String,
    val stateContainer: String,
    val descriptionContainer: String,
    val dateContainer: String,
    val genresContainer: String,
    val sizeContainer: String
) {
    companion object {
        const val INSTANCE = "ANIME_PROFILE_SCRAP"
    }
}