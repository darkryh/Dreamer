package com.ead.project.dreamer.data.utils

import android.content.Context
import androidx.room.Room
import com.ead.project.dreamer.data.AnimeRepository
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.AnimeDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProvisionalRepository {

    private var database: AnimeDatabase?=null
    private var repository: AnimeRepository?=null
    private var retrofit : Retrofit?=null

    fun get(context: Context) : AnimeRepository {
        return repository?:AnimeRepository(
            getDatabase(context).chapterHomeDao(),
            getDatabase(context).animeBaseDao(),
            getDatabase(context).animeProfileDao(),
            getDatabase(context).chapterDao(),
            getDatabase(context).newsItemDao(),
            getRetrofit()
        ).also { repository = it }
    }

    private fun getDatabase(context: Context) : AnimeDatabase = database?: Room.databaseBuilder(
        context,
        AnimeDatabase::class.java,
        AnimeDatabase.DATABASE
    ).build().also { database = it }

    private fun getRetrofit() : Retrofit = retrofit?:Retrofit.Builder()
        .baseUrl(Constants.API_APP)
        .addConverterFactory(GsonConverterFactory.create())
        .build().also { retrofit = it }
}