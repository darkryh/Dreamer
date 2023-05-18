package com.ead.project.dreamer.app.model.scraper

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
        const val INSTANCE = "NEWS_ITEM_WEB_SCRAP"
    }
}
