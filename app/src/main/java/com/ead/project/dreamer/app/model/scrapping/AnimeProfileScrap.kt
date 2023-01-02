package com.ead.project.dreamer.app.model.scrapping

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson


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

        fun get() : AnimeProfileScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.ANIME_PROFILE_SCRAP), AnimeProfileScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : AnimeProfileScrap) =
            DataStore.writeStringAsync(Constants.ANIME_PROFILE_SCRAP, Gson().toJson(value))

    }
}