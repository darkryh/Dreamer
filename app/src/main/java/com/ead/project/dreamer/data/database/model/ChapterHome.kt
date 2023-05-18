package com.ead.project.dreamer.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.utils.ui.mechanism.EqualsDiffUtil
import java.util.*

@Entity(tableName = "anime_chapter_home_table")
data class ChapterHome (
    @PrimaryKey(autoGenerate = false)
    val id : Int,
    val title : String,
    val chapterCover : String,
    val chapterNumber : Int,
    val type : String,
    val reference : String
) : EqualsDiffUtil {

    companion object {
        fun fake() : ChapterHome = ChapterHome(
            0,
            "null",
            "null",
            -1,
            "null",
            "null")

    }

    fun isWorking() = title.isNotEmpty() && chapterCover.isNotEmpty()
            && chapterNumber != -1 && type.isNotEmpty() && reference.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        val chapter: ChapterHome = other as ChapterHome
        return sameData(chapter)
    }

    override fun hashCode(): Int {
        return Objects.hash(title,chapterCover,chapterNumber,type,reference)
    }

    fun sameData(other: ChapterHome) : Boolean
            = this.title == other.title
                && this.chapterCover == other.chapterCover
                && this.chapterNumber == other.chapterNumber
                && this.type == other.type
                && this.reference == other.reference

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapter: ChapterHome = other as ChapterHome
        return title == chapter.title
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val chapter: ChapterHome = other as ChapterHome
        return sameData(chapter)
    }
}