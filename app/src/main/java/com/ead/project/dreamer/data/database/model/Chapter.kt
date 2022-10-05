package com.ead.project.dreamer.data.database.model

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DiffUtilEquality
import com.ead.project.dreamer.ui.chapter.settings.ChapterSettingsFragment
import com.ead.project.dreamer.ui.menuplayer.MenuPlayerFragment
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
    var lastSeen : Date = Calendar.getInstance().time,
    var selected : Boolean = false
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

        fun setStreamDuration(value: Int) { DataStore.writeIntAsync(Constants.CAST_STREAM_DURATION,value) }

        fun getStreamDuration(): Int? = try {
            DataStore.readInt(Constants.CAST_STREAM_DURATION)
        } catch (e : Exception) { null }

        fun callMenuInAdapter(context: Context,chapter: Chapter) {
            if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_CHAPTER)) {
                DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,true)
                val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
                val data = Bundle()
                data.apply { putParcelable(Constants.REQUESTED_CHAPTER, chapter) }
                val chapterMenu = MenuPlayerFragment()
                chapterMenu.apply {
                    arguments = data
                    show(fragmentManager, Constants.MENU_PLAYER_FRAGMENT)
                }
            }
        }

        fun callInAdapterSettings(context : Context,chapterList: List<Chapter>) {
            val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
            val data = Bundle()
            data.apply { putParcelableArrayList(Constants.REQUESTED_CHAPTER_LIST,
                chapterList as ArrayList<out Parcelable>) }
            val chapterMenu = ChapterSettingsFragment()
            chapterMenu.apply {
                arguments = data
                show(fragmentManager, Constants.MENU_CHAPTER_SETTINGS)
            }
        }
    }

    fun isWorking() = title.isNotEmpty() && chapterCover.isNotEmpty()
            && chapterNumber != -1 && reference.isNotEmpty()

    fun isNotWorking () = !isWorking()

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
        return  chapterNumber == chapter.chapterNumber
                && chapterCover == chapter.chapterCover
                && currentSeen == chapter.currentSeen
                && reference == chapter.reference
    }
}