package com.ead.project.dreamer.data.database.model

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.commons.Tools
import com.ead.project.dreamer.data.commons.Tools.Companion.delete
import com.ead.project.dreamer.data.commons.Tools.Companion.launchIntent
import com.ead.project.dreamer.data.models.VideoModel
import com.ead.project.dreamer.data.utils.DataStore
import com.ead.project.dreamer.data.utils.DiffUtilEquality
import com.ead.project.dreamer.data.utils.DirectoryManager
import com.ead.project.dreamer.ui.chapter.settings.ChapterSettingsFragment
import com.ead.project.dreamer.ui.menuserver.MenuServerFragment
import com.ead.project.dreamer.ui.ads.InterstitialAdActivity
import com.ead.project.dreamer.ui.player.PlayerActivity
import com.ead.project.dreamer.ui.player.PlayerExternalActivity
import com.ead.project.dreamer.ui.player.PlayerWebActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "anime_chapter_table")
data class Chapter (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val idProfile : Int,
    val title : String,
    val cover : String,
    val number : Int,
    val reference : String,
    var downloadState : Int = DOWNLOAD_STATUS_INITIALIZED,
    var currentSeen : Int = 0,
    var totalToSeen : Int = 0,
    var alreadySeen : Boolean = false,
    var lastSeen : Date = Calendar.getInstance().time,
    var selected : Boolean = false
) : Parcelable, DiffUtilEquality {

    companion object {

        const val DOWNLOAD_STATUS_INITIALIZED = 0
        const val DOWNLOAD_STATUS_COMPLETED = 1
        const val DOWNLOAD_STATUS_PAUSED = 2
        const val DOWNLOAD_STATUS_RUNNING = 3
        const val DOWNLOAD_STATUS_PENDING = 4
        const val DOWNLOAD_STATUS_FAILED = -1

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

        fun getDownloadList() : MutableList<Pair<Long,Int>> = try {
            Gson().fromJson(DataStore.readString(Constants.DOWNLOADED_CHAPTERS),
                object : TypeToken<ArrayList<Pair<Long,Int>?>?>() {}.type)
        } catch (e : Exception) { mutableListOf() }

        fun addToDownloadList(data: Pair<Long,Int>) {
            try {
                val initList = getDownloadList()
                initList.add(data)
                DataStore.writeStringAsync(Constants.DOWNLOADED_CHAPTERS,
                    Gson().toJson(initList))
            } catch (e : Exception) { e.printStackTrace() }
        }

        fun removeFromDownloadList(data: Pair<Long,Int>) {
            try {
                val initList = getDownloadList()
                initList.remove(data)
                DataStore.writeStringAsync(Constants.DOWNLOADED_CHAPTERS,
                    Gson().toJson(initList))
            } catch (e : Exception) { e.printStackTrace() }
        }

        fun manageVideo(context: Context, chapter: Chapter) {
            if (chapter.isNotDownloaded()) launchServer(context, chapter)
            else launchOfflineVideo(context as Activity, chapter, chapter.toVideoModelArray())
        }

        fun launchServer(context: Context, chapter: Chapter, isDownloadMode : Boolean = false) {
            if (!DataStore.readBoolean(Constants.WORK_PREFERENCE_CLICKED_CHAPTER)) {
                DataStore.writeBooleanAsync(Constants.WORK_PREFERENCE_CLICKED_CHAPTER,true)
                val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
                val data = Bundle()
                data.apply {
                    putParcelable(Constants.REQUESTED_CHAPTER, chapter)
                    putBoolean(Constants.IS_DATA_FOR_DOWNLOADING_MODE,isDownloadMode)
                }
                val chapterMenu = MenuServerFragment()
                chapterMenu.apply {
                    arguments = data
                    show(fragmentManager, Constants.MENU_PLAYER_FRAGMENT)
                }
            }
        }

        private fun launchOfflineVideo(activity: Activity, chapter: Chapter, playList: List<VideoModel>, isDirect : Boolean = true) {
            val isExternalPlayerMode = Constants.isExternalPlayerMode()
            if (Constants.isAdInterstitialTime(isDirect)) {
                launchIntent(activity, chapter, InterstitialAdActivity::class.java, playList, isDirect)
                Constants.resetCountedAds()
            } else {
                if (isDirect) {
                    if (!isExternalPlayerMode) launchIntent(activity, chapter, PlayerActivity::class.java, playList)
                    else launchIntent(activity, chapter, PlayerExternalActivity::class.java, playList)
                }
                else launchIntent(activity, chapter, PlayerWebActivity::class.java, playList)
            }
        }

        fun callInAdapterSettings(context : Context, chapter: Chapter, isChapter : Boolean = true,isRecord : Boolean = false) {
            val fragmentManager: FragmentManager = (context as FragmentActivity).supportFragmentManager
            val data = Bundle()
            data.apply {
                putParcelable(Constants.REQUESTED_CHAPTER, chapter)
                putBoolean(Constants.IS_CORRECT_DATA_FROM_CHAPTER_SETTINGS,isChapter)
                putBoolean(Constants.IS_CORRECT_DATA_FROM_RECORDS_SETTINGS,isRecord)
            }
            val chapterMenu = ChapterSettingsFragment()
            chapterMenu.apply {
                arguments = data
                show(fragmentManager, Constants.MENU_CHAPTER_SETTINGS)
            }
        }

        fun fake() : Chapter = Chapter(
            0,
            0,
            "null",
            "null",
            -1,
            "null")

    }

    fun needsToUpdate() : Boolean = currentSeen > 0L

    private fun toVideoModelArray() : ArrayList<VideoModel> =
        arrayListOf(VideoModel("default",getDownloadedRouteReference()))

    fun getDownloadedRouteReference() : String = DirectoryManager.getChapterFolder(this)

    fun getFile() : File = File(getDownloadedRouteReference())

    fun getWebReference() : String = Tools.getWebServerAddress() + "/${routeName()}"

    fun routeName() : String = title
        .delete(" ")
        .delete("(")
        .delete(")")
        .delete(":")
        .lowercase() + number

    fun isDownloaded() : Boolean =
        downloadState == DOWNLOAD_STATUS_COMPLETED && getFile().exists()

    fun isNotDownloaded() : Boolean = !isDownloaded()

    private fun isWorking() : Boolean = title.isNotEmpty() && cover.isNotEmpty()
            && number != -1 && reference.isNotEmpty()

    fun isNotWorking () : Boolean = !isWorking()

    fun currentSeenToLong() = Tools.secondsToLong(this.currentSeen)

    fun sameData(other : Chapter) : Boolean
            = this.idProfile == other.idProfile
            && this.title == other.title
            && this.cover == other.cover
            && this.number == other.number
            && this.reference == other.reference

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
        return number == chapter.number
                && cover == chapter.cover
                && currentSeen == chapter.currentSeen
                && reference == chapter.reference
                && selected == chapter.selected
    }
}