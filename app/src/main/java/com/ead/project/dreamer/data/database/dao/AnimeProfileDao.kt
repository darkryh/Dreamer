package com.ead.project.dreamer.data.database.dao

import androidx.room.*
import com.ead.project.dreamer.data.database.model.AnimeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(animeProfile: AnimeProfile)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(animeProfile: AnimeProfile)

    @Query("select * from anime_profile_table")
    suspend fun getProfileList() : List<AnimeProfile>

    @Query("select * from anime_profile_table " +
            "where coverPhoto = '' or profilePhoto = '' or title = '' " +
            "or rating = -1.0 or state = '' or description = '' or date = '' or rawGenres = ''")
    suspend fun getProfilesToFix() : List<AnimeProfile>

    @Query("select * from anime_profile_table where state = '${AnimeProfile.PROFILE_RELEASE_STATE}'")
    suspend fun getProfileReleases() : List<AnimeProfile>

    @Query("select title from anime_profile_table " +
            "where isFavorite = 1 and state ='${AnimeProfile.PROFILE_RELEASE_STATE}'")
    suspend fun getFavoriteProfileReleasesTitles() : List<String>

    @Query("select * from anime_profile_table where id=:id")
    suspend fun getProfile(id : Int) : AnimeProfile?

    @Query("select * from anime_profile_table where id=:id")
    fun getFlowProfile(id : Int) : Flow<AnimeProfile?>

    @Query("select * from anime_profile_table " +
            "where rawGenres like '%' || :rawGenre || '%'" +
            "and id !=:id order by RANDOM() limit :limit")
    fun getFlowProfileRandomListFrom(rawGenre : String, id: Int,limit : Int) : Flow<List<AnimeProfile>>

    @Query("select * from (select * from anime_profile_table " +
            "where rawGenres not like '%' || '${AnimeProfile.TYPE_ECCHI}' || '%' " +
            "and rawGenres not like '%' || '${AnimeProfile.TYPE_BOYS_LOVE}' || '%') " +
            "as T where rawGenres like '%' || :rawGenre || '%'" +
            "and id !=:id order by RANDOM() limit :limit")
    fun getFlowProfileRandomListCensuredFrom(rawGenre : String, id: Int,limit: Int) : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table " +
            "where state = '${AnimeProfile.PROFILE_RELEASE_STATE}' " +
            "and coverPhoto !='' order by RANDOM() limit 10")
    fun getFlowProfileRandomRecommendationsList() : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table " +
            "where state = '${AnimeProfile.PROFILE_RELEASE_STATE}' " +
            "and rawGenres not like '%' || '${AnimeProfile.TYPE_ECCHI}' || '%' " +
            "and coverPhoto !='' order by RANDOM() limit 10")
    fun getFlowProfileRandomRecommendationsListCensured() : Flow<List<AnimeProfile>>

    @Query("select * from anime_profile_table " +
            "where isFavorite = 1 order by title asc")
    fun getLikeFlowDataList() : Flow<List<AnimeProfile>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select p.id, p.coverPhoto,p.profilePhoto,p.title,p.titleAlternate," +
            "p.rating,p.state,p.description,p.date,p.genres,p.rawGenres,p.size," +
            "p.lastChapterId,p.reference,p.isFavorite " +
            "from (select * from anime_chapter_table " +
            "where currentProgress >= totalProgress*0.75 and totalProgress != 0 " +
            "GROUP BY idProfile ORDER BY count(idProfile) desc) as c " +
            "INNER join anime_profile_table as p on c.idProfile = p.id")
    fun getFlowMostViewedSeries(): Flow<List<AnimeProfile>>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select p.id, p.coverPhoto,p.profilePhoto,p.title,p.titleAlternate," +
            "p.rating,p.state,p.description,p.date,p.genres,p.rawGenres,p.size," +
            "p.lastChapterId,p.reference,p.isFavorite " +
            "from (select * from anime_chapter_table " +
            "where currentProgress >= totalProgress*0.75 and totalProgress != 0 " +
            "GROUP BY idProfile ORDER BY count(idProfile) desc) as c " +
            "INNER join anime_profile_table as p on c.idProfile = p.id limit 8")
    fun getFlowMostViewedSeriesPreview(): Flow<List<AnimeProfile>>


    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select p.id, p.coverPhoto,p.profilePhoto,p.title,p.titleAlternate" +
            ",p.rating,p.state,p.description,p.date,p.genres,p.rawGenres,p.size" +
            ",p.lastChapterId,p.reference,p.isFavorite " +
            "from (select * from anime_chapter_table " +
            "where currentProgress >= totalProgress*0.75 and totalProgress != 0 " +
            "GROUP BY idProfile ORDER BY count(idProfile) desc) as c " +
            "INNER join anime_profile_table as p on c.idProfile = p.id")
    suspend fun getMostViewedSeries(): List<AnimeProfile>

    @Query("select * from anime_profile_table " +
            "where (rawGenres like '%' || :fMGenre || '%' or rawGenres like '%' || :sMGenre || '%' " +
            "or rawGenres like '%' || :tMGenre || '%' or rawGenres like '%' || :forMGenre || '%' " +
            "or rawGenres like '%' || :fivMGenre || '%' or rawGenres like '%' || :fSGenre || '%' " +
            "or rawGenres like '%' || :sSGenre || '%' or rawGenres like '%' || :tSGenre || '%' " +
            "or rawGenres like '%' || :tFGenre || '%') " +
            "order by random() limit :limit")
    suspend fun getRecommendations(fMGenre : String,sMGenre: String,tMGenre : String,forMGenre : String,fivMGenre : String,fSGenre : String,sSGenre : String,tSGenre :String,tFGenre : String,limit: Int=20) : List<AnimeProfile>
}