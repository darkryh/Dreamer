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

    @Query("select * from anime_chapter_table  where idProfile =:id and currentSeen > 0")
    suspend fun getChaptersRecordsFromId(id : Int) : List<Chapter>

    @Query("select * from anime_chapter_table where title='' or cover='' or number=-1 or reference='' GROUP by idProfile")
    suspend fun getChaptersToFix() : List<Chapter>

    @Query("select * from(select count(*) as id from anime_chapter_table where idProfile =:id) as a union all select * from(select id from anime_chapter_table where idProfile =:id order by number desc Limit 1) as b")
    suspend fun getPreparation(id : Int) : List<Int>

    @Query("select * from anime_chapter_table  where idProfile =:id order by number desc")
    fun getFlowChaptersFromProfile(id : Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id and number >=:start and number <=:end order by number desc ")
    fun getFlowChaptersFromProfileInSections(id : Int,start : Int,end: Int) : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table  where idProfile =:id order by number asc")
    fun getFlowChaptersFromProfileAsc(id : Int) : Flow<List<Chapter>>

    @Query("select * from(select * from (select * from anime_chapter_table where currentSeen > 0 order by lastSeen desc) as X group by X.title) as T order by lastSeen desc")
    fun getFlowDataRecords() : Flow<List<Chapter>>

    @Query("select * from anime_chapter_table where title=:title and number=:chapterNumber")
    suspend fun getChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Chapter?

    @Query("select * from anime_chapter_table where title=:title and number=:chapterNumber")
    fun getFlowChapterFromTitleAndNumber(title : String, chapterNumber: Int) : Flow<Chapter?>

    @Query("select * from ( select * from anime_chapter_table where idProfile=:id order by number asc ) as x where downloadState='${Chapter.DOWNLOAD_STATUS_INITIALIZED}' or downloadState='${Chapter.DOWNLOAD_STATUS_FAILED}'")
    suspend fun getNotDownloadedChaptersFromId(id: Int) : List<Chapter>
}