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
    fun updateNormal(chapter: Chapter)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateChapters(chapters: List<Chapter>)

    @Query("delete from anime_chapter_table  where idProfile=:id")
    suspend fun deleteChaptersById(id : Int)

    @Query("select * from anime_chapter_table  where id =:id")
    suspend fun getChapterFromId(id : Int) : Chapter?

    @Query("select * from anime_chapter_table where title='' or chapterCover='' or chapterNumber=-1 or reference='' GROUP by idProfile")
    suspend fun getChaptersToFix() : List<Chapter>

    @Query("select * from(select count(*) as id from anime_chapter_table where idProfile =:id) as a union all select * from(select id from anime_chapter_table where idProfile =:id order by chapterNumber desc Limit 1) as b")
    fun getFlowPreparation(id : Int) : Flow<List<Int>>

    @Query("select * from anime_chapter_table  where idProfile =:id order by chapterNumber desc")
    fun getFlowChaptersFromProfile(id : Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id and chapterNumber >=:start and chapterNumber <=:end order by chapterNumber desc ")
    fun getFlowChaptersFromProfileInSections(id : Int,start : Int,end: Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id order by chapterNumber asc")
    fun getFlowChaptersFromProfileAsc(id : Int) : Flow<List<Chapter>>

    @Query("select * from(select * from (select * from anime_chapter_table where currentSeen > 0 order by lastSeen desc) as X group by X.title) as T order by lastSeen desc")
    fun getFlowDataRecords() : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table where title=:title and chapterNumber=:chapterNumber")
    suspend fun getChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Chapter?

    @Query("select * from anime_chapter_table where title=:title and chapterNumber=:chapterNumber")
    fun getFlowChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Flow<Chapter?>

    @Query("select * from ( select * from anime_chapter_table where idProfile=:id order by chapterNumber asc ) as x where downloadState='${Chapter.STATUS_INITIALIZED}' or downloadState='${Chapter.STATUS_FAILED}'")
    suspend fun getNotDownloadedChaptersFromId(id: Int) : List<Chapter>
}