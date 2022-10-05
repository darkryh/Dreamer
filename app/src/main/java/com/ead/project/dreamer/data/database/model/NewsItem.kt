package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.utils.DiffUtilEquality
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "anime_news_item_table")
data class NewsItem (
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val title : String,
    val cover : String,
    val type : String,
    val date : String,
    val reference: String
)    : Parcelable, DiffUtilEquality {

    companion object {
        fun fake() : NewsItem = NewsItem(
            0,
            "null",
            "null",
            "null",
            "null",
            "null")
    }


    override fun equalsHeader(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        val newsItem: NewsItem = other as NewsItem
        return title == newsItem.title
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        val newsItem: NewsItem = other as NewsItem
        return title == newsItem.title &&
                type == newsItem.type &&
                date == newsItem.date
    }

    override fun hashCode(): Int {
        return Objects.hash(title,type,date)
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val newsItem: NewsItem = other as NewsItem
        return title == newsItem.title &&
                type == newsItem.type &&
                date == newsItem.date
    }
}