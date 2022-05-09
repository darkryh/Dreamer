package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.database.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChapters(chapterList: List<Chapter>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(chapter: Chapter)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateChapters(chapters: List<Chapter>)

    @Query("select * from anime_chapter_table  where id =:id")
    fun getChapterFromId(id : Int) : Chapter?

    @Query("select * from(select count(*) as id from anime_chapter_table where idProfile =:id) as a union all select * from(select id from anime_chapter_table where idProfile =:id order by chapterNumber desc Limit 1) as b")
    fun getFlowPreparation(id : Int) : Flow<List<Int>>

    @Query("select * from anime_chapter_table  where idProfile =:id order by chapterNumber desc")
    fun getFlowChaptersFromProfile(id : Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id and chapterNumber >=:start and chapterNumber <=:end order by chapterNumber desc ")
    fun getFlowChaptersFromProfileInSections(id : Int,start : Int,end: Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id order by chapterNumber asc")
    fun getFlowChaptersFromProfileAsc(id : Int) : Flow<List<Chapter>>

    @Query("select * from(select * from anime_chapter_table where currentSeen > 0 order by lastSeen asc) as X group by X.title")
    fun getRecords() : List<Chapter>

    @Query("select * from(select * from (select * from anime_chapter_table where currentSeen > 0 order by lastSeen asc) as X group by X.title) as T order by lastSeen desc")
    fun getFlowDataRecords() : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table where title=:title and chapterNumber=:chapterNumber")
    fun getChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Chapter?

    @Query("select * from anime_chapter_table where title=:title and chapterNumber=:chapterNumber")
    fun getFlowChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Flow<Chapter?>


}