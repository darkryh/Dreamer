package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.database.model.AnimeBase
import com.ead.project.dreamer.data.database.model.AnimeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeBaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll (list : List<AnimeBase>)

    @Query("select * from anime_base_table")
    suspend fun getList() : List<AnimeBase>

    @Query("select * from anime_base_table where id=:id")
    suspend fun getById(id: Int) : AnimeBase

    @Query("select * from anime_base_table " +
            "where title like '%' || :title || '%' order by title asc")
    fun getFlowDataListByName(title : String) : Flow<List<AnimeBase>>

    @Query("select * from anime_base_table " +
            "where title like '%' || :title || '%' " +
            "and type not like '%' || '${AnimeProfile.TYPE_UNCENSORED}' || '%' order by title asc")
    fun getFlowDataListByNameCensured(title : String) : Flow<List<AnimeBase>>

    @Query("select b.id, b.title,b.cover,b.reference,b.type,b.year " +
            "from (select * from anime_base_table) as b " +
            "INNER join anime_profile_table as p on b.id = p.id " +
            "where b.title like '%' || :title || '%' " +
            "or p.titleAlternate like '%' || :title || '%' order by b.title asc")
    fun getFlowDataFullListByName(title : String) : Flow<List<AnimeBase>>

    @Query("select b.id, b.title,b.cover,b.reference,b.type,b.year " +
            "from (select * from anime_base_table) as b " +
            "INNER join anime_profile_table as p on b.id = p.id " +
            "where b.title like '%' || :title || '%' " +
            "or p.titleAlternate like '%' || :title || '%' " +
            "and type not like '%' || '${AnimeProfile.TYPE_UNCENSORED}' || '%' order by b.title asc")
    fun getFlowDataFullListByNameCensured(title : String) : Flow<List<AnimeBase>>

    @Query("select * from anime_base_table where title=:title")
    fun getFlowAnimeBaseFromTitle(title : String) : Flow<AnimeBase?>

    @Query("select * from anime_base_table where title=:title")
    fun checkIfExist(title : String) : Boolean
}