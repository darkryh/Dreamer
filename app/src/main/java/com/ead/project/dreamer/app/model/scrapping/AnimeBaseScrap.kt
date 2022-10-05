package com.ead.project.dreamer.app.model.scrapping

import android.util.Log
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class AnimeBaseScrap(
    val id : Int,
    val classList : String,
    val titleContainer : String,
    val imageContainer : String,
    val typeContainer : String,
    val referenceContainer : String,
    val yearContainer : String
) {

    companion object {

        fun get() : AnimeBaseScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.ANIME_BASE_SCRAP), AnimeBaseScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : AnimeBaseScrap) =
            DataStore.writeStringAsync(Constants.ANIME_BASE_SCRAP,Gson().toJson(value))


        fun getDataFromApi(repository: AnimeRepository) : AnimeBaseScrap {
            val data = repository.getAnimeBaseScrap()
            Log.d("testing", "AnimeBaseScrap: getting data")
            set(data)
            return data
        }
    }
}