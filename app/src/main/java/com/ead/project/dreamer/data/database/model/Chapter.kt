package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DiffUtilEquality
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "anime_chapter_table")
data class Chapter (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val idProfile : Int,
    val title : String,
    val chapterCover : String,
    val chapterNumber : Int,
    val reference : String,
    var currentSeen : Int = 0,
    var totalToSeen : Int = 0,
    var alreadySeen : Boolean = false,
    var lastSeen : Date = Calendar.getInstance().time
) : Parcelable, DiffUtilEquality {

    companion object {

        fun fake() : Chapter = Chapter(
            0,
            0,
            "null",
            "null",
            -1,
            "null")

        fun sameData(first: Chapter,second: Chapter) : Boolean
                = first.idProfile == second.idProfile
                && first.title == second.title
                && first.chapterCover == second.chapterCover
                && first.chapterNumber == second.chapterNumber
                && first.reference == second.reference

        fun get(): Chapter? = try {
            Gson().fromJson(DataStore.readString(Constants.CURRENT_EXECUTED_CHAPTER), Chapter::class.java)
        } catch (e : Exception) { null }

        fun set(chapter: Chapter) { DataStore.writeStringAsync(Constants.CURRENT_EXECUTED_CHAPTER,Gson().toJson(chapter)) }

        fun getCasting(): Chapter? = try {
            Gson().fromJson(DataStore.readString(Constants.CURRENT_CASTING_CHAPTER), Chapter::class.java)
        } catch (e : Exception) { null }

        fun setCasting(chapter: Chapter) { DataStore.writeStringAsync(Constants.CURRENT_CASTING_CHAPTER,Gson().toJson(chapter)) }

    }

    fun currentSeenToLong() = Tools.secondsToLong(this.currentSeen)

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapter: Chapter = other as Chapter
        return title == chapter.title
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapter: Chapter = other as Chapter
        return  chapterNumber == chapterNumber
                && chapterCover == chapter.chapterCover
                && currentSeen == chapter.currentSeen
                && reference == chapter.reference
    }
}