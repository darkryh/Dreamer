package com.ead.project.dreamer.app.model.scrapping

import android.util.Log
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class ChapterScrap (
    val id : Int = 0,
    val classList : String,
    val titleContainer : String,
    val coverContainer : String,
    val numberContainer : String,
    val referenceContainer : String
    ) {

    companion object {

        fun get() : ChapterScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.CHAPTER_SCRAP), ChapterScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : ChapterScrap) =
            DataStore.writeStringAsync(Constants.CHAPTER_SCRAP, Gson().toJson(value))

        fun getDataFromApi(repository: AnimeRepository) : ChapterScrap {
            val data = repository.getChapterScrap()
            Log.d("testing", "ChapterScrap: getting data")
            set(data)
            return data
        }
    }
}