package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import com.ead.project.dreamer.data.database.model.ChapterHome
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterHomeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll (list : List<ChapterHome>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateHome(list: List<ChapterHome>)

    @Query("select * from anime_chapter_home_table order by id desc")
    suspend fun getChapterHomeList() : MutableList<ChapterHome>

    @Query("select * from anime_chapter_home_table order by id desc")
    fun getFlowDataList() : Flow<List<ChapterHome>>

    @Query("select * from anime_chapter_home_table " +
            "where type!='${AnimeProfile.TYPE_UNCENSORED}' order by id desc")
    fun getFlowDataListCensured() : Flow<List<ChapterHome>>

    @Query("select * from anime_chapter_home_table where chapterNumber <= 1")
    suspend fun getReleaseList() : List<ChapterHome>
}