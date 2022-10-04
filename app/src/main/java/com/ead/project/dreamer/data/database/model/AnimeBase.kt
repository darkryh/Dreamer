package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ead.project.dreamer.data.utils.DiffUtilEquality
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "anime_base_table")
data class AnimeBase (
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title : String,
    val cover : String,
    val reference : String,
    val type : String,
    val year : Int
) : Parcelable,DiffUtilEquality {

    fun isWorking() = title.isNotEmpty() && cover.isNotEmpty()
            && reference.isNotEmpty() && type.isNotEmpty() && year != -1

    //fun isNotWorking () = !isWorking()

    override fun equalsHeader(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val animeBase: AnimeBase = other as AnimeBase
        return title == animeBase.title
    }

    override fun equalsContent(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val animeBase: AnimeBase = other as AnimeBase
        return title == animeBase.title
                && cover == animeBase.cover
                && reference == animeBase.reference
    }


}