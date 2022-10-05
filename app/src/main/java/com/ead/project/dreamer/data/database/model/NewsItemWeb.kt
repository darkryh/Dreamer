package com.ead.project.dreamer.data.database.model

data class NewsItemWeb(
    val title: String,
    val author : String,
    val cover: String,
    val type : String,
    val date : String,
    val bodyList : List<Any>,
    val photoAuthor : String,
    val authorFooter : String,
    val authorWords : String
) {

    companion object {
        fun fake () : NewsItemWeb = NewsItemWeb("","","","","", emptyList(),
            "","","")
    }
}