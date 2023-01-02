package com.ead.project.dreamer.app.model.scrapping

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class NewsItemScrap(
    val classList : String,
    val titleContainer : String,
    val coverContainer : String,
    val dateContainer : String,
    val typeContainer : String,
    val referenceContainer : String
) {

    companion object {

        fun get() : NewsItemScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.NEWS_ITEM_SCRAP), NewsItemScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : NewsItemScrap) =
            DataStore.writeStringAsync(Constants.NEWS_ITEM_SCRAP, Gson().toJson(value))

    }
}