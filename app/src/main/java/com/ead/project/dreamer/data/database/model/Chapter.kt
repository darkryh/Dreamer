package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.app.data.files.Files
import com.ead.project.dreamer.app.data.util.TimeUtil
import com.ead.project.dreamer.app.data.util.system.delete
import com.ead.project.dreamer.data.utils.LocalServer
import com.ead.project.dreamer.data.utils.ui.mechanism.EqualsDiffUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.math.roundToInt

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
    val state : Int = STATUS_STREAMING,
    val currentProgress : Int = 0,
    val totalProgress : Int = 0,
    val isContentConsumed : Boolean = false,
    val lastDateSeen : Date = TimeUtil.getNow()
) : Parcelable, EqualsDiffUtil {

    companion object {
        const val STATUS_STREAMING = 0
        const val STATUS_DOWNLOADED = 1
        const val STATUS_PAUSED = 2
        const val STATUS_RUNNING = 3
        const val STATUS_PENDING = 4
        const val STATUS_FAILED = -1
        const val STATUS_ERROR = -444

        const val PLAY_VIDEO_LIST = "PLAY_VIDEO_LIST"

        const val REQUESTED = "REQUESTED_CHAPTER"
        const val CONTENT_IS_DIRECT = "CHAPTER_CONTENT_IS_DIRECT"
        const val PREVIOUS_CASTING_MEDIA = "PREVIOUS_CASTING_MEDIA"
    }

    @IgnoredOnParcel
    val isMediaInitialized get() = currentProgress > 0L && totalProgress > 0L

    @IgnoredOnParcel
    val isMediaWatched get() = currentProgress >= (totalProgress * 0.91).roundToInt() && isMediaInitialized

    fun getLocalReference() : String = LocalServer.address + "/${routeName()}"

    fun routeName() : String = title
        .delete(" ")
        .delete("(")
        .delete(")")
        .delete(":")
        .lowercase() + number

    fun isDownloaded() : Boolean = state == STATUS_DOWNLOADED && Files.getFile(this).exists()

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
                && currentProgress == chapter.currentProgress
                && reference == chapter.reference
    }
}