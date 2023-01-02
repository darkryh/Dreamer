package com.ead.project.dreamer.app.model.scrapping

import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson

data class NewsItemWebScrap(
    val headerContainer :String,
    val titleContainer : String,
    val authorContainer : String,
    val typeContainer : String,
    val dateContainer : String,
    val coverContainer : String,
    val bodyContainer : String,
    val footerContainer :String,
    val photoAuthorContainer : String,
    val authorFooter : String,
    val authorWords : String
    ) {

    companion object {

        fun get() : NewsItemWebScrap? = try {
            Gson().fromJson(DataStore.readString(Constants.NEWS_ITEM_WEB_SCRAP), NewsItemWebScrap::class.java)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }

        fun set(value : NewsItemWebScrap) =
            DataStore.writeStringAsync(Constants.NEWS_ITEM_WEB_SCRAP, Gson().toJson(value))

    }
}
