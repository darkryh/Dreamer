package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.database.model.NewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list: List<NewsItem>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateNews(list: List<NewsItem>)

    @Query("select * from anime_news_item_table order by id desc")
    suspend fun getNewsItemList() : MutableList<NewsItem>

    @Query("select * from anime_news_item_table order by id desc")
    fun getFlowDataList() : Flow<List<NewsItem>>
}