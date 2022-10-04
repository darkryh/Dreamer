package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeBase
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeBaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll (list : List<AnimeBase>)

    @Update
    suspend fun update(animeBase: AnimeBase)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateDirectory(list: List<AnimeBase>)

    @Query("select * from anime_base_table")
    suspend fun getList() : List<AnimeBase>

    @Query("select * from anime_base_table where id=:id")
    suspend fun getById(id: Int) : AnimeBase

    @Query("select * from anime_base_table where title like '%' || :title || '%' order by title asc")
    fun getFlowDataListByName(title : String) : Flow<List<AnimeBase>>

    @Query("select * from anime_base_table where title like '%' || :title || '%' and type not like '%' || '${Constants.TYPE_UNCENSORED}' || '%' order by title asc")
    fun getFlowDataListByNameCensured(title : String) : Flow<List<AnimeBase>>

    @Query("select * from anime_base_table where title=:title")
    fun getFlowAnimeBaseFromTitle(title : String) : Flow<AnimeBase?>

    @Query("select * from anime_base_table where title=:title")
    fun checkIfExist(title : String) : Boolean
}