package com.ead.project.dreamer.data.database.model

import android.os.Parcelable
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.utils.DataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoModel (
    val quality : String,
    val directLink : String
) : Parcelable {

    companion object {

        fun getList(): List<VideoModel> = try { Gson().fromJson(DataStore.readString(Constants.CURRENT_EXECUTED_PLAYLIST)
            ,object : TypeToken<List<VideoModel?>?>() {}.type)
        } catch (e : Exception) { emptyList() }

        fun setList(list: List<VideoModel>) { DataStore.writeString(Constants.CURRENT_EXECUTED_PLAYLIST,Gson().toJson(list)) }
    }
}
