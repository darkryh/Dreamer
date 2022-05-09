package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.commons.Constants
import com.ead.project.dreamer.data.database.model.AnimeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(animeProfile: AnimeProfile)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(animeProfile: AnimeProfile)

    @Query("select * from anime_profile_table")
    fun getProfileList() : List<AnimeProfile>

    @Query("select * from anime_profile_table where state = '${Constants.PROFILE_RELEASE_STATE}'")
    fun getProfileReleases() : List<AnimeProfile>

    @Query("select * from anime_profile_table where id=:id")
    fun getFlowProfile(id : Int) : Flow<AnimeProfile?>

    @Query("select * from anime_profile_table where rawGenres like '%' || :rawGenre || '%'  and rating>=:rating and id !=:id order by RANDOM() limit :limit")
    fun getFlowProfileRandomListFrom(rawGenre : String, rating : Float, id: Int,limit : Int) : Flow<List<AnimeProfile>>

    @Query("select * from (select * from anime_profile_table where rawGenres not like '%' || '${Constants.TYPE_ECCHI}' || '%' and rawGenres not like '%' || '${Constants.TYPE_BOYS_LOVE}' || '%') as T where rawGenres like '%' || :rawGenre || '%'  and rating>=:rating and id !=:id order by RANDOM() limit :limit")
    fun getFlowProfileRandomListCensuredFrom(rawGenre : String, rating : Float, id: Int,limit: Int) : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table where state = '${Constants.PROFILE_RELEASE_STATE}' and rating >= 4.7 order by RANDOM() limit 10")
    fun getFlowProfileRandomRecommendationsList() : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table where state = '${Constants.PROFILE_RELEASE_STATE}' and rating >= 4.7 and rawGenres not like '%' || '${Constants.TYPE_ECCHI}' || '%' order by RANDOM() limit 10")
    fun getFlowProfileRandomRecommendationsListCensured() : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table where isFavorite = 1 order by title asc")
    fun getLikeFlowDataList() : Flow<List<AnimeProfile>>
}