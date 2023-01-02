package com.ead.project.dreamer.app.model.scrapping

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class ChapterScrap (
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

    }
}