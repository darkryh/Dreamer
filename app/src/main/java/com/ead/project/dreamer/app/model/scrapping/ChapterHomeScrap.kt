package com.ead.project.dreamer.app.model.scrapping

import android.util.Log
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class ChapterHomeScrap (
    val id : Int = 0,
    val classList : String,
    val titleContainer : String,
    val chapterCoverContainer : String,
    val chapterNumberContainer : String,
    val typeContainer : String,
    val referenceContainer : String
) {

    companion object {

        fun get() : ChapterHomeScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.CHAPTER_HOME_SCRAP), ChapterHomeScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : ChapterHomeScrap) =
            DataStore.writeStringAsync(Constants.CHAPTER_HOME_SCRAP, Gson().toJson(value))

        fun getDataFromApi(repository: AnimeRepository) : ChapterHomeScrap {
            val data = repository.getChapterHomeScrap()
            Log.d("testing", "ChapterHomeScrap: getting data")
            set(data)
            return data
        }
    }
}